package org.activiti.spring.components.scope;

import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.core.NamedThreadLocal;

/**
 * mutex for {@link org.activiti.engine.runtime.ProcessInstance} state.
 *
 * todo setup corresponding logic to register the {@link ProcessInstance} and destroy it on {@link ProcessInstance} lifetimes.
 *
 *
 * @author Josh Long
 */
public abstract class ProcessInstanceScopeContextHolder {

	private static final ThreadLocal<ProcessInstance> processInstanceHolder =
			new NamedThreadLocal<ProcessInstance>(ProcessInstance.class.getName());

	public static void reset() {
		processInstanceHolder.set(null);
	}

	public static ProcessInstance getCurrentProcessInstance() {
		ProcessInstance processInstance = processInstanceHolder.get();
		if(processInstance == null){
			 throw new IllegalStateException("there is no " +
					 ProcessInstance.class.getName() + " bound to the current thread!") ;
		}
		return processInstance;
	}

	public static void setProcessInstance(ProcessInstance processInstance ){
		processInstanceHolder.set(processInstance);
	}

}
