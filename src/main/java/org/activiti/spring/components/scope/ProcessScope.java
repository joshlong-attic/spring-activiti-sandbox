package org.activiti.spring.components.scope;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.Assert;

import java.util.logging.Logger;

/**
 * binds variables to a currently executing Activiti business process -- a
 * {@link org.activiti.engine.runtime.ProcessInstance}.
 *
 * @author Josh Long
 */
public class ProcessScope implements Scope, InitializingBean, BeanFactoryPostProcessor, DisposableBean {

	public final static String PROCESS_SCOPE_NAME = "process";

	private Logger logger = Logger.getLogger(getClass().getName()) ;

	private ProcessEngine processEngine;

	private RuntimeService runtimeService;

	/**
	 * REQUIRED
	 *
	 * this is set by reflection in the {@link org.activiti.spring.components.config.xml.ActivitiNamespaceHandler}
	 *
	 * @param processEngine the in-use {@link ProcessEngine}
	 *
	 */
	@SuppressWarnings("unused")
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	public Object get(String name, ObjectFactory<?> objectFactory) {

		logger.info ("returning scoped object having beanName '" +name +
				"' for conversation ID '" + this.getConversationId()+ "'. ");
		Object scopedObject = runtimeService.getVariable(getProcessInstanceId(), name);
		if (scopedObject == null) {
			scopedObject = objectFactory.getObject();
			runtimeService.setVariable(this.getProcessInstanceId(), name, scopedObject);
		}
		return scopedObject;
	}


	// todo
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.info ( "no support for registering descruction callbacks " +
				"implemented currently. registerDestructionCallback('" + name + "',callback) will do nothing.");

	}

	private String getProcessInstanceId() {
		return getProcessInstance().getId();
	}

	public Object remove(String name) {
		return runtimeService.getVariable(getProcessInstanceId(), name);
	}

	public Object resolveContextualObject(String key) {

		if("processInstance".equalsIgnoreCase(key))
				return getProcessInstance();

		if("processInstanceId".equalsIgnoreCase(key ))
			return getProcessInstanceId();

		if("processEngine".equalsIgnoreCase( key))
			return this.processEngine ;

		return null ;
	}

	public ProcessInstance getProcessInstance(){
		return ProcessInstanceScopeContextHolder.getCurrentProcessInstance();
	}

	public String getConversationId() {
		return getProcessInstanceId();
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope(ProcessScope.PROCESS_SCOPE_NAME, this);
 	}

	public void destroy() throws Exception {
		logger.info( ProcessScope.class.getName()+ "#destroy() called ...");
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.processEngine, "the 'processEngine' must not be null!");
		this.runtimeService = this.processEngine.getRuntimeService();
	}
}
