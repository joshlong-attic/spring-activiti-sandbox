package org.activiti.spring.test.components;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/ProcessStartingBeanPostProcessorTest-context.xml")
public class ProcessStartingBeanPostProcessorTest {

	private Logger log = Logger.getLogger(getClass().getName());

	@Autowired private ProcessEngine processEngine;
	@Autowired private ProcessInitiatingPojo processInitiatingPojo;

	@After
	public void closeProcessEngine() {
		processEngine.close();
	}

	@Test
	@Deployment
	public void testReturnedProcessInstance() throws Throwable {
		String processInstanceId = this.processInitiatingPojo.startProcessA(22);
		assertNotNull("the process instance id should not be null", processInstanceId);
	}

	@Test
	@Deployment
	public void testReflectingSideEffects() throws Throwable {
		assertNotNull("the processInitiatingPojo mustn't be null.", this.processInitiatingPojo);

		this.processInitiatingPojo.reset();

		assertEquals(this.processInitiatingPojo.getMethodState(), 0);

		this.processInitiatingPojo.startProcess(53);

		assertEquals(this.processInitiatingPojo.getMethodState(), 1);
	}

	@Deployment
	@Test
	public void testUsingBusinessKey() throws Throwable {
		long id = 5;
		String businessKey = "usersKey" + System.currentTimeMillis();
		ProcessInstance pi = processInitiatingPojo.enrollCustomer(businessKey, id);
		assertEquals("the business key of the resultant ProcessInstance should match " +
				"the one specified through the AOP-intercepted method" ,businessKey, pi.getBusinessKey());

	}

	@Test
	@Deployment
	public void testLaunchingProcessInstance() {
		long id = 343;
		String processInstance = processInitiatingPojo.startProcessA(id);
		Long customerId = (Long) processEngine.getRuntimeService().getVariable(processInstance, "customerId");
		assertEquals("the process variable should both exist and be equal to the value given, " + id, customerId, (Long) id);
		log.info("the customerId fromt he ProcessInstance is " + customerId);
		assertNotNull("processInstanc can't be null", processInstance);
		assertNotNull("the variable should be non-null", customerId);
	}
}
