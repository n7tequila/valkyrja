/*
 * PROJECT valkyrja2
 * util/BeanPropertyWithClassFilter.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * JsonFilterEx类过滤器
 *
 * @author Tequila
 * @create 2022/06/25 00:17
 **/
public class BeanPropertyWithClassFilter implements PropertyFilter {

	/**
	 * 用于判断属性是否要过滤的类型
	 * 
	 * @deprecated 将属性判断放在Providoer的findFilter
	 */
	@Deprecated
	protected Class<?> type;
	
	protected BeanPropertyWithClassFilter() { /* Only create by sub-class */ }
	
	public static BeanPropertyWithClassFilter serializeAll() {
        return ClassSerializeExceptFilter.INCLUDE_ALL;
    }

	public static BeanPropertyWithClassFilter filterOutAllExcept(Class<?> type, Set<String> properties) {
		return new ClassFilterExceptFilter(type, properties);
	}
	
	public static BeanPropertyWithClassFilter filterOutAllExcept(Class<?> type, String... propertyArray) {
        HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new ClassFilterExceptFilter(type, properties);
    }

	public static BeanPropertyWithClassFilter serializeAllExcept(Class<?> type, Set<String> properties) {
		return new ClassSerializeExceptFilter(type, properties);
	}
	
	public static BeanPropertyWithClassFilter serializeAllExcept(Class<?> type, String... propertyArray) {
		HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new ClassSerializeExceptFilter(type, properties);
	}
	
	/*
    /**********************************************************
    /* Methods for sub-classes
    /**********************************************************
     */
	
	/**
     * Method called to determine whether property will be included
     * (if 'true' returned) or filtered out (if 'false' returned)
     */
    protected boolean include(BeanPropertyWriter writer) {
        return true;
    }

    /**
     * Method called to determine whether property will be included
     * (if 'true' returned) or filtered out (if 'false' returned)
     * @param pojo 
     *
     * @since 2.3
     */
    protected boolean include(PropertyWriter writer) {
        return true;
    }
    
    /**
     * Method called to determine whether property will be included
     * (if 'true' returned) or filtered out (if 'false' returned)
     */
    @Deprecated
    protected boolean include(Class<?> pojoClass, BeanPropertyWriter writer) {
        return true;
    }

    /**
     * Method that defines what to do with container elements
     * (values contained in an array or {@link java.util.Collection}:
     * default implementation simply writes them out.
     * 
     * @since 2.3
     */
    protected boolean includeElement(Object elementValue) {
        return true;
    }
	
    /*
    /**********************************************************
    /* PropertyFilter implementation
    /**********************************************************
     */
    
	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
			throws Exception {
		if (include(writer)) {
			writer.serializeAsField(pojo, jgen, provider);
		} else if (!jgen.canOmitFields()) { // since 2.3
			writer.serializeAsOmittedField(pojo, jgen, provider);
		}
	}

	@Override
	public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider,
			PropertyWriter writer) throws Exception {
		if (includeElement(elementValue)) {
			writer.serializeAsElement(elementValue, jgen, provider);
		}
	}
    
	@Override
	@Deprecated
	public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider)
			throws JsonMappingException {
		if (include(writer)) {
			writer.depositSchemaProperty(propertiesNode, provider);
		}
	}

	@Override
	public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor,
			SerializerProvider provider) throws JsonMappingException {
		if (include(writer)) {
			writer.depositSchemaProperty(objectVisitor, provider);
		}
	}
	
	/*
    /**********************************************************
    /* Sub-classes
    /**********************************************************
     */

	/**
     * Filter implementation which defaults to filtering out unknown
     * properties and only serializes ones explicitly listed.
     */
	public static class ClassFilterExceptFilter extends BeanPropertyWithClassFilter implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Set of property names to serialize.
		 */
		protected final Set<String> propertiesToInclude;

		public ClassFilterExceptFilter(Class<?> type, Set<String> properties) {
			this.type = type;
			propertiesToInclude = properties;
		}

		@Override
		protected boolean include(BeanPropertyWriter writer) {
			return propertiesToInclude.contains(writer.getName());
		}

		@Override
		protected boolean include(PropertyWriter writer) {
			return propertiesToInclude.contains(writer.getName());
		}
	}
	
	/**
     * Filter implementation which defaults to serializing all
     * properties, except for ones explicitly listed to be filtered out.
     */
	public static class ClassSerializeExceptFilter extends BeanPropertyWithClassFilter implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		final static ClassSerializeExceptFilter INCLUDE_ALL = new ClassSerializeExceptFilter();

		/**
		 * Set of property names to filter out.
		 */
		protected final Set<String> propertiesToExclude;

		ClassSerializeExceptFilter() {
			propertiesToExclude = Collections.emptySet();
		}

		public ClassSerializeExceptFilter(Class<?> type, Set<String> properties) {
			this.type = type;
			propertiesToExclude = properties;
		}

		@Override
		protected boolean include(BeanPropertyWriter writer) {
			return !propertiesToExclude.contains(writer.getName());
		}
		
		@Override
		protected boolean include(PropertyWriter writer) {
			return !propertiesToExclude.contains(writer.getName());
		}
	}
}
