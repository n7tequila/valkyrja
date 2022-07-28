/*
 * PROJECT valkyrja2
 * core/ControllerInterceptorHandler.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.valkyrja2.mvc.RequestContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ListIterator;

/**
 * Controller对象初始化拦截器，默认每一个RequestMapping都初始化RequestContext对象<br>
 * Spring配置
 * spring配置
 * <pre>
 * &lt;mvc:interceptors&gt;
 *   &lt;mvc:interceptor&gt;
 *     &lt;mvc:mapping path="/services/**" /&gt;
 *     &lt;bean class="org.valkyrja.framework.core.spring.ControllerInterceptorAdapter"&gt;
 *     &lt;/bean&gt;
 *   &lt;/mvc:interceptor&gt;
 * &lt;/mvc:interceptors&gt;
 * </pre>
 *
 * @author Tequila
 *
 */
public class ControllerInterceptorHandler implements AsyncHandlerInterceptor {

	private List<AsyncHandlerInterceptor> interceptors;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		RequestContextFactory.registerContext(request, response); // 默认注册RequestContext

		if (interceptors != null && !interceptors.isEmpty()) {
			for (AsyncHandlerInterceptor interceptor: interceptors) {
				interceptor.preHandle(request, response, handler);
			}
		}

		return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (interceptors != null && !interceptors.isEmpty()) {
			ListIterator<AsyncHandlerInterceptor> li = interceptors.listIterator(interceptors.size());
			while (li.hasPrevious()) {
				AsyncHandlerInterceptor interceptor = li.previous();
				interceptor.postHandle(request, response, handler, modelAndView);
			}
		}

		AsyncHandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		ListIterator<AsyncHandlerInterceptor> li = interceptors.listIterator(interceptors.size());
		while (li.hasPrevious()) {
			AsyncHandlerInterceptor interceptor = li.previous();
			interceptor.afterCompletion(request, response, handler, ex);
		}

		RequestContextFactory.unregistContext();

		AsyncHandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

	public List<AsyncHandlerInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<AsyncHandlerInterceptor> interceptors) {
		this.interceptors = interceptors;
	}
}
