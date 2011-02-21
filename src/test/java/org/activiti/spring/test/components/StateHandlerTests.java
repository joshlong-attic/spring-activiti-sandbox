package org.activiti.spring.test.components;

import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;

/**
 * simple class that tests our ability to register components as state handlers
 *
 * @author Josh Long
 * @since 5.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/StateHandlerTests-context.xml")
public class StateHandlerTests {
	private Logger logger = Logger.getLogger(getClass().getName());

	@Deployment
	@Test
	public void testRunningAProcessThatDependsOnAState() {
		logger.info("running " + getClass().getName()) ;
	}

}
