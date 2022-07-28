/*
 * PROJECT valkyrja2
 * core/BizException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.valkyrja2.mvc.BizResponseCode;
import org.valkyrja2.mvc.RequestContext;
import org.valkyrja2.mvc.RequestContextFactory;
import org.valkyrja2.mvc.ResponseCode;

/**
 * 标准业务逻辑层面Exception抛出父类<br>
 * 所有的业务逻辑中需要代码处理的错误都需要转化为相应的BizException进行抛出<br>
 * BizException主要目的是为了统一Java方面业务逻辑方法之间进行已知的流程控制或者错误处理使用的Exception对象
 *
 * @author Tequila
 * @create 2022/07/01 23:23
 **/
@JsonPropertyOrder(value = {"code", "httpCode", "requestContext", "cause"})
public class BizException extends Exception implements BizExceptionBean {

	private static final long serialVersionUID = -931243351325954559L;

	/** 错误代码 */
	private final String code;
	
	/** 对应的HttpCode */
	private final int httpCode; 
	
	/** 错误相关的请求上下文对象 */
	private final RequestContext requestContext;

	/** 错误相关的数据 */
	private Object data;

	/**
	 * 构造一个默认的BizException
	 * 默认的code和httpCode使用ResponseCode.SYSTEM_UNKNOW_ERROR中的定义
	 *
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException () {
		super();
		
		this.code = ResponseCode.SYSTEM_UNKNOWN_ERROR.code();
		this.httpCode = ResponseCode.SYSTEM_UNKNOWN_ERROR.httpCode();
		this.requestContext = RequestContextFactory.getContext();
	}

	/**
	 * 通过message构造一个BizException对象
	 *
	 * @param message 消息
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(String message) {
		super(message);
		
		this.code = ResponseCode.SYSTEM_UNKNOWN_ERROR.code();
		this.httpCode = ResponseCode.SYSTEM_UNKNOWN_ERROR.httpCode();
		this.requestContext = RequestContextFactory.getContext();
	}

	/**
	 * 通过code和httpCode构造一个BizException对象
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(String code, int httpCode) {
		super();
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 通过code和httpCode构造一个BizException对象
	 * 同时赋予message信息
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @param message  消息
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(String code, int httpCode, String message) {
		super(message);
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 初始化BizException同时传递错误对象
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @param message  消息
	 * @param cause    导致
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(String code, int httpCode, String message, Throwable cause) {
		super(message, cause);
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 用message和cause构造BizException对象
	 *
	 * @param message 消息
	 * @param cause   导致
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(String message, Throwable cause) {
		super(message, cause);
		
		this.code = ResponseCode.SYSTEM_UNKNOWN_ERROR.code();
		this.httpCode = ResponseCode.SYSTEM_UNKNOWN_ERROR.httpCode();
		this.requestContext = RequestContextFactory.getContext();
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(ResponseCode responseCode) {
		super(responseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用ResponseCode和cause来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/07/03 01:03
	 */
	public BizException(ResponseCode responseCode, Throwable cause) {
		super(responseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用ResponseCode来构造错误对象，沿用ResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @author Tequila
	 * @date 2022/07/03 01:04
	 */
	public BizException(ResponseCode responseCode, String message) {
		super(message);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @param data         数据
	 * @author Tequila
	 * @date 2022/07/03 01:04
	 */
	public BizException(ResponseCode responseCode, String message, Object data) {
		super(message);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @param data         数据
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/07/03 01:04
	 */
	public BizException(ResponseCode responseCode, String message, Object data, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 使用ResponseCode和cause来构造错误对象，沿用ResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/07/03 01:04
	 */
	public BizException(ResponseCode responseCode, String message, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用BizResponseCode来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 *
	 * @param bizResponseCode 商务响应代码
	 * @author Tequila
	 * @date 2022/07/03 00:04
	 */
	public BizException(BizResponseCode bizResponseCode) {
		super(bizResponseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}

	/**
	 * 使用BizResponseCode和cause来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/07/03 00:04
	 */
	public BizException(BizResponseCode bizResponseCode, Throwable cause) {
		super(bizResponseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}

	/**
	 * 使用BizResponseCode和cause来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param message         消息
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/07/03 01:04
	 */
	public BizException(BizResponseCode bizResponseCode, String message, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}
	
	public String getCode() {
		return code;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
