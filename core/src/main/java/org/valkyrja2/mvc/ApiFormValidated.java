/*
 * PROJECT valkyrja2
 * core/ApiFormValidated.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.springframework.validation.annotation.Validated;
import org.valkyrja2.mvc.AbstractFormObject.FieldRequired;
import org.valkyrja2.mvc.StandardApiForm.ApiFieldRequired;

import java.lang.annotation.*;

/**
 * @author Tequila
 * @create 2022/07/19 14:41
 **/
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated({ FieldRequired.class, ApiFieldRequired.class })
public @interface ApiFormValidated {

}
