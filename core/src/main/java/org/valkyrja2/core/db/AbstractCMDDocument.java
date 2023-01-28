/*
 * PROJECT valkyrja2
 * core/AbstractCMDDocument.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import java.io.Serializable;

/**
 * 带有Create、Modify和Delete操作描述的mongodb映射的实体对象<br>
 * 注：拥有Delete操作描述的删除为伪删除
 *
 * @author Tequila
 * @create 2022/07/01 20:46
 **/
public abstract class AbstractCMDDocument<ID extends Serializable> extends AbstractCMDocument<ID> {

	private static final long serialVersionUID = 6271828379849308164L;

	/** 删除标记 */
	private boolean deleted = false;
	
	/** 删除操作描述 */
	private Operator deleter = null;
	
	protected AbstractCMDDocument() {
		super();
	}
	
	protected AbstractCMDDocument(ID id) {
		super(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractCMDDocument)) return false;
		if (!super.equals(o)) return false;

		AbstractCMDDocument<?> that = (AbstractCMDDocument<?>) o;

		return deleted == that.deleted;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (deleted ? 1 : 0);
		return result;
	}

	public Operator getDeleter() {
		return deleter;
	}

	public void setDeleter(Operator deleter) {
		this.deleter = deleter;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
