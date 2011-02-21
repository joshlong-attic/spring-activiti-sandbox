package org.activiti.engine.annotations;

import java.lang.annotation.*;

/**
 * this annotation instructs the component model to start an Activiti business process on
 * sucessful invocation of a method that's annotated with it.
 *
 * @author Josh Long
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StartProcess {
	/**
	 * the name of the business process to start (by key)
	 */
	String processKey() ;

	/**
	 * returns the ID of the {@link org.activiti.engine.runtime.ProcessInstance}. If specified, it'll only work if
	 * the return type of the invocation is compatabile with a {@link org.activiti.engine.runtime.ProcessInstance}'s ID
	 * (which is a String, at the moment)
	 *
	 * @return  whether to return the process instance ID
	 */
	boolean returnProcessInstanceId() default false;




}
