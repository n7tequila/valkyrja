/*
 * PROJECT valkyrja2
 * util/BeanWithClassFilterProvider.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonFilterEx类过滤器Provider
 *
 * @author Tequila
 * @create 2022/06/25 00:13
 **/
public class BeanWithClassFilterProvider extends FilterProvider	implements java.io.Serializable {

	// for 2.5+
	private static final long serialVersionUID = 1L;

	/** 根据Class配置的过滤器 */
	protected final Map<Class<?>, PropertyFilter> filtersByType;

	/** 默认如果没有定义@JsonFilter(name)，则使用默认的过滤方法 */
	protected PropertyFilter defaultFilter;

	/**
	 * 对未知筛选器是否会报错的标记位
	 * 注意：只有在没有默认过滤器时才会生效
	 */
	protected boolean cfgFailOnUnknownId = true;

	public BeanWithClassFilterProvider() {
		filtersByType = new HashMap<>();
	}


	/**
	 * 添加过滤器
	 *
	 * @param klass  对过滤器生效的Class类型
	 * @param filter 过滤器
	 * @return {@link BeanWithClassFilterProvider }
	 * @author Tequila
	 * @date 2022/06/27 15:40
	 */
	public BeanWithClassFilterProvider addFilter(Class<?> klass, PropertyFilter filter) {
		filtersByType.put(klass, filter);
		return this;
	}

	/**
	 * 根据@JsonFilter(name)的定义，查找相应的PropertyFilter对象
	 */
	@Override
	public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
		PropertyFilter f = null;
		for (Map.Entry<Class<?>, PropertyFilter> entry : filtersByType.entrySet()) {
			if (entry.getKey().isAssignableFrom(valueToFilter.getClass())) {
				f = entry.getValue();
				break;
			}
		}

		if (f == null) {
			f = defaultFilter;
			if (f == null && cfgFailOnUnknownId) {
				throw new IllegalArgumentException(
						String.format("No filter configured with id '%s' (type %s)",
								filterId, filterId.getClass().getName()));
			}
		}
		return f;
	}

	/**
	 * 新版本开始不支持BeanPropertyFilter
	 *
	 * @param filterId 过滤器id
	 * @return {@link BeanPropertyFilter }
	 * @author Tequila
	 * @date 2022/06/25 00:15
	 */
	@Override
	public BeanPropertyFilter findFilter(Object filterId) {
		throw new UnsupportedOperationException("Access to deprecated filters not supported");
	}

	/**
	 * 设置默认过滤器
	 *
	 * @param f 过滤器
	 * @return {@link BeanWithClassFilterProvider }
	 * @author Tequila
	 * @date 2022/06/27 16:03
	 */
	public BeanWithClassFilterProvider setDefaultFilter(SimpleBeanPropertyFilter f) {
		defaultFilter = f;
		return this;
	}

	public PropertyFilter getDefaultFilter() {
		return defaultFilter;
	}

	public BeanWithClassFilterProvider setFailOnUnknownId(boolean state) {
		cfgFailOnUnknownId = state;
		return this;
	}

	public boolean willFailOnUnknownId() {
		return cfgFailOnUnknownId;
	}
}
