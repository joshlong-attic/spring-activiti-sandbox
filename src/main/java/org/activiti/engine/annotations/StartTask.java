package org.activiti.engine.annotations;

import java.lang.annotation.*;

//todo 
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StartTask {

	String userId() default "";

	String taskId() ;

	/**
	 * the name of the task to start work on
	 * @return the name of the task
	 */
	String value()   ;
}
