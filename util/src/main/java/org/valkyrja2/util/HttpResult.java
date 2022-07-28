/*
 * PROJECT valkyrja2
 * util/HttpResult.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * http请求返回对象
 *
 * @author Tequila
 * @create 2022/06/30 22:28
 **/
public class HttpResult {
	
	/** 返回httpCode */
	private int httpCode;
	
	/** 返回的header */
	private Map<String, String> headers = new HashMap<>();

	/** http请求返回的内容，如果是下载操作，则返回临时文件的地址 */
	private byte[] content;
	
	/** 数据类型 */
	private ContentType contentType;
	
	/** 数据的hash */
	private String hash;
	
	public HttpResult() {
		// nothing
	}
	
	public HttpResult(int httpCode) {
		this.httpCode = httpCode;
	}
	
	public String toString() {
		return String.format("HTTP Code: %d, %s, %s", httpCode, contentType.toString(), getContentString());
	}

	/**
	 * 添加header
	 *
	 * @param name  名字
	 * @param value 值
	 * @author Tequila
	 * @date 2022/07/01 14:45
	 */
	public void addHeader(String name, String value) {
		this.headers.put(name,value);
	}

	/**
	 * 获取内容的String值
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 14:45
	 */
	@JsonIgnore
	public String getContentString() {
		return new String(this.content, Consts.UTF_8);
	}

	/**
	 * 获取httpCode的HttpStatus类型
	 *
	 * @return {@link HttpStatus }
	 * @author Tequila
	 * @date 2022/07/01 14:42
	 */
	@JsonIgnore
	public HttpStatus getHttpStatus() {
		return HttpStatus.valueOf(this.httpCode);
	}

	/**
	 * 根据charset获取内容的String值
	 *
	 * @param charset 字符集
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 14:41
	 */
	@JsonIgnore
	public String getContentString(Charset charset) {
		return new String(this.content, charset);
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
