/*
 * PROJECT valkyrja2
 * util/JsonFilterOption.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * json过滤选项，用于将一个对象的所有JsonFilterEx注释都统一获取
 *
 * @author Tequila
 * @create 2022/06/25 00:56
 **/
public class JsonFilterOption {

	/** 选项 */
	private List<JsonFilterEx> options;
	
	public JsonFilterOption() {
		options = new ArrayList<>();
	}
	
	public JsonFilterOption(JsonFilterEx filter) {
		this();
		
		addFilter(filter);
	}
	
	public JsonFilterOption(JsonFilterEx[] filters) {
		this();
		
		addFilters(filters);
	}

	/**
	 * 添加过滤器
	 *
	 * @param filter 过滤器
	 * @author Tequila
	 * @date 2022/06/25 00:57
	 */
	public void addFilter(JsonFilterEx filter) {
		options.add(filter);
	}

	/**
	 * 添加多个过滤器
	 *
	 * @param filters 过滤器
	 * @author Tequila
	 * @date 2022/06/25 00:57
	 */
	public void addFilters(JsonFilterEx[] filters) {
		for (JsonFilterEx filter: filters) {
			addFilter(filter);
		}
	}

	public List<JsonFilterEx> getOptions() {
		return options;
	}

	public void setOptions(List<JsonFilterEx> options) {
		this.options = options;
	}

	
}
