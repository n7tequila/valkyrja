/*
 * PROJECT valkyrja2
 * core/AuditLogger.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

/**
 * 标准审计日志输出接口
 * 
 * @author Tequila
 *
 */
public interface AuditLogger {

	/**
	 * 输出到日志
	 *
	 * @param level       水平
	 * @param description 描述
	 * @author Tequila
	 * @date 2022/07/20 10:23
	 */
	void logger(String level, AuditDescription description);

	/**
	 * 日志记录器
	 *
	 * @param level       水平
	 * @param description 描述
	 * @param e           e
	 * @author Tequila
	 * @date 2022/07/20 10:23
	 */
	void logger(String level, AuditDescription description, Exception e);
}
