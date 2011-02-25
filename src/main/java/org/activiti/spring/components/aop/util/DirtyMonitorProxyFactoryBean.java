package org.activiti.spring.components.aop.util;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class DirtyMonitorProxyFactoryBean extends ProxyConfig implements MethodInterceptor, FactoryBean<Object> {

	private Logger logger = Logger.getLogger(getClass().getName());
	private ClassLoader beanClassLoader;
	private Object objectToMonitor;
	private ObjectDirtiedListener objectDirtiedListener;

	private Advisor advisor = new Advisor() {
		public Advice getAdvice() {
			return DirtyMonitorProxyFactoryBean.this;
		}

		public boolean isPerInstance() {
			return true;
		}
	};

	public DirtyMonitorProxyFactoryBean(Object o, ObjectDirtiedListener objectDirtiedListener) {
		this.objectToMonitor = o;
		this.objectDirtiedListener = objectDirtiedListener;
		this.beanClassLoader = ClassUtils.getDefaultClassLoader();
		this.setProxyTargetClass(true);
	}

	public Object getObject() throws Exception {
		return createDirtyMonitorProxy(this.objectToMonitor);
	}

	public Class<?> getObjectType() {
		return this.objectToMonitor.getClass();
	}

	public boolean isSingleton() {
		return false;
	}

	/**
	 * clients of this class must implement this listener to be notified of when an object has changed.
	 */
	public static interface ObjectDirtiedListener {
		void onMethodInvoked(Object o, Method method);
	}

	private Object createDirtyMonitorProxy(Object bean) {

		Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (AopUtils.canApply(this.advisor, targetClass)) {// always true since we have no pointcut
			if (bean instanceof Advised) {
				((Advised) bean).addAdvisor(0, this.advisor);
				return bean;
			} else {
				ProxyFactory proxyFactory = new ProxyFactory(bean);
				proxyFactory.copyFrom(this);
				proxyFactory.addAdvisor(this.advisor);
				return proxyFactory.getProxy(this.beanClassLoader);
			}
		} else {
			return bean;
		}
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object result = methodInvocation.proceed();
		logger.fine("a method has been called on " + methodInvocation.getThis() + ": " + methodInvocation.getMethod().getName());
		this.objectDirtiedListener.onMethodInvoked(this.objectToMonitor, methodInvocation.getMethod());
		return result;
	}
}

/*
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
*/
