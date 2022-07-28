/*
 * PROJECT valkyrja2
 * util/FilterPolicy.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

/**
 * JsonFilterEx过滤政策<br>
 * 限定JsonFilterEx中所规定的字段处理的方式
 *
 * @author Tequila
 * @create 2022/06/25 00:00
 **/
public enum FilterPolicy {

	/** 包括 */
	INCLUDE,

	/** 排除 */
	EXCLUDE;
}
