/*
 * PROJECT valkyrja2
 * util/ExecuteResult.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.util.Arrays;
import java.util.List;

/**
 * 命令行执行结果
 *
 * @author Tequila
 * @create 2022/06/28 16:47
 **/
public class ExecuteResult {
	
	private int exitCode = -1;
	
	private String executeOut;
	
	private List<String> executeOutList;

	public ExecuteResult(int exitCode, String executeOut) {
		this.exitCode = exitCode;
		this.executeOut = executeOut;
		if (executeOut != null) {
			String[] array = executeOut.split("\n|\r\n");
			this.executeOutList = Arrays.asList(array);
		}
		
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public String getExecuteOut() {
		return executeOut;
	}

	public void setExecuteOut(String executeOut) {
		this.executeOut = executeOut;
	}

	public List<String> getExecuteOutList() {
		return executeOutList;
	}

	public void setExecuteOutList(List<String> executeOutList) {
		this.executeOutList = executeOutList;
	}

}
