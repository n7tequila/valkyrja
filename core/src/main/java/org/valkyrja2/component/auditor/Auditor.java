/*
 * PROJECT valkyrja2
 * core/Auditor.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

import java.lang.annotation.*;

/**
 * 审计模块注释对象<br>
 *
 * @author Tequila
 * @create 2022/07/19 20:15
 **/
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditor {

	/** 系统 */
	String system() default "";
	
	/** 模块 */
	String module() default "";
	
	/** 操作 */
	String operate() default "";
	
	/**
	 * 审计记录数据对象<br>
	 * ANY      包括请求和返回<br>
	 * REQUEST  仅请求<br>
	 * RESPONSE 仅返回
	 */
	AuditPolicy policy() default AuditPolicy.NONE;
}
