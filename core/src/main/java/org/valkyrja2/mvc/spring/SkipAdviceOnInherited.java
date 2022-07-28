/*
 * PROJECT valkyrja2
 * core/SkipAdviceOnInherited.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SkipAdviceOnInherited {

}
