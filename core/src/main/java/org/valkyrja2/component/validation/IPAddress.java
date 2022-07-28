/*
 * PROJECT valkyrja2
 * core/IPAddress.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Bean Validation IPAddress 判断
 *
 * @author Tequila
 * @create 2022/07/15 11:36
 **/
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPAddressValidator.class)
public @interface IPAddress {

    String message() default "IP address format error.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
