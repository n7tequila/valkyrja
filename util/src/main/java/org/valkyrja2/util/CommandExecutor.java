/*
 * PROJECT valkyrja2
 * util/CommandExecutor.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

/**
 * 命令执行接口
 *
 * @author Tequila
 * @create 2022/06/28 16:30
 **/
public interface CommandExecutor {

	/**
	 * 执行命令
	 *
	 * @param commands 命令
	 * @param timeout  超时时间
	 * @return {@link ExecuteResult }
	 * @author Tequila
	 * @date 2022/06/28 16:31
	 */
	ExecuteResult executeCommand(String[] commands, long timeout);
}
