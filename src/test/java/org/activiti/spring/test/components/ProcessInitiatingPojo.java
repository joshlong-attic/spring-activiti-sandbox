package org.activiti.spring.test.components;


import org.activiti.engine.annotations.ProcessVariable;
import org.activiti.engine.annotations.StartProcess;

import java.util.logging.Logger;

public class ProcessInitiatingPojo {

  private Logger log = Logger.getLogger(getClass().getName());

	@StartProcess(processKey = "b")
	public void startProcess(@ProcessVariable("customerId") long customerId) {
		log.info( "starting 'b' with customerId # " + customerId);
	}
}
