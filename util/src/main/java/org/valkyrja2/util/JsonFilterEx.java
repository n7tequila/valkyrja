/*
 * PROJECT valkyrja2
 * util/JsonFilterEx.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.lang.annotation.*;


/**
 * Spring MVC RestController自定义Json过滤配置<br>
 *
 * @author Tequila
 * @create 2022/06/24 23:58
 **/
@Documented
@Repeatable(JsonFilterGroup.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFilterEx {

	/** 根据属性匹配 */
	String[] properties();
	
	/** 指定过滤的对象 */
	Class<?> type() default Object.class;

	/**
	 * 操作模式（默认为黑名单模式）<br>
	 * INCLUDE 白名单，允许列表<br>
	 * EXCLUDE 黑名单，过滤列表<br>
	 */
	FilterPolicy policy() default FilterPolicy.EXCLUDE;
}
