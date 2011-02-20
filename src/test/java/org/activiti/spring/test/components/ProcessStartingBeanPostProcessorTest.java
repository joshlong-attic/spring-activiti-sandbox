package org.activiti.spring.test.components;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.test.Deployment;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/ProcessStartingBeanPostProcessorTest-context.xml")
public class ProcessStartingBeanPostProcessorTest {


	@Autowired
	private ProcessEngine processEngine;


	@After
	public void closeProcessEngine() {
		// Required, since all the other tests seem to do a specific drop on the end
		processEngine.close();
	}


	@Autowired
	private ProcessInitiatingPojo processInitiatingPojo;

	@Test
	@Deployment
	public void simpleProcessTest() {

		assertNotNull("the processInitiatingPojo mustn't be null.", this.processInitiatingPojo);

		this.processInitiatingPojo.startProcess(53);

		/*   runtimeService.startProcessInstanceByKey("simpleProcess");
				Task task = taskService.createTaskQuery().singleResult();
				assertEquals("My Task", task.getName());

				taskService.complete(task.getId());
				assertEquals(0, runtimeService.createProcessInstanceQuery().count());
			 */
	}
}
