package org.activiti.spring.components.scope;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.core.NamedThreadLocal;

/**
 * mutex for {@link org.activiti.engine.runtime.ProcessInstance} state.
 *
 * @author Josh Long
 */
public abstract class ProcessInstanceScopeContextHolder {
	@Deprecated
	public static ProcessInstance getCurrentProcessInstance() {
		ProcessInstance processInstance = Context.getExecutionContext().getProcessInstance();
		if (processInstance == null) {
			throw new IllegalStateException("there is no " + ProcessInstance.class.getName() + " bound to the current thread!");
		}
		return processInstance;
	}

	public static String getCurrentExecutionId() {
		return Context.getExecutionContext().getExecution().getId();
	}

}
