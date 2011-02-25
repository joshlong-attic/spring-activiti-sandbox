package org.activiti.spring.test.components.scope;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/ScopingTests-context.xml")
public class ScopingTests {

	@Autowired private ProcessEngine processEngine;

	private Logger logger = Logger.getLogger(getClass().getName());

	@Test
	public void testUsingAnInjectedScopedProxy() {
		logger.info("Running 'component-waiter' process instance with scoped beans.");
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("component-waiter");

		StatefulObject scopedObject = (StatefulObject) processEngine.getRuntimeService().getVariable(processInstance.getId(), "scopedTarget.c1");

		Assert.assertNotNull("the scopedObject can't be null", scopedObject);

		Assert.assertTrue("the 'name' property can't be null.", StringUtils.hasText(scopedObject.getName()));

		Assert.assertEquals(scopedObject.getVisitedCount(), 2);
	}


}
