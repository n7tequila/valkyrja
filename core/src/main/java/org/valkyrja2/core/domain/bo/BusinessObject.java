/*
 * PROJECT valkyrja2
 * core/BusinessObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.domain.bo;

import java.lang.annotation.*;

/**
 * BO对象的定义
 *
 * @author Tequila
 * @create 2022/07/01 22:39
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessObject {
	
	/** 创建repository创建 */
	boolean repo() default true;

	/** 创建dao */
	boolean dao() default true;

	/** 当初始化报错的时候是否抛出错误 */
	boolean raiseError() default false;

	/** 初始化配置 */
	boolean init() default true;
}
