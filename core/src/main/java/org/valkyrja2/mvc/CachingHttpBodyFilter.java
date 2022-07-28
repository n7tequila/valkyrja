/*
 * PROJECT valkyrja2
 * core/CachingHttpBodyFilter.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 提供重写HttpServletRequestWrapper的Filter模式<br>
 * 可以使用MultipleReadHttpRequest来解决HttpServletRequest中getReader和getInputerStream方法只能执行一次的问题。<br>
 * web.xml配置<br>
 * <pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;cacheFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;org.valkyrja.framework.core.CachingHttpBodyFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;cacheFilter&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author Tequila
 * @version 1.0
 * 
 */
public class CachingHttpBodyFilter extends GenericFilterBean {
	
	private static final String TRUE_VALUE = "true";
	private static final String TRUE_INT_VALUE = "1";
	
	/**
	 * web.xml中配置是否允许替换HttpServletRequest的参数名
	 */
	private static final String REPLACE_REQUEST = "httpRequest";
	
	/**
	 * web.xml中配置是否允许替换HttpServletResponse的参数名
	 */
	public static final String REPLACE_RESPONSE = "httpResponse";
	
	/**
	 * web.xml中配置是否允许跨域访问的参数名
	 */
	public static final String ALLOW_CROSS_DOMAIN = "allowCrossDomain";
	
	/**
	 * web.xml中配置跨域的域名
	 */
	public static final String CROSS_DOMAIN = "crossDomain";
	
	/**
	 * web.xml中配置跨域的header
	 */
	public static final String CROSS_HEADERS = "crossHeaders";
	
	/**
	 * web.xml中配置跨域的method
	 */
	public static final String CROSS_METHODS = "crossMethods";
	
	/**
	 * 默认的跨域允许的范围
	 */
	public static final String DEFAULT_CROSS_DOMAIN = "*";

	/**
	 * 默认的跨域允许的数据类型
	 */
	public static final String DEFAULT_CROSS_HEADERS = "X-PINGOTHER, Content-Type, x-requested-with, Authorization";
	
	/**
	 * 默认跨域允许的Http操作类型
	 */
	public static final String DEFAULT_CROSS_METHODS = "POST, GET, PUT, DELETE, OPTIONS";
	
	/**
	 * 替换HttpServletRequest操作方法
	 */
	private boolean replaceHttpRequest = true;
	
	/**
	 * 替换HttpServletResponse操作方法
	 */
	private boolean replaceHttpResponse = true;
	
	/**
	 * 支持跨域访问
	 */
	private boolean allowCrossDomain = false;
	
	/**
	 * 允许跨域的域名
	 */
	private String crossDomain = DEFAULT_CROSS_DOMAIN;
	
	/**
	 * 允许跨域的头
	 */
	private String crossHeaders = DEFAULT_CROSS_HEADERS;
	
	/**
	 * 
	 */
	private String crossMethods = DEFAULT_CROSS_METHODS;
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		
		initParameter();
	}
	
	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
		ServletRequest request;
		if (replaceHttpRequest) {
			request = new MultipleReadHttpRequest((HttpServletRequest) servletRequest);
		} else {
			request = servletRequest;
		}
        
		/* 设置跨越访问 */
		if (allowCrossDomain == true) {
	         ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", crossDomain);
	         ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", crossHeaders);
	         ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods", crossMethods);
		}
		
		ServletResponse response = null;
		if (replaceHttpResponse == true) {
			response = new CachingHttpResponseWrapper((HttpServletResponse) servletResponse);
		} else {
			response = servletResponse;
		}
        
		chain.doFilter(request, response);
    }
	
	/**
	 * 初始化过滤器中的所有参数
	 */
	public void initParameter() {
		replaceHttpRequest = getBooleanInitParameter(REPLACE_REQUEST, true);
		replaceHttpResponse = getBooleanInitParameter(REPLACE_RESPONSE, true);
		allowCrossDomain = getBooleanInitParameter(ALLOW_CROSS_DOMAIN, false);
		crossDomain = getStringInitParameter(CROSS_DOMAIN, DEFAULT_CROSS_DOMAIN);
		crossHeaders = getStringInitParameter(CROSS_HEADERS, DEFAULT_CROSS_HEADERS);
		crossMethods = getStringInitParameter(CROSS_METHODS, DEFAULT_CROSS_METHODS);
	}
	
	private boolean getBooleanInitParameter(String paramName, boolean defaultValue) {
		String paramValue = getFilterConfig().getInitParameter(paramName);
		if (paramValue == null) return defaultValue;
		return (TRUE_VALUE.equalsIgnoreCase(paramValue) || TRUE_INT_VALUE.equalsIgnoreCase(paramValue));
	}
	
	private String getStringInitParameter(String paramName, String defaultValue) {
		String paramValue = getFilterConfig().getInitParameter(paramName);
		if (paramValue == null) return defaultValue;
		return paramValue;
	}
}
