/*
 * PROJECT valkyrja2
 * util/DiskUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;


import org.valkyrja2.util.DiskSpaceBean.SpaceKind;

/**
 * 磁盘工具包
 *
 * @author Tequila
 * @create 2022/06/28 16:26
 **/
public class DiskUtils {

	private DiskUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 获取磁盘空间信息
	 *
	 * @param kind 种类
	 * @return {@link DiskSpaceBean }
	 * @author Tequila
	 * @date 2022/06/28 16:27
	 */
	public static DiskSpaceBean getDiskSpaceInfo(SpaceKind kind) {
		return new DiskSpaceBean(kind);
	}

	/**
	 * 获取磁盘空间信息，默认GB
	 *
	 * @return {@link DiskSpaceBean }
	 * @author Tequila
	 * @date 2022/06/28 16:27
	 */
	public static DiskSpaceBean getDiskSpaceInfo() {
		return new DiskSpaceBean(SpaceKind.GB);
	}
}
