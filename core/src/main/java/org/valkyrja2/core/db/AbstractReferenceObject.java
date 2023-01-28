/*
 * PROJECT valkyrja2
 * core/AbstractReferenceObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

/**
 * 引用对象的抽象类
 *
 * @author Tequila
 * @create 2022/07/01 21:08
 **/
public abstract class AbstractReferenceObject<T extends AbstractDocument<?>> {
	
	protected AbstractReferenceObject() {
		super();
	}

	/**
	 * 获取引用对象的实体
	 *
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/07/01 21:09
	 */
	@JsonBackReference
	public abstract T getBody();

	/**
	 * 获取引用对象的实体的id
	 *
	 * @return {@link ID }
	 * @author Tequila
	 * @date 2022/07/01 21:30
	 */
	@SuppressWarnings("unchecked")
	@Transient @JsonIgnore
	public <ID> ID getId() {
		if (getBody() != null) {
			return (ID) this.getBody().getId();
		}
	
		return null;
	}

	/**
	 * 返回引用对象是否为空
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/01 21:30
	 */
	@Transient @JsonIgnore
	public boolean isBodyNull() {
		/* 如果body为null，则返回true */
		return (getBody() == null);
	}

	/**
	 * 返回引用对象是否不为空
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2023/01/16 10:32
	 */
	@Transient @JsonIgnore
	public boolean isBodyNotNull() {
		return !isBodyNull();
	}

	@Override
	public String toString() {
		if (getBody() != null) {
			return String.format("%s.%s(%s)", this.getClass().getSimpleName(), this.getBody().getClass().getSimpleName(), getId());
		} else {
			return String.format("%s.null(null)", this.getClass().getSimpleName());
		}
	}
}
