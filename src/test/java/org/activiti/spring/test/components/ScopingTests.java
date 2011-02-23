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

	//	@Autowired ProcessInitiatingPojo  scopedCustomer ;
	@Autowired
	ProcessEngine processEngine;

	@Autowired
	ApplicationContext applicationContext;

	private ScopedCustomer scopedCustomer() {
		return this.applicationContext.getBean("customer", ScopedCustomer.class);
	}

	private ProcessInitiatingPojo processInitiatingPojo() {
		return this.applicationContext.getBean(ProcessInitiatingPojo.class);
	}



	public void testUsingAnInjectedScopedProxy() {

		logger.info("---------------------------------");
		logger.info("scoped proxy test");
		ProcessInstance processInstance;

		processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("waiter");
		Assert.assertNotNull(processInstance);
		ProcessInstanceScopeContextHolder.reset();
		ProcessInstanceScopeContextHolder.setProcessInstance(processInstance);


		ProcessInitiatingPojo processInitiatingPojo = processInitiatingPojo();
		processInitiatingPojo.logScopedCustomer(processInstance);

		ProcessInstanceScopeContextHolder.reset();
		logger.info("scoped proxy test");
		logger.info("---------------------------------");
	}

	@Deployment
	@Test
	public void testRunningAProcessThatDependsOnAState() {

		ProcessInstance processInstance;

		processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("waiter");
		Assert.assertNotNull(processInstance);
		ProcessInstanceScopeContextHolder.reset();
		ProcessInstanceScopeContextHolder.setProcessInstance(processInstance);
		ScopedCustomer sc = scopedCustomer();
		log(processInstance, sc);
		ProcessInstanceScopeContextHolder.reset();

		processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("waiter");
		Assert.assertNotNull(processInstance);
		ProcessInstanceScopeContextHolder.reset();
		ProcessInstanceScopeContextHolder.setProcessInstance(processInstance);
		sc = scopedCustomer();
		log(processInstance, sc);
		ScopedCustomer sc1 = scopedCustomer();

		Assert.assertEquals("successive accesses of the same object during " +
				"a given ProcessInstance execution should yeild the same result.", sc1.getName(), sc.getName());

		ProcessInstanceScopeContextHolder.reset();

		testUsingAnInjectedScopedProxy();
		testUsingAnInjectedScopedProxy();

	}

	private void log(ProcessInstance processInstance, ScopedCustomer scopedCustomer) {
		ScopedCustomer sc = (ScopedCustomer)
				this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "customer");
		Assert.assertNotNull("scopedCustomer is null.", sc);
		Assert.assertEquals("the scopedCustomer should be the same as the one in the Activiit context.",
				scopedCustomer.getName(), sc.getName());
	}

}
