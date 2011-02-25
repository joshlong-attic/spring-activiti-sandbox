package org.activiti.spring.test.components.scope;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Josh Long
 * @since 5,3
 */

public class Delegate1 implements JavaDelegate {

	private Logger log = Logger.getLogger( getClass().getName());

	@Autowired private StatefulObject statefulObject;

	public void execute(DelegateExecution execution) throws Exception {


		Assert.assertNotNull("the 'scopedCustomer' reference can't be null",  statefulObject);
		String uuid =  UUID.randomUUID().toString();
		statefulObject.setName( uuid );
		log.info( "the 'uuid' value given to the ScopedCustomer#name property is '" +  uuid + "' in "+getClass().getName());

		this.statefulObject.increment();
	}

}
