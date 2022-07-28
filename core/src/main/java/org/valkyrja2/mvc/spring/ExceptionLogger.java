/*
 * PROJECT valkyrja2
 * core/ExceptionLogger.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

/**
 * 统一Exception日志写入接口，用户可以自定义日志写入
 *
 * @author Tequila
 * @create 2022/07/13 23:36
 **/
public interface ExceptionLogger {

	/**
	 * 报错日志具体输出操作
	 *
	 * @param e 错误信息
	 * @author Tequila
	 * @date 2022/07/13 23:36
	 */
	void logger(Exception e);
}
