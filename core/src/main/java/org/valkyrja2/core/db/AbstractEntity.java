/*
 * PROJECT valkyrja2
 * core/AbstractEntity.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import java.io.Serializable;
import java.util.Objects;

/**
 * 所有数据库实体对象的父类
 * @param <ID> 唯一主键类型
 *
 * @author Tequila
 * @create 2022/07/01 20:26
 **/
public abstract class AbstractEntity<ID extends Serializable> implements java.io.Serializable {

	private static final long serialVersionUID = -388004184373531948L;

	protected AbstractEntity() {
		super();
	}

	protected AbstractEntity(ID id) {
		setId(id);
	}

	/**
	 * 返回数据的id
	 * 
	 * @return the id
	 */
	public abstract ID getId();

	/**
	 * 设置数据的id，但是只能从方法内部进行设置，不能由外部方法进行修改。
	 *
	 * @param id id
	 * @author Tequila
	 * @date 2022/07/01 20:26
	 */
	protected abstract void setId(ID id);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractEntity<?> that = (AbstractEntity<?>) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
