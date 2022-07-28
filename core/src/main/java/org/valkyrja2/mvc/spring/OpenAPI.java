/*
 * PROJECT valkyrja2
 * core/OpenAPI.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import java.lang.annotation.*;

/**
 * OpenAPI接口定义
 *
 * @author Tequila
 * @create 2022/07/24 01:59
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenAPI {
}
