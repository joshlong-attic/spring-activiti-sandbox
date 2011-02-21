package org.activiti.spring.test.components;


import org.activiti.engine.annotations.BusinessKey;
import org.activiti.engine.annotations.ProcessId;
import org.activiti.engine.annotations.ProcessVariable;
import org.activiti.engine.annotations.StartProcess;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.logging.Logger;

public class ProcessInitiatingPojo {

	private Logger log = Logger.getLogger(getClass().getName());

	private int methodState = 0;

	public void reset(){
		this.methodState=0;
	}

	/*
	public void log (ActivityExecution execution){
		 log.info("execution:" + ToStringBuilder.reflectionToString(execution ) ) ;
	}*/

	@StartProcess(processKey = "b")
	public void startProcess(@ProcessVariable("customerId") long customerId) {
		log.info( "starting 'b' with customerId # " + customerId);
		this.methodState+=1;
		log.info ( "up'd the method state");
	}

	public int getMethodState() {
		return methodState;
	}

	@StartProcess(processKey = "waiter",returnProcessInstanceId = true )
	public String startProcessA(@ProcessVariable("customerId" ) long cId){
		return null;
	}

	@StartProcess(processKey = "waiter")
	public ProcessInstance enrollCustomer( @BusinessKey String key,  @ProcessVariable("customerId") long customerId ){
		return null  ;
	}

}
