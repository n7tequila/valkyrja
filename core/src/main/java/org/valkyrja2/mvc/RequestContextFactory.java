/*
 * PROJECT valkyrja2
 * core/RequestContextFactory.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestContext工厂方法<br>
 * 每一次http请求都应该自动生成一个RequestContext对象
 *
 * @author Tequila
 * @create 2022/07/01 22:00
 **/
public final class RequestContextFactory {
	private static final Logger log = LoggerFactory.getLogger(RequestContextFactory.class);

	/** 当前RequestContext对象根据请求线程缓存的对象 */
	private static final ThreadLocal<RequestContext> contextThreadLocal = new ThreadLocal<>();

	/** 会话线程本地 */
	private static final ThreadLocal<Map<String, Object>> sessionThreadLocal = new ThreadLocal<>();

	/** 请求上下文中 */
	private static RequestContext requestContext;

    private RequestContextFactory() {
		throw new IllegalStateException("Factory class");
    }

	/**
	 * 获取当前请求的RequestContext对象
	 *
	 * @return {@link RequestContext }
	 * @author Tequila
	 * @date 2022/07/01 22:21
	 */
	public static RequestContext getContext() {
    	return contextThreadLocal.get();
    }

	/**
	 * 获取当前的requestId
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 22:21
	 */
	public static String getCurrentRequestId() {
    	RequestContext requestContext = getContext();
    	if (requestContext != null) {
    		return requestContext.getRequestId();
    	} else {
    		return null;
    	}
    }

	/**
	 * 根据HttpServletRequest创建一个RequestContext对象，并注册
	 *
	 * @param httpRequest http请求
	 * @param httResponse http响应
	 * @author Tequila
	 * @date 2022/07/01 22:21
	 */
	public static void registerContext(HttpServletRequest httpRequest, HttpServletResponse httResponse) {
    	RequestContext context = buildRequestContext(httpRequest, httResponse);
		registerContext(context);
    }

	/**
	 * 注册一个RequestContext对象
	 *
	 * @param context 上下文
	 * @author Tequila
	 * @date 2022/07/01 22:22
	 */
	public static void registerContext(RequestContext context) {
    	contextThreadLocal.set(context);
    	sessionThreadLocal.set(new HashMap<>());
    }

	/**
	 * 构建请求上下文
	 *
	 * @return {@link RequestContext }
	 * @author Tequila
	 * @date 2022/07/05 09:07
	 */
	private static RequestContext buildRequestContext(HttpServletRequest httpRequest, HttpServletResponse httResponse) {
		if (requestContext == null) {
			return new RequestContext(httpRequest, httResponse);
		} else {
			try {
				RequestContext context = requestContext.getClass().newInstance();
				context.init(httpRequest, httResponse);
				return context;
			} catch (InstantiationException | IllegalAccessException e) {
				log.warn(String.format("Use default RequestContext, because can not create %s, %s", requestContext.getClass().getName(), e.getMessage()));
				return new RequestContext(httpRequest, httResponse);
			}
		}
	}

	/**
	 * 释放当前请求的RequestContext对象
	 *
	 * @author Tequila
	 * @date 2022/07/01 22:22
	 */
	public static void unregistContext() {
    	contextThreadLocal.remove();
    	sessionThreadLocal.remove();
    }

	/**
	 * 有上下文
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/01 22:22
	 */
	public static boolean hasContext() {
		return (contextThreadLocal.get() != null);
	}

	/**
	 * 在会话中缓存参数
	 *
	 * @param key key
	 * @param obj obj
	 * @author Tequila
	 * @date 2022/07/01 22:23
	 */
	public static void putSession(String key, Object obj) {
		sessionThreadLocal.get().put(key, obj);
	}

	/**
	 * 获得会话当中的缓存数据
	 *
	 * @param key key
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/07/01 22:23
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSession(String key) {
		return (T) sessionThreadLocal.get().get(key);
	}

	/**
	 * 清楚会话缓存
	 *
	 * @author Tequila
	 * @date 2022/07/01 22:23
	 */
	public static void clearSession() {
		sessionThreadLocal.remove();
	}

	public static void setRequestContext(RequestContext requestContext) {
		RequestContextFactory.requestContext = requestContext;
	}
}
