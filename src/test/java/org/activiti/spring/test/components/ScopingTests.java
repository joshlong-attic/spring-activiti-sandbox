package org.activiti.spring.test.components;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.components.scope.ProcessInstanceScopeContextHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/ScopingTests-context.xml")
public class ScopingTests {

	private Logger logger = Logger.getLogger(getClass().getName());

	//	@Autowired ProcessInitiatingPojo  processInitiatingPojo ;
	@Autowired
	ProcessEngine processEngine;

	@Autowired
	ApplicationContext applicationContext;

	private ScopedCustomer processInitiatingPojo() {
		return this.applicationContext.getBean(ScopedCustomer.class);
	}

	@Deployment
	@Test
	public void testRunningAProcessThatDependsOnAState() {


		ProcessInstance processInstance;

		processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("waiter");
		Assert.assertNotNull(processInstance);
		ProcessInstanceScopeContextHolder.reset();
		ProcessInstanceScopeContextHolder.setProcessInstance(processInstance);
		log(processInstance, processInitiatingPojo());
		ProcessInstanceScopeContextHolder.reset();

		processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("waiter");
		Assert.assertNotNull(processInstance);
		ProcessInstanceScopeContextHolder.reset();
		ProcessInstanceScopeContextHolder.setProcessInstance(processInstance);
		ScopedCustomer sc=processInitiatingPojo();
		log(processInstance, sc);
		ScopedCustomer sc1=processInitiatingPojo();
		Assert.assertEquals(sc1.getName(), sc.getName());



		ProcessInstanceScopeContextHolder.reset();


	}


	void log(ProcessInstance processInstance, ScopedCustomer scopedCustomer) {

		ScopedCustomer sc =(ScopedCustomer)
				this.processEngine.getRuntimeService().getVariable( processInstance.getId(), "customer");

		Assert.assertNotNull("scopedCustomer is null." , sc) ;

	}

}
