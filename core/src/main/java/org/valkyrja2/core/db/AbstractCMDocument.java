/*
 * PROJECT valkyrja2
 * core/AbstractCMDocument.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import java.io.Serializable;

/**
 * 带有Create和Modify描述的mongodb映射的实体对象
 *
 * @author Tequila
 * @create 2022/07/01 20:46
 **/
public abstract class AbstractCMDocument<ID extends Serializable> extends AbstractDocument<ID> {

	private static final long serialVersionUID = -6032651576820833592L;

	/** 创建操作描述 */
	private Operator creator;
	
	/** 最后一次修改操作描述 */
	private Operator lastModifier;
	
	protected AbstractCMDocument() {
		super();
	}
	
	protected AbstractCMDocument(ID id) {
		super(id);
	}

	public Operator getCreator() {
		return creator;
	}

	public void setCreator(Operator creator) {
		this.creator = creator;
	}

	public Operator getLastModifier() {
		return lastModifier;
	}

	public void setLastModifier(Operator lastModifier) {
		this.lastModifier = lastModifier;
	}
}
