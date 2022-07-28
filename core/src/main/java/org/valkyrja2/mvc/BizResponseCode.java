/*
 * PROJECT valkyrja2
 * core/BizResponseCode.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.valkyrja2.util.HttpStatus;

/**
 * 用于给基于项目定义的自定义返回值的基础类
 *
 * @author Tequila
 * @create 2022/07/03 00:02
 **/
public class BizResponseCode {
	
	/** 返回值 */
	private final String code;

	/** 返回信息 */
	private final String message;
	
	/** http状态 */
	private final HttpStatus httpStatus;

	private BizResponseCode() {
		throw new IllegalStateException("Please use new BizResponseCode(code, message, httpStatus)");
	}

	public BizResponseCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String code() {
		return this.code;
	}
	
	public String message() {
		return this.message;
	}
	
	public HttpStatus httpStatus() {
		return this.httpStatus;
	}
	
	public int httpCode() {
		return this.httpStatus.value();
	}
}
