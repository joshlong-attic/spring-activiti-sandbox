package org.activiti.engine.annotations;

import java.lang.annotation.*;

/**
 * specifies which parameter is a user id
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserId {
	String value() ;
}
