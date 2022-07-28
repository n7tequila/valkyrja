/*
 * PROJECT valkyrja2
 * core/IdObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.domain;

/**
 * 具有id的对象定义
 *
 * @author Tequila
 * @create 2022/07/01 20:31
 **/
public interface IdObject<T> {

	/**
	 * 获取id
	 *
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/07/01 20:32
	 */
	T getId();
}
