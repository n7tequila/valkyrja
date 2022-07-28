/*
 * PROJECT valkyrja2
 * core/FormValidated.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.springframework.validation.annotation.Validated;
import org.valkyrja2.mvc.AbstractFormObject.FieldRequired;

import java.lang.annotation.*;

/**
 * @author Tequila
 * @create 2022/07/19 14:40
 **/
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated({ FieldRequired.class })
public @interface FormValidated {

}
