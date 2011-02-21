package org.activiti.engine.annotations;

import java.lang.annotation.*;

/**
 * specifies which parameter is a task id
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskId {
	String value() default "";


}
