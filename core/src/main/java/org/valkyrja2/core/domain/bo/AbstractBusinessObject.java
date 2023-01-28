/*
 * PROJECT valkyrja2
 * core/AbstractBusinessObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.domain.bo;

import org.springframework.data.repository.Repository;
import org.valkyrja2.core.db.AbstractEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * BusinessObject的抽象类
 *
 * @param <T> 实体对象
 * @param <ID> 实体的主键类型
 * @param <REPO> repository类定义
 *
 * @author Tequila
 * @create 2022/07/01 21:46
 **/
public abstract class AbstractBusinessObject<T extends AbstractEntity<ID>, ID extends Serializable, REPO extends Repository<T, ID>> {

	/** 当前业务逻辑对象的实体对象 */
	protected T entity;

	/** repository对象 */
	protected REPO repo;
	
	protected AbstractBusinessObject() {
		// nothing
	}

	protected AbstractBusinessObject(T entity) {
		this.setEntity(entity);
	}

	protected AbstractBusinessObject(T entity, REPO repo) {
		this.setEntity(entity);
		this.setRepo(repo);
	}

	/**
	 * 新增保存
	 *
	 * @author Tequila
	 * @date 2022/07/01 21:43
	 */
	public abstract void save();

	/**
	 * 删除
	 *
	 * @author Tequila
	 * @date 2022/07/01 21:43
	 */
	public abstract void delete();

	/**
	 * 获取当前对象的id<br>
	 *
	 * @return {@link ID }
	 * @author Tequila
	 * @date 2022/07/01 21:41
	 */
	public ID getId() {
		if (entity != null) {
			return ((AbstractEntity<ID>) entity).getId();
		}
		
		return null;
	}

	/**
	 * 得到实体对象
	 *
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/07/13 18:02
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * 设置实体对象，同时设置id
	 * 如需清空实体对象，请调用<code>clear()</code>方法
	 *
	 * @param entity 实体
	 * @author Tequila
	 * @date 2022/07/13 18:02
	 */
	public void setEntity(T entity) {
		Objects.requireNonNull(entity, "entity must not be null");

		this.entity = entity;
	}

	/**
	 * 获得repository对象
	 *
	 * @return {@link REPO }
	 * @author Tequila
	 * @date 2022/07/13 19:49
	 */
	public REPO getRepo() {
		return repo;
	}

	/**
	 * 设置repository对象
	 *
	 * @param repo 回购
	 * @author Tequila
	 * @date 2022/07/13 19:49
	 */
	public void setRepo(REPO repo) {
		Objects.requireNonNull(repo, "repo must not be null");

		this.repo = repo;
	}

	/**
	 * 清空entity对象
	 *
	 * @author Tequila
	 * @date 2022/07/13 18:07
	 */
	public void clear() {
		this.entity = null;
	}
}
