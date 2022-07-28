/*
 * PROJECT valkyrja2
 * core/ResponseObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.http.entity.ContentType;
import org.valkyrja2.exception.BizExceptionBean;
import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.util.ClassUtils;
import org.valkyrja2.util.Jackson2Utils;
import org.valkyrja2.util.NetworkUtils;

import javax.servlet.http.HttpServletResponse;
import java.beans.Transient;
import java.io.IOException;

/**
 * 响应对象
 *
 * @author Tequila
 * @create 2022/07/01 23:27
 **/
@JsonPropertyOrder({"code", "message", "hostId", "requestId", "time", "httpCode", "data", "errData"})
public class ResponseObject<T> {

	/** 请求id */
	private String requestId;

	/** 返回代码 */
	private String code;

	/** 消息 */
	private String message;

	/** 主机id */
	private String hostId;

	/** http代码 */
	private int httpCode;

	/** 时间戳 */
	private long time = System.currentTimeMillis();

	/** 数据 */
	@JsonInclude(Include.NON_NULL)
	private T data;

	/** 错误数据 */
	@JsonInclude(Include.NON_NULL)
	private Object errData;

	/**
	 * 初始化默认值
	 *
	 * @param requestCode 请求代码
	 * @author Tequila
	 * @date 2022/07/01 23:28
	 */
	protected void initDefaultValue(ResponseCode requestCode) {
		initDefaultValue(requestCode, RequestContextFactory.getContext());
	}

	/**
	 * 初始化默认值
	 *
	 * @param requestCode 请求代码
	 * @param context     上下文
	 * @author Tequila
	 * @date 2022/07/01 23:28
	 */
	protected void initDefaultValue(ResponseCode requestCode, RequestContext context) {
		if (context != null) {
			this.requestId = context.getRequestId();
			this.hostId = context.getHostId();
		} else {
			this.hostId = NetworkUtils.getHostName();
		}
		this.code = requestCode.code();
		this.message = requestCode.message();
		this.httpCode = requestCode.httpCode();
		flushResponseHttpCode();
	}

	/**
	 * 初始化默认值
	 *
	 * @param e e
	 * @author Tequila
	 * @date 2022/07/01 23:29
	 */
	protected void initDefaultValue(Exception e) {
		initDefaultValue(ResponseCode.SYSTEM_UNKNOWN_ERROR);
		
		if (e instanceof BizExceptionBean) {
			BizExceptionBean exBean = (BizExceptionBean) e;
			
			this.code = exBean.getCode();
			this.message = exBean.getMessage();
			this.httpCode = exBean.getHttpCode();
			this.errData = exBean.getData();

			flushResponseHttpCode();
		}
	}

	protected void initDefaultValue(T data) {
		this.initDefaultValue(ResponseCode.SUCCESS);
		this.data = data;
	}

	public ResponseObject() {
		super();
		
		initDefaultValue(ResponseCode.SUCCESS);
	}

	@SuppressWarnings("unchecked")
	public ResponseObject(Object data) {
		if (data instanceof ResponseCode) {
			initDefaultValue((ResponseCode) data, RequestContextFactory.getContext());
		} else if (data instanceof Exception) {
			initDefaultValue((Exception) data);
		} else {
			initDefaultValue((T) data);
		}
	}

	public ResponseObject(ResponseCode responseCode, T data) {
		initDefaultValue(responseCode);
		this.data = data;
	}

	public ResponseObject(ResponseCode responseCode) {
		initDefaultValue(responseCode);
	}
	
	public ResponseObject(ResponseCode responseCode, String message) {
		initDefaultValue(responseCode);
		this.message = message;
	}
	
	public ResponseObject(BizRuntimeException re) {
		initDefaultValue(re);
	}
	
	public ResponseObject(ValidateException ve) {
		initDefaultValue(ve);
		this.errData = ve.getValidateMessage();
	}
	
	public ResponseObject(BizRuntimeException re, Object errData) {
		initDefaultValue(re);
		this.errData = errData;
	}
	
	public ResponseObject(RuntimeException re) {
		initDefaultValue(re);
	}
	
	public ResponseObject(RuntimeException re, Object errData) {
		initDefaultValue(re);
		this.errData = errData;
	}

	/**
	 * write2 http响应
	 *
	 * @param response 响应
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/03 01:18
	 */
	public void write2HttpResponse(HttpServletResponse response) throws IOException {
		response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().print(toString());
        response.setStatus(getHttpCode());
	}

	/**
	 * flush httpResponse返回的代码
	 *
	 * @author Tequila
	 * @date 2022/07/01 23:30
	 */
	public void flushResponseHttpCode() {
		if (RequestContextFactory.getContext() != null) {
			HttpServletResponse response = RequestContextFactory.getContext().getHttpResponse();
			flushResponseHttpCode(response);
		}
	}

	/**
	 * flush httpResponse返回的代码
	 *
	 * @param response 响应
	 * @author Tequila
	 * @date 2022/07/01 23:29
	 */
	public void flushResponseHttpCode(HttpServletResponse response) {
		if (response != null) response.setStatus(getHttpCode());
	}

	public String toString() {
		try {
			return Jackson2Utils.obj2json(this);
		} catch (IOException e) {
			return String.format("{\"code\":\"%s\", \"message\":\"%s\", \"hostId\":\"%s\", \"requestId\":\"%s\", \"httpCode\":\"%s\"",
					getCode(),
					getMessage(),
					getHostId(),
					getRequestId(),
					getHttpCode());
		}
	}

	/**
	 * 判断返回对象是否返回的是成功值
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/03 01:21
	 */
	@JsonIgnore @Transient
	public boolean isSuccess() {
		return (ResponseCode.SUCCESS.code().equals(this.code));
	}

	/**
	 * 获取泛型类型
	 *
	 * @author Tequila
	 * @date 2022/07/03 22:53
	 */
	@JsonIgnore @Transient
	protected Class<T> getGenericType() {
		Class<T> dataClass = ClassUtils.getClassGenericType(this.getClass(), 0);
		if (dataClass == null) {
			throw new IllegalArgumentException("ResponseObject<T> generic type is not define");
		}
		return dataClass;
	}
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Object getErrData() {
		return errData;
	}

	public void setErrData(Object errData) {
		this.errData = errData;
	}
}
