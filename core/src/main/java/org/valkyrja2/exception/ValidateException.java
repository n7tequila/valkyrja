/*
 * PROJECT valkyrja2
 * core/ValidateException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.*;
import org.valkyrja2.mvc.ResponseCode;

import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * 数据校验错误所抛出的RuntimeException
 * 
 * @author Tequila
 *
 */
@JsonPropertyOrder(value = {"code", "httpCode", "requestContext", "cause"})
public class ValidateException extends BizRuntimeException {
	
	private static final long serialVersionUID = 1214876356885167870L;

	/** 报错的字段列表 */
	private final List<ValidateMessage> validateMessage = new ArrayList<>();

	/**
	 * 验证异常
	 *
	 * @param field   场
	 * @param message 消息
	 * @author Tequila
	 * @date 2022/07/03 00:26
	 */
	public ValidateException(String field, String message) {
		super(ResponseCode.VALIDATE_ERROR);

		validateMessage.add(new ValidateMessage(field, message));
	}

	/**
	 * 验证议程，MethodArgumentNotValidException
	 * Spring MVC Controller无法转换参数时的报错
	 *
	 * @param e 运行期错误
	 * @author Tequila
	 * @date 2022/07/03 00:26
	 */
	public ValidateException(MethodArgumentNotValidException e) {
		super(ResponseCode.VALIDATE_ERROR, e);
		
		for (ObjectError objError: e.getBindingResult().getAllErrors()) {
			Object[] objs = objError.getArguments();
			if (objs != null && objs.length > 0) {
				DefaultMessageSourceResolvable message = (DefaultMessageSourceResolvable) objs[0];
				validateMessage.add(new ValidateMessage(message.getCode(), objError.getDefaultMessage()));
			}
		}
	}

	/**
	 * 验证异常
	 *
	 * @param e ServletRequestBindingException
	 * @author Tequila
	 * @date 2022/07/03 00:27
	 */
	public ValidateException(ServletRequestBindingException e) {
		super(ResponseCode.VALIDATE_ERROR, e);
		
		if (UnsatisfiedServletRequestParameterException.class.isAssignableFrom(e.getClass())) {
			UnsatisfiedServletRequestParameterException ex = (UnsatisfiedServletRequestParameterException) e;
			validateMessage.add(new ValidateMessage("actualParams", ex.getActualParams()));
		} else {
			String field = null;
			String message = null;
			try {
				BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(e);
				MethodParameter param = (MethodParameter) wrapper.getPropertyValue("parameter");
				if (param != null) {
					field = param.getParameterName();
					message = "missing";
				}
			} catch (Exception ex) {
				switch (e.getClass().getSimpleName()) {
					case "MissingRequestHeaderException" :
						field = ((MissingRequestHeaderException) e).getHeaderName();
						break;
					case "MissingPathVariableException" :
						field = ((MissingPathVariableException) e).getVariableName();
						break;
					case "MissingMatrixVariableException" :
						field = ((MissingMatrixVariableException) e).getVariableName();
						break;
					case "MissingRequestCookieException" :
						field = ((MissingRequestCookieException) e).getCookieName();
						break;
					case "MissingServletRequestParameterException" :
						field = ((MissingServletRequestParameterException) e).getParameterName();
						break;
					default:
						field = "unknown_field";
				}
				message = e.getMessage();
			}
				
			validateMessage.add(new ValidateMessage(field, message));
		}
	}

	/**
	 * 验证异常
	 *
	 * @param re HttpMessageConversionException
	 * @author Tequila
	 * @date 2022/07/03 00:29
	 */
	public ValidateException(HttpMessageConversionException e) {
		super(ResponseCode.VALIDATE_ERROR, e);
		
		if (e.getCause() instanceof JsonMappingException) {
			JsonMappingException jme = (JsonMappingException) e.getCause();
			for (Reference reference: jme.getPath()) {
				validateMessage.add(new ValidateMessage(reference.getFieldName(), "数据格式错误"));
			}
		}
	}

	/**
	 * 验证异常
	 *
	 * @param violations 违反
	 * @author Tequila
	 * @date 2022/07/03 00:29
	 */
	public ValidateException(Set<ConstraintViolation<Object>> violations) {
		super(ResponseCode.VALIDATE_ERROR);

		addValidateMessage(violations);
	}

	public ValidateException(ResponseCode responseCode) {
		super(responseCode);
	}
	
	public ValidateException(ResponseCode responseCode, String message) {
		super(responseCode, message);
	}
	
	public ValidateException(ResponseCode responseCode, Throwable cause) {
		super(responseCode, cause);
	}
	
	public ValidateException(ResponseCode responseCode, String message, Throwable cause) {
		super(responseCode, message, cause);
	}

	/**
	 * 添加验证消息
	 *
	 * @param violations 违反
	 * @author Tequila
	 * @date 2022/07/03 00:30
	 */
	public void addValidateMessage(Set<ConstraintViolation<Object>> violations) {
		violations.forEach(violation -> {
			validateMessage.add(new ValidateMessage(violation.getPropertyPath().toString(), violation.getMessage()));
		});
	}

	/**
	 * 添加验证消息
	 *
	 * @param field   场
	 * @param message 消息
	 * @author Tequila
	 * @date 2022/07/03 00:31
	 */
	public void addValidateMessage(final String field, final String message) {
		validateMessage.add(new ValidateMessage(field, message));
	}

	public List<ValidateMessage> getValidateMessage() {
		return validateMessage;
	}

	/**
	 * 验证消息
	 *
	 * @author Tequila
	 * @create 2022/07/03 00:43
	 **/
	public static class ValidateMessage {
		private final String field;

		private final Object message;

		public ValidateMessage(String field, Object message) {
			this.field = field;
			this.message = message;
		}

		public String getField() {
			return field;
		}

		public Object getMessage() {
			return message;
		}
	}
}
