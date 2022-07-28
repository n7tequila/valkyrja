/*
 * PROJECT valkyrja2
 * core/FormValidResult.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * 通过java bean validation校验后的自定义校验结果
 *
 * @author Tequila
 * @create 2022/07/04 09:13
 **/
public class FormValidResult {

	/** 违反规则的列表 */
	private Set<ConstraintViolation<Object>> violations;
	
	public FormValidResult(Set<ConstraintViolation<Object>> violations) {
		this.violations = violations;
	}
	
	public boolean isValid() {
		return violations.isEmpty();
	}

	public Set<ConstraintViolation<Object>> getViolations() {
		return violations;
	}

	public void setViolations(Set<ConstraintViolation<Object>> violations) {
		this.violations = violations;
	}
}
