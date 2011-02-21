package org.activiti.engine.annotations;

import java.lang.annotation.*;

/**
 *
 * Inspired by the CDI implemenation
 *
 * @author Josh Long
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessKey {
}
