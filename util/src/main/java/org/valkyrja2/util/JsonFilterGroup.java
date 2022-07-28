/*
 * PROJECT valkyrja2
 * util/JsonFilterGroup.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.lang.annotation.*;


/**
 * Spring MVC RestController自定义Json过滤配置<br>
 * 
 * @author Tequila
 *
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFilterGroup {
	
	JsonFilterEx[] value();
}
