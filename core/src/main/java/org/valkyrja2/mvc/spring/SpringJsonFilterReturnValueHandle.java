/*
 * PROJECT valkyrja2
 * core/SpringJsonFilterReturnValueHandle.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import com.fasterxml.jackson.core.JsonEncoding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.valkyrja2.mvc.ResponseObject;
import org.valkyrja2.util.JsonFilterEx;
import org.valkyrja2.util.JsonFilterExSerialization;
import org.valkyrja2.util.JsonFilterGroup;
import org.valkyrja2.util.JsonFilterOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * SpringMVC 返回值处理，支持使用JsonFilterEx进行返回值处理
 *
 * @author Tequila
 * @create 2022/07/14 00:09
 **/

public class SpringJsonFilterReturnValueHandle implements SpringMVCReturnValueHandler, HandlerMethodReturnValueHandler {

	private SpringMVCReturnValueAdapter adapter;

	@Override
	@SuppressWarnings("unchecked")
	public boolean supportsReturnType(MethodParameter returnType) {
		return isResponseBody(returnType)
				&& (hasAnnotation(returnType, JsonFilterGroup.class, JsonFilterEx.class));
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
		mavContainer.setRequestHandled(true);

		for (ResponseBodyAdvice<Object> ad: getAdapter().getAdvices()) {
			if (ad.supports(returnType, null)) {
				returnValue = ad.beforeBodyWrite(returnValue, returnType, MediaType.APPLICATION_JSON, null,
						new ServletServerHttpRequest(webRequest.getNativeRequest(HttpServletRequest.class)),
						new ServletServerHttpResponse(webRequest.getNativeResponse(HttpServletResponse.class)));
			}
		}

		ServletServerHttpResponse outputMessage = getAdapter().createOutputMessage(webRequest);
		outputMessage.getServletResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
		if (returnValue instanceof ResponseObject) {
			outputMessage.getServletResponse().setStatus(((ResponseObject<?>) returnValue).getHttpCode());
		}

		Annotation[] annos = returnType.getMethodAnnotations();
		JsonFilterOption option = JsonFilterExSerialization.fromAnnotations(annos);
		
		JsonFilterExSerialization jfex = new JsonFilterExSerialization(returnValue, option);
		jfex.createJsonGenerator(outputMessage.getBody(), getJsonEncoding(outputMessage.getHeaders().getContentType()));
		jfex.write();
	}
	
	protected JsonEncoding getJsonEncoding(MediaType contentType) {
		if (contentType != null && contentType.getCharset() != null) {
			Charset charset = contentType.getCharset();
			for (JsonEncoding encoding : JsonEncoding.values()) {
				if (encoding.getJavaName().equals(charset.name())) {
					return encoding;
				}
			}
		}
		return JsonEncoding.UTF8;
	}

	public SpringMVCReturnValueAdapter getAdapter() {
		if (this.adapter == null) {
			this.adapter = SpringUtils.getBean(SpringMVCReturnValueAdapter.class);
		}
		return adapter;
	}

	public void setAdapter(SpringMVCReturnValueAdapter adapter) {
		this.adapter = adapter;
	}
}
