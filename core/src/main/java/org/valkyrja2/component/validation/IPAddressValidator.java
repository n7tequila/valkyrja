/*
 * PROJECT valkyrja2
 * core/IPAddressValidator.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.validation;

import org.valkyrja2.util.NetworkUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * IPAddress校验Validator
 *
 * @author Tequila
 * @create 2022/07/15 11:41
 **/
public class IPAddressValidator implements ConstraintValidator<IPAddress, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkUtils.isIPAddress(value.toString());
    }
}
