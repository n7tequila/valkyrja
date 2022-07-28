/*
 * PROJECT valkyrja2
 * core/AbstractDocument.java
 * Copyright (c) 2022 Tequila.Yang
 */
package org.valkyrja2.core.db;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * 文档式数据的抽象类<br>
 * 主要用在mongodb类似的数据库上
 *
 * @author Tequila
 * @create 2022/07/01 20:35
 **/
public abstract class AbstractDocument<ID extends Serializable> extends AbstractEntity<ID> {

	private static final long serialVersionUID = 8773389400630877237L;

	@Id
	private ID id;

	protected AbstractDocument() {
		super();
	}

	protected AbstractDocument(ID id) {
		super(id);
	}

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}
}
