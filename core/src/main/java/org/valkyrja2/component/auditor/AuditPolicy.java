/*
 * PROJECT valkyrja2
 * core/AuditPolicy.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

/**
 * 审计政策
 *
 * @author Tequila
 * @create 2022/07/19 20:15
 **/
public enum AuditPolicy {

	ANY,
	
	REQUEST,
	
	RESPONSE,
	
	NONE;
}
