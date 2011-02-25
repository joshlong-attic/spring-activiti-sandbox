package org.activiti.spring.components.scope;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.runtime.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.components.aop.util.DirtyMonitorProxyFactoryBean;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * binds variables to a currently executing Activiti business process -- a
 * {@link org.activiti.engine.runtime.ProcessInstance}.
 *
 * @author Josh Long
 */
public class ProcessScope implements Scope, InitializingBean, BeanFactoryPostProcessor, DisposableBean {

	public final static String PROCESS_SCOPE_NAME = "process";

	private Logger logger = Logger.getLogger(getClass().getName());

	private ProcessEngine processEngine;

	private RuntimeService runtimeService;

	/**
	 * REQUIRED
	 * <p/>
	 * this is set by reflection in the {@link org.activiti.spring.components.config.xml.ActivitiNamespaceHandler}
	 *
	 * @param processEngine the in-use {@link ProcessEngine}
	 */
	@SuppressWarnings("unused")
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
//
//	private ProcessInstance processInstance() {
//		return Context.getExecutionContext().getProcessInstance();
//	}

	public Object get(String name, ObjectFactory<?> objectFactory) {

		ExecutionEntity executionEntity = null;
		try {
			logger.fine("returning scoped object having beanName '" + name + "' for conversation ID '" + this.getConversationId() + "'. ");

			ProcessInstance processInstance = Context.getExecutionContext().getProcessInstance();
			executionEntity = (ExecutionEntity) processInstance;

			Object scopedObject = executionEntity.getVariable(name);
			if (scopedObject == null) {
				scopedObject = objectFactory.getObject();
				if (scopedObject instanceof ScopedObject) {
					ScopedObject sc = (ScopedObject) scopedObject;
					scopedObject = sc.getTargetObject();
					logger.fine("de-referencing " + ScopedObject.class.getName() + "#targetObject before persisting variable");
				}
				executionEntity.setVariable(name, scopedObject);
			}

			return createDirtyCheckingProxy(executionEntity, name, scopedObject);
		} catch (Throwable th) {
			logger.warning("couldn't return value from process scope! " + ExceptionUtils.getFullStackTrace(th));
		} finally {

			Assert.notNull(executionEntity, "executionEntity can't be null");

			if (executionEntity != null) logger.fine("set variable '" + name + "' on executionEntity# " + executionEntity.getId());
		}
		return null;
	}

	// todo
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.info("no support for registering descruction callbacks " +
				"implemented currently. registerDestructionCallback('" + name + "',callback) will do nothing.");
	}

	private String getExecutionId() {
		return ProcessInstanceScopeContextHolder.getCurrentExecutionId();
	}

	public Object remove(String name) {
		logger.fine("remove '" + name + "'");
		return runtimeService.getVariable(getExecutionId(), name);
	}

	public Object resolveContextualObject(String key) {

		if ("processInstance".equalsIgnoreCase(key))
			return getExecutionId();

		if ("processInstanceId".equalsIgnoreCase(key))
			return getExecutionId();

		if ("processEngine".equalsIgnoreCase(key))
			return this.processEngine;

		return null;
	}

	public String getConversationId() {
		return getExecutionId();
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope(ProcessScope.PROCESS_SCOPE_NAME, this);
	}

	public void destroy() throws Exception {
		logger.info(ProcessScope.class.getName() + "#destroy() called ...");
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.processEngine, "the 'processEngine' must not be null!");
		this.runtimeService = this.processEngine.getRuntimeService();

		logger.fine(getClass().getName() + " started.");
	}

	private Object createDirtyCheckingProxy(final ExecutionEntity executionEntity, final String name,final Object scopedObject) throws Throwable {
		DirtyMonitorProxyFactoryBean dirtyMonitorProxyFactoryBean = new DirtyMonitorProxyFactoryBean(scopedObject, new DirtyMonitorProxyFactoryBean.ObjectDirtiedListener() {
			public void onMethodInvoked(Object o, Method method) {
			 executionEntity.setVariable( name, scopedObject);
			}
		}) ;
		return dirtyMonitorProxyFactoryBean .getObject();
	}
}

@Deprecated
class DirtyMonitorFactoryBean extends ProxyConfig implements MethodInterceptor, FactoryBean {

	private ClassLoader beanClassLoader;
	private Advisor advisor;
	private ExecutionEntity executionEntity;
	private Object scopedObject;
	private String name;

	private Logger logger = Logger.getLogger(getClass().getName());

	public Object getObject() throws Exception {
		return createDirtyMonitorProxy(this.scopedObject);
	}

	public Class<?> getObjectType() {
		return Object.class;
	}

	public boolean isSingleton() {
		return false;
	}

	public DirtyMonitorFactoryBean(ExecutionEntity executionEntity, String name, Object scopedObj) {
		this.beanClassLoader = ClassUtils.getDefaultClassLoader();
		this.executionEntity = executionEntity;
		this.scopedObject = scopedObj;
		this.name = name;

		this.setProxyTargetClass(true);
		advisor = new Advisor() {
			public Advice getAdvice() {
				return DirtyMonitorFactoryBean.this;
			}

			public boolean isPerInstance() {
				return true;
			}
		};
	}

	private Object createDirtyMonitorProxy(Object bean) {

		Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (AopUtils.canApply(this.advisor, targetClass)) {// always true since we have no pointcut
			if (bean instanceof Advised) {
				((Advised) bean).addAdvisor(0, this.advisor);
				return bean;
			} else {
				ProxyFactory proxyFactory = new ProxyFactory(bean);
				proxyFactory.copyFrom(this );
				proxyFactory.addAdvisor(this.advisor);
				return proxyFactory.getProxy(this.beanClassLoader);
			}
		} else {
			return bean;
		}
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object result = methodInvocation.proceed();
		logger.info("a method has been called on " +methodInvocation.getThis()+": "+ methodInvocation.getMethod().getName());
		this.executionEntity.setVariable(this.name, scopedObject);
		return result;
	}
}
