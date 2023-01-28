/*
 * PROJECT valkyrja2
 * core/ReferenceLazyObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * 对象引用，懒加载
 *
 * @author Tequila
 * @create 2022/07/01 21:36
 **/
public class ReferenceLazyObject<T extends AbstractDocument<?>> extends AbstractReferenceObject<T> {
	
	@DBRef(lazy = true)
	private T body;
	
	public ReferenceLazyObject() {
		super();
	}
	
	public ReferenceLazyObject(T t) {
		this.body = t;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ReferenceLazyObject)) return false;

		ReferenceLazyObject<?> that = (ReferenceLazyObject<?>) o;

		return body.equals(that.body);
	}

	@Override
	public int hashCode() {
		return body.hashCode();
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
	
}
