/*
 * PROJECT valkyrja2
 * core/RequestContext.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.valkyrja2.security.SecurityConst;
import org.valkyrja2.util.NetworkUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Security;
import java.util.Date;
import java.util.UUID;

/**
 * http请求的上下文数据<br>
 *
 * 包含：<br>
 * <pre>
 * requestId        请求Id		每次请求自动生成
 * hostId           服务器名称 	读取计算机名
 * hostIPAddress    服务器IP地址	读取当前计算机的IP地址
 * receiveDate      数据接收的时间	系统时间
 * remoteIPAddress  请求IP地址	从HttpServletRequest中读取
 * httpRequest      Request对象	HttpServletRequest
 * </pre>
 *
 * @author Tequila
 * @create 2022/07/01 21:50
 **/
public class RequestContext {

	/** 请求Id */
	private String requestId;
	
	/** 服务器名 */
	private String hostId;
	
	/** 服务器名IP地址 */
	private String hostIPAddress;
	
	/** 数据接收时间 */
	private Date receiveDate;
	
	/** 请求对象的IP地址 */
	private String remoteIPAddress;

	/** HTTP请求对象 */
	private HttpServletRequest httpRequest;
	
	/** HTTP返回对象 */
	private HttpServletResponse httpResponse;

	public RequestContext() {
		this(null, null);
	}
	
	public RequestContext(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		init(httpRequest, httpResponse);
	}

	/**
	 * 初始化
	 *
	 * @param httpRequest  http请求
	 * @param httpResponse http响应
	 * @author Tequila
	 * @date 2022/07/05 09:09
	 */
	public void init(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;

		this.requestId = UUID.randomUUID().toString();
		this.hostId = NetworkUtils.getHostName();
		this.hostIPAddress = NetworkUtils.getHostIP();
		this.receiveDate = new Date(System.currentTimeMillis());

		if (httpRequest != null) {
			this.remoteIPAddress = NetworkUtils.getRemoteIPAddress(httpRequest);
		}
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getHostId() {
		return hostId;
	}

	public String getHostIPAddress() {
		return hostIPAddress;
	}
	
	public Date getReceiveDate() {
		return receiveDate;
	}

	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}
}
