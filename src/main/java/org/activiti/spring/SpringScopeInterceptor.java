package org.activiti.spring;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.cmd.StartProcessInstanceCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.components.scope.ProcessInstanceScopeContextHolder;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * interceptor that ensures the correct state for scoping
 * @author Josh Long
 */
public class SpringScopeInterceptor extends CommandInterceptor   {

	private Logger log = Logger.getLogger(getClass().getName());

	private RuntimeService runtimeService;

	public SpringScopeInterceptor(RuntimeService rs) {
		this.runtimeService =rs;
	}


	public <T> T execute(final Command<T> command) {
		try {

			try {


				T resu = next.execute(command) ;

				if (resu instanceof ProcessInstance && command instanceof StartProcessInstanceCmd){
					ProcessInstance pi= (ProcessInstance) resu;
					ProcessInstanceScopeContextHolder.setProcessInstance( pi);
				}
				return resu;

			} finally {
				ProcessInstanceScopeContextHolder.reset();
			}
		} catch (Exception e) {
			log.severe("could not execute command. " + ExceptionUtils.getFullStackTrace(e));
		}
		return null;
	}

}
