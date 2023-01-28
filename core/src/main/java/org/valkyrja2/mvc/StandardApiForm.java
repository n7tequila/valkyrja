/*
 * PROJECT valkyrja2
 * core/StandardApiForm.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.component.api.AccessKeyForm;
import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.util.Jackson2Utils;
import org.valkyrja2.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

/**
 * 标准API交互接口Form
 *
 * @author Tequila
 * @create 2022/07/19 14:19
 **/
public class StandardApiForm<T extends FormObject> extends AbstractFormObject implements AccessKeyForm, RequestTimeForm {
	private static final Logger log = LoggerFactory.getLogger(StandardApiForm.class);

	private static final String ERR_BODY_FIELD = "body";
	private static final String ERR_BODY_CONVERT = "无法转换成目标对象";

	/** 接入标识，由接口发布者提供 */
	@NotBlank(groups = ApiFieldRequired.class)
	private String appCode;
	
	/** 请求操作的数据实体 */
	@NotBlank(groups = ApiFieldRequired.class)
	private String body;

	@JsonIgnore @JsonBackReference
	private T bodyForm;

	/** 杂数字符串 */
	private String nonceStr;

	/** 操作发生时间（UTC 时间戳格式 timestamp，精确到毫秒） */
	@NotBlank(groups = ApiFieldRequired.class)
	private String time;

	/**
	 * 数据签名
	 * 将所有传输的参数及双方约定的SignKey拼接后进行sha-256运算
	 * 具体签名方法根据不同接口单独定义
	 */
	@NotBlank(groups = ApiFieldRequired.class)
	private String sign;
	
	public StandardApiForm() {
		super();
	}

	public StandardApiForm(String appCode) {
		this.appCode = appCode;
	}

	public StandardApiForm(String appCode, T bodyForm, String nonceStr, String time, String sign) {
		this.appCode = appCode;
		this.bodyForm = bodyForm;
		this.nonceStr = nonceStr;
		this.time = time;
		this.sign = sign;
	}

	/**
	 * 生成验签数据原文
	 * 由继承的子类自己实现
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/19 13:42
	 */
	protected String buildSignatureRaw() {
		return concatParams(appCode, body, nullableField(nonceStr), getTime());
	}

	/**
	 * 转换可以为空的字段为""
	 *
	 * @param value 值
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/19 13:43
	 */
	protected String nullableField(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		} else {
			return value;
		}
	}

	/**
	 * 合并参数
	 *
	 * @param strs 参数字符串数组
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/19 13:44
	 */
	protected String concatParams(String ... strs) {
		StringBuilder sb = new StringBuilder();
		for (String s: strs) {
			sb.append(nullableField(s));
		}
		return sb.toString();
	}

	/**
	 * 签名是否有效
	 *
	 * @param signKey 标志key
	 * @return boolean
	 * @author Tequila
	 * @date 2022/08/01 21:12
	 */
	public boolean isSignatureValid(String signKey) {
		String raw = buildSignatureRaw();
		String correctSign = DigestUtils.sha256Hex(raw + signKey);
		if (!correctSign.equalsIgnoreCase(sign)) {
			log.debug("========== 正确的API签名: {}", correctSign);
		}
		return (correctSign.equalsIgnoreCase(sign));
	}

	/**
	 * 转换body数据并检查表单数据是否合法
	 *
	 * @param bodyClass body类
	 * @param groups    groups
	 * @return {@link FormValidResult }
	 * @author Tequila
	 * @date 2022/07/19 14:20
	 */
	@JsonIgnore
	public FormValidResult isBodyValid(Class<T> bodyClass, Class<?> ... groups) {
		bodyForm = parseBodyForm(bodyClass);
		return isFormValid(bodyForm, groups);
	}

	/**
	 * 解析bodyForm的json对象
	 *
	 * @param bodyClass 体类
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/08/01 20:36
	 */
	public T parseBodyForm(Class<T> bodyClass) {
		try {
			return Jackson2Utils.json2obj(this.body, bodyClass);
		} catch (IOException e) {
			throw new ValidateException(ERR_BODY_FIELD, ERR_BODY_CONVERT);
		}
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public T getBodyForm() {
		return bodyForm;
	}

	public void setBodyForm(T bodyForm) {
		this.bodyForm = bodyForm;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * api所需字段
	 *
	 * @author Tequila
	 * @create 2022/07/19 13:40
	 **/
	public interface ApiFieldRequired {}
}
