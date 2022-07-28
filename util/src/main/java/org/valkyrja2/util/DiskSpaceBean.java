/*
 * PROJECT valkyrja2
 * util/DiskSpaceBean.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.io.File;

/**
 * 磁盘空间对象
 *
 * @author Tequila
 * @create 2022/06/28 16:06
 **/
public class DiskSpaceBean {
	private long _free;
	private long _usable;
	private long _total;
	private long _used;
	private int _usedPercent;
	
	private String free;
	private String usable;
	private String total;
	private String used;
	private String usedPercent;
	
	public DiskSpaceBean() {
		this(SpaceKind.B);
	}
	
	public DiskSpaceBean(SpaceKind kind) {
		init();
		calc(kind);
	}
	
	public DiskSpaceBean(String path, SpaceKind kind) {
		File file = new File(path);
		if (file.exists()) {
			init(file);
		} else {
			init();
		}
		calc(kind);
	}

	/**
	 * 默认初始化
	 *
	 * @author Tequila
	 * @date 2022/06/28 16:17
	 */
	private void init() {
		File[] roots = File.listRoots();
		if (roots != null && roots.length > 0) {
			init(roots[0]);
		} else {
			throw new IllegalArgumentException("Can not calc space");
		}
	}

	/**
	 * 指定文件初始化
	 *
	 * @param file 文件
	 * @author Tequila
	 * @date 2022/06/28 16:17
	 */
	private void init(File file) {
		this._free = file.getFreeSpace();
		this._usable = file.getUsableSpace();
		this._total = file.getTotalSpace();
		this._used = _total - _free;
		this._usedPercent = (int) ((double) _used / (double) _total * 100);
		this.usedPercent = String.valueOf(_usedPercent) + "%";
	}
	
	public void calc(SpaceKind kind) {
		long base = 1;
		switch (kind) {
			case PB: base *= 1024;
			case TB: base *= 1024;
			case GB: base *= 1024;
			case MB: base *= 1024;
			case KB: base *= 1024;
			case B: base *= 1;
		}
		
		this.free = (_free / base) + kind.name();
		this.usable = (_usable / base) + kind.name();
		this.total = (_total / base) + kind.name();
		this.used = (_used / base) + kind.name();
	}

	/**
	 * 空间类型
	 *
	 * @author Tequila
	 * @create 2022/06/28 16:15
	 **/
	public enum SpaceKind {
		B, KB, MB, GB, TB, PB; 
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getUsable() {
		return usable;
	}

	public void setUsable(String usable) {
		this.usable = usable;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getUsedPercent() {
		return usedPercent;
	}

	public void setUsedPercent(String usedPercent) {
		this.usedPercent = usedPercent;
	}
}
