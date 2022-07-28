/*
 * PROJECT valkyrja2
 * core/ReferenceObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * 引用对象
 *
 * @author Tequila
 * @create 2022/07/01 21:34
 **/
public class ReferenceObject<T extends AbstractDocument<?>> extends AbstractReferenceObject<T> {
	
	@DBRef(lazy = false)
	private T body;
	
	public ReferenceObject() {
		super();
	}
	
	public ReferenceObject(T t) {
		this.body = t;
	}

	@Override
	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
}
