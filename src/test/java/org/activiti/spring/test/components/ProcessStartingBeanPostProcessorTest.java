package org.activiti.spring.test.components;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Test @Deployment public void testReturnedProcessInstance ()  throws Throwable {

	}
	@Test
	@Deployment
	public void testReflectingSideEffects() throws Throwable {
		assertNotNull("the processInitiatingPojo mustn't be null.", this.processInitiatingPojo);
		this.processInitiatingPojo.startProcess(53);
		assertEquals(this.processInitiatingPojo.getMethodState(), 1);
	}

	@Test
	@Deployment
	public void simpleProcessTest() {


		ProcessInstance processInstance = processInitiatingPojo.startProcessA(343);
		Object v = processEngine.getRuntimeService().getVariable(
				processInstance.getId(), "customerId");

		log.info("the customerId fromt he ProcessInstance is " + v);
		assertNotNull("processInstanc can't be null", processInstance);
		assertNotNull("the variable should be non-null", v);
	}
}
