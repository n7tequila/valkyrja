/*
 * PROJECT valkyrja2
 * core/AbstractFormObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.util.JsonObject;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;


/**
 * Form对象特殊定义了从前台向后台（或外部平台调用接口）传递数据的对象
 *
 * @author Tequila
 * @create 2022/07/01 22:35
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractFormObject implements FormObject, JsonObject {
	
	public static final String VALIDATE_TYPE_ERROR = "格式错误";
	
	public static final String VALIDATE_NOT_BLANK = "数据必填";
	
	public static final String VALIDATE_NOT_NULL = "不能为空";
	
	protected AbstractFormObject() {
		super();
	}

	/**
	 * 检查表单数据是否合法
	 *
	 * @param form   表单
	 * @param groups 校验的group信息
	 * @return {@link FormValidResult }
	 * @author Tequila
	 * @date 2022/07/04 09:14
	 */
	@JsonIgnore
	protected FormValidResult isFormValid(FormObject form, Class<?> ... groups) {
		try ( ValidatorFactory factory = Validation.buildDefaultValidatorFactory(); ) {
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<Object>> violations = validator.validate(form, groups);
			return new FormValidResult(violations);
		}
	}

	/**
	 * 检查表单数据是否合法，不合法则抛出错误
	 *
	 * @param form   表单
	 * @param groups 校验的group信息
	 * @author Tequila
	 * @date 2022/07/04 09:19
	 */
	protected void checkFormValid(AbstractFormObject form, Class<?> ... groups) {
		FormValidResult formValidResult = isFormValid(form, groups);
		if (formValidResult.isNotValid()) {
			throw new ValidateException(formValidResult.getViolations());
		}
	}

	/** 标准必须字段 **/
	public static interface FieldRequired {}
}
