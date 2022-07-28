/*
 * PROJECT valkyrja2
 * core/BizRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.valkyrja2.mvc.BizResponseCode;
import org.valkyrja2.mvc.RequestContext;
import org.valkyrja2.mvc.RequestContextFactory;
import org.valkyrja2.mvc.ResponseCode;
import org.valkyrja2.util.HttpStatus;
import org.valkyrja2.util.StringUtils;

/**
 * 标准业务逻辑层面RunException抛出父类<br>
 * 所有的业务逻辑中在运行期可能的错误都需要转化为相应的BizRuntimeException进行抛出<br>
 * BizRuntimeException主要是用于和前台交互时使用的相关错误对象
 *
 * @author Tequila
 * @create 2022/07/01 23:24
 **/
@JsonPropertyOrder(value = {"code", "httpCode", "requestContext", "cause"})
public class BizRuntimeException extends RuntimeException implements BizExceptionBean {

	private static final long serialVersionUID = 2344766074370640465L;

	/** 错误代码 */
	private final String code;
	
	/** 对应的HttpCode */
	private final int httpCode;
	
	/** 错误相关的请求上下文对象 */
	private final RequestContext requestContext;

	/** 错误时返回的数据 */
	private Object data;

	/**
	 * 构造一个默认的BizException
	 * 默认的code和httpCode使用ResponseCode.SYSTEM_UNKNOWN_ERROR中的定义
	 *
	 * @author Tequila
	 * @date 2022/07/01 23:25
	 */
	public BizRuntimeException () {
		super();
		
		this.code = ResponseCode.SYSTEM_UNKNOWN_ERROR.code();
		this.httpCode = ResponseCode.SYSTEM_UNKNOWN_ERROR.httpCode();
		this.requestContext = RequestContextFactory.getContext();
	}

	/**
	 * 通过message构造一个BizRuntimeException对象
	 *
	 * @param message 消息
	 * @author Tequila
	 * @date 2022/07/01 23:25
	 */
	public BizRuntimeException(String message) {
		super(message);
		
		this.code = ResponseCode.SYSTEM_UNKNOWN_ERROR.code();
		this.httpCode = ResponseCode.SYSTEM_UNKNOWN_ERROR.httpCode();
		this.requestContext = RequestContextFactory.getContext();
	}

	/**
	 * 通过code和httpCode构造一个BizRuntimeException对象
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @author Tequila
	 * @date 2022/07/01 23:26
	 */
	public BizRuntimeException(String code, int httpCode) {
		super();
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 通过code和httpCode构造一个BizRuntimeException对象
	 * 同时赋予message信息
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @param message  消息
	 * @author Tequila
	 * @date 2022/07/03 01:11
	 */
	public BizRuntimeException(String code, int httpCode, String message) {
		super(message);
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 初始化BizRuntimeException同时传递错误对象
	 *
	 * @param code     代码
	 * @param httpCode http代码
	 * @param message  消息
	 * @param cause    导致
	 * @author Tequila
	 * @date 2022/07/03 01:12
	 */
	public BizRuntimeException(String code, int httpCode, String message, Throwable cause) {
		super(message, cause);
		
		this.requestContext = RequestContextFactory.getContext();
		this.code = code;
		this.httpCode = httpCode;
	}

	/**
	 * 用message和cause构造BizRuntimeException对象
	 *
	 * @param message 消息
	 * @param cause   导致
	 * @author Tequila
	 * @date 2022/07/03 01:14
	 */
	public BizRuntimeException(String message, Throwable cause) {
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
	 * @date 2022/07/03 01:14
	 */
	public BizRuntimeException(ResponseCode responseCode) {
		super(responseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}


	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @author Tequila
	 * @date 2022/05/10 10:36
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode) {
		super(responseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
	}

	/**
	 * 使用ResponseCode和cause来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/07/03 01:07
	 */
	public BizRuntimeException(ResponseCode responseCode, Throwable cause) {
		super(responseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用ResponseCode和cause来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/05/10 10:37
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, Throwable cause) {
		super(responseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
	}

	/**
	 * 使用ResponseCode来构造错误对象，沿用ResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @author Tequila
	 * @date 2022/07/03 01:08
	 */
	public BizRuntimeException(ResponseCode responseCode, String message) {
		super(StringUtils.isBlank(message) ? responseCode.message() : message);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
	}

	/**
	 * 使用ResponseCode来构造错误对象，沿用ResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param message      消息
	 * @author Tequila
	 * @date 2022/05/10 10:38
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, String message) {
		super(StringUtils.isBlank(message) ? responseCode.message() : message);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
	}


	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param data         数据
	 * @author Tequila
	 * @date 2022/07/03 01:14
	 */
	public BizRuntimeException(ResponseCode responseCode, Object data) {
		super(responseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param data         数据
	 * @author Tequila
	 * @date 2022/05/10 10:38
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, Object data) {
		super(responseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param data         数据
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/07/03 01:14
	 */
	public BizRuntimeException(ResponseCode responseCode, Object data, Throwable cause) {
		super(responseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 商业运行时异常
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param data         数据
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/05/10 11:47
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, Object data, Throwable cause) {
		super(responseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
		this.data = data;
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param message      消息
	 * @param data         数据
	 * @author Tequila
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(ResponseCode responseCode, String message, Object data) {
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
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(ResponseCode responseCode, String message, Object data, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = responseCode.httpCode();
		this.data = data;
	}

	/**
	 * 使用ResponseCode来构造错误对象
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param message      消息
	 * @param data         数据
	 * @author Tequila
	 * @date 2022/05/10 11:48
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, String message, Object data) {
		super(message);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
		this.data = data;
	}

	/**
	 * 商业运行时异常
	 *
	 * @param responseCode 响应代码
	 * @param httpCode     http代码
	 * @param message      消息
	 * @param data         数据
	 * @param cause        导致
	 * @author Tequila
	 * @date 2022/05/10 11:45
	 */
	public BizRuntimeException(ResponseCode responseCode, HttpStatus httpCode, String message, Object data, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = responseCode.code();
		this.httpCode = httpCode.value();
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
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(ResponseCode responseCode, String message, Throwable cause) {
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
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode) {
		super(bizResponseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}

	/**
	 * 商业运行时异常
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param httpCode        http代码
	 * @author Tequila
	 * @date 2022/05/10 11:50
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode, HttpStatus httpCode) {
		super(bizResponseCode.message());
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = httpCode.value();
	}

	/**
	 * 使用BizResponseCode和cause来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode, Throwable cause) {
		super(bizResponseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}

	/**
	 * 商业运行时异常
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param httpCode        http代码
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/05/10 11:51
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode, HttpStatus httpCode, Throwable cause) {
		super(bizResponseCode.message(), cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = httpCode.value();
	}

	/**
	 * 使用BizResponseCode和cause来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param message         消息
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/07/03 01:15
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode, String message, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = bizResponseCode.httpCode();
	}

	/**
	 * 使用BizResponseCode和cause来构造错误对象，沿用BizResponseCode中code和httpCode的定义
	 * 但是用户可以自己选择返回的错误信息
	 *
	 * @param bizResponseCode 商务响应代码
	 * @param httpCode        http代码
	 * @param message         消息
	 * @param cause           导致
	 * @author Tequila
	 * @date 2022/05/10 11:51
	 */
	public BizRuntimeException(BizResponseCode bizResponseCode, HttpStatus httpCode, String message, Throwable cause) {
		super(message, cause);
		this.requestContext = RequestContextFactory.getContext();
		this.code = bizResponseCode.code();
		this.httpCode = httpCode.value();
	}

	/**
	 * 判断报错信息是否是某个ResponseCode的返回值
	 *
	 * @param responseCode 响应代码
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/03 01:10
	 */
	@JsonIgnore
	public boolean isResponseCode(ResponseCode responseCode) {
		return this.getCode().equals(responseCode.code());
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
