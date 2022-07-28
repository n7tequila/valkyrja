/*
 * PROJECT valkyrja2
 * util/JsonFilterExSerialization.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * json过滤器前序列化
 *
 * @author Tequila
 * @create 2022/06/25 00:04
 **/
public class JsonFilterExSerialization {

    /** 默认过滤器前 */
    private static final String DEFAULT_FILTER_EX = "JsonFilterEx";

    /** 默认字符集 */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /** 漂亮打印 */
    private boolean prettyPrint = false;

    /** 默认输出json的ObjectMapper对象 */
    private final ObjectMapper objectMapper;

    /** 默认字符集 */
    private Charset defaultCharset;

    /** json输出对象 */
    private JsonGenerator generator;
    
    /** 生成json数据的选项 */
    private JsonFilterOption option;
    
    /** 当前json序列化的对象 */
    private Object serializeValue;

    /**
     * 构造一个JsonFilterExSerialization对象，初始化重要变量
     *
     * @author Tequila
     * @date 2022/06/25 00:05
     */
    public JsonFilterExSerialization() {
        this.objectMapper = new ObjectMapper();
        this.defaultCharset = DEFAULT_CHARSET;
        this.option = new JsonFilterOption();
    }

    /**
     * 通过serializeValue构造一个JsonFilterExSerialization对象，并根据serializeValue中的定义配置JsonFilterOptions
     *
     * @param serializeValue 序列化对象
     * @author Tequila
     * @date 2022/06/25 00:05
     */
    public JsonFilterExSerialization(Object serializeValue) {
        this();
        this.serializeValue = serializeValue;
        
        initFilterConfig(serializeValue);
        initObjectMapper();
    }

    /**
     * json过滤器前序列化
     *
     * @param serializeClass 序列化类
     * @author Tequila
     * @date 2022/06/26 02:03
     */
    public JsonFilterExSerialization(Class<?> serializeClass) {
        this();

        initFilterConfig(serializeClass);
        initObjectMapper();
    }

    /**
     * 通过serializeValue和JsonFilterOptions构造一个JsonFilterExSerialization对象
     *
     * @param serializeValue 序列化值
     * @param option         选项
     * @author Tequila
     * @date 2022/06/25 00:06
     */
    public JsonFilterExSerialization(Object serializeValue, JsonFilterOption option) {
        this();
        this.serializeValue = serializeValue;
        this.option = option;
        
        initObjectMapper();
    }

    /**
     * json过滤器前序列化
     *
     * @param serializeValue 序列化值
     * @param annotations    注释
     * @author Tequila
     * @date 2022/06/25 00:06
     */
    public JsonFilterExSerialization(Object serializeValue, Annotation[] annotations) {
        this();
        this.serializeValue = serializeValue;
        this.option = fromAnnotations(annotations);
        
        initObjectMapper();
    }

    /**
     * 初始化JsonFilterEx配置信息
     *
     * @param obj obj
     * @author Tequila
     * @date 2022/06/25 00:06
     */
    private void initFilterConfig(Object obj) {
        Class<?> klass;
        if (obj instanceof Class) {
            klass = (Class<?>) obj;
        } else {
            klass = obj.getClass();
        }

        if (!isJsonFilterEx(klass)) {  // 判断对象是否支持JsonFilterEx操作
            throw new UnsupportedOperationException("Can not found JsonFilterEx or JsonFilterGroup annotation declare in class " + obj.getClass() + ".");
        }
        
        /* 从序列化对象的配置中获取@JsonFilterEx和@JsonFilterGroup对象，并转换成JsonFilterOption对象 */
        JsonFilterGroup[] filterGroups = getJsonFilterGroupAnnotation(klass);
        if (filterGroups.length > 0) {
            for (JsonFilterGroup group: filterGroups) {
                option.addFilters(group.value());
            }
        } else {
            JsonFilterEx[] filters = getJsonFilterExAnnotation(klass);
            option.addFilters(filters);
        }
    }
    
    /**
     * 初始化ObjectMapp的FilterProvider对象
     */
    private void initObjectMapper() {
        /* 将所有配置对象的Class都配置到MixIn中，用于mapper对所有MixIn的对象进行Filter判断 */
        BeanWithClassFilterProvider filterProvider = new BeanWithClassFilterProvider();
        for (JsonFilterEx filter: option.getOptions()) {
            objectMapper.addMixIn(filter.type(), JsonFilterExMixIn.class);
            /* 根据FilterPolicy定义的规则，使用白名单或者黑名单的方式，配置序列化对象 */
            if (FilterPolicy.INCLUDE == filter.policy()) {
                filterProvider.addFilter(filter.type(),
                        BeanPropertyWithClassFilter.filterOutAllExcept(filter.type(), filter.properties()));
            } else {  // FilterPolicy.EXCLUDE
                filterProvider.addFilter(filter.type(),
                        BeanPropertyWithClassFilter.serializeAllExcept(filter.type(), filter.properties()));
            }
        }
        
        objectMapper.setFilterProvider(filterProvider);
    }

    /**
     * 动态生成用户输出数据使用的JsonGenerator对象
     *
     * @param out     输出Stream对象
     * @param charset 字符集
     * @return {@link JsonGenerator }
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/25 00:43
     */
    public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding charset) throws IOException {
        this.generator = this.objectMapper.getFactory().createGenerator(out, charset);
        return this.generator;
    }

    /**
     * 动态生成用户输出数据使用的JsonGenerator对象
     *
     * @param out     输出Stream对象
     * @param charset 字符集
     * @return {@link JsonGenerator }
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/25 00:43
     */
    public JsonGenerator createJsonGenerator(OutputStream out, Charset charset) throws IOException {
        return createJsonGenerator(out, getJsonEncoding(charset));
    }

    /**
     * 动态生成用户输出数据使用的JsonGenerator对象
     *
     * @param out     输出Stream对象
     * @param charset 字符集
     * @return {@link JsonGenerator }
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    public JsonGenerator createJsonGenerator(OutputStream out, String charset) throws IOException {
        return createJsonGenerator(out, Charset.forName(charset));
    }

    /**
     * 将默认的序列化对象转换成Json字符串
     *
     * @return {@link String }
     * @throws JsonProcessingException json处理异常
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    public String writeAsString() throws JsonProcessingException {
        return writeAsString(this.prettyPrint);
    }

    /**
     * 写字符串
     *
     * @param serializeObj 序列化obj
     * @return {@link String }
     * @throws JsonProcessingException json处理异常
     * @author Tequila
     * @date 2022/06/26 02:06
     */
    public String writeAsString(Object serializeObj) throws JsonProcessingException {
        return writeAsString(serializeObj, this.prettyPrint);
    }

    /**
     * 将默认的序列化对象转换成Json字符串
     *
     * @param prettyPrint 漂亮打印
     * @return {@link String }
     * @throws JsonProcessingException json处理异常
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    public String writeAsString(boolean prettyPrint) throws JsonProcessingException {
        return writeAsString(this.serializeValue, this.prettyPrint);
    }


    /**
     * 写字符串
     *
     * @param serializeObj 序列化obj
     * @param prettyPrint  漂亮打印
     * @return {@link String }
     * @throws JsonProcessingException json处理异常
     * @author Tequila
     * @date 2022/06/26 02:05
     */
    public String writeAsString(Object serializeObj, boolean prettyPrint) throws JsonProcessingException {
        if (prettyPrint) {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serializeObj);
        } else {
            return objectMapper.writeValueAsString(serializeObj);
        }
    }

    /**
     * 使用默认的JsonGenerator对象输出obj对象的json数据
     *
     * @param obj obj
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    public void write(Object obj) throws IOException {
        this.generator.writeObject(obj);
        this.generator.flush();
    }

    /**
     * 使用默认的JsonGenerator对象输出默认序列化对象的json数据
     *
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    public void write() throws IOException {
        write(this.serializeValue);
    }

    /**
     * 判断对象是否支持JsonFilterEx操作
     *
     * @param klass Class对象
     * @return boolean
     * @author Tequila
     * @date 2022/06/25 00:44
     */
    private static boolean isJsonFilterEx(Class<?> klass) {
        return (klass.getAnnotation(JsonFilterGroup.class) != null || 
                klass.getAnnotation(JsonFilterEx.class) != null);
    }

    /**
     * 获取序列化对象Annotation定义中的JsonFilterEx实例
     *
     * @param klass Class对象
     * @return {@link JsonFilterEx[] }
     * @author Tequila
     * @date 2022/06/25 00:45
     */
    private static JsonFilterEx[] getJsonFilterExAnnotation(Class<?> klass) {
        return klass.getDeclaredAnnotationsByType(JsonFilterEx.class);
    }

    /**
     * 获取序列化对象Annotation定义中的JsonFilterGroup实例
     *
     * @param klass Class对象
     * @return {@link JsonFilterGroup[] } 获取到的JsonFilterGroup数组
     * @author Tequila
     * @date 2022/06/25 00:08
     */
    private static JsonFilterGroup[] getJsonFilterGroupAnnotation(Class<?> klass) {
        return klass.getDeclaredAnnotationsByType(JsonFilterGroup.class);
    }

    /**
     * 将Charset转换成JsonEncoding对象
     *
     * @param charset 字符集
     * @return {@link JsonEncoding }
     * @author Tequila
     * @date 2022/06/25 00:09
     */
    private JsonEncoding getJsonEncoding(Charset charset) {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            if (charset.name().equals(encoding.getJavaName())) {
                return encoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    /**
     * 是超级注释
     *
     * @param annotation 注释
     * @return boolean
     * @author Tequila
     * @date 2022/06/25 00:10
     */
    private static boolean isSuperAnnotation(Annotation annotation) {
    	return (annotation.annotationType().isAnnotationPresent(JsonFilterEx.class)
    			|| annotation.annotationType().isAnnotationPresent(JsonFilterGroup.class));
    	
    }

    /**
     * 得到超级注释
     *
     * @param annotation 注释
     * @return {@link Annotation }
     * @author Tequila
     * @date 2022/06/25 00:10
     */
    private static Annotation getSuperAnnotation(Annotation annotation) {
    	if (annotation.annotationType().isAnnotationPresent(JsonFilterEx.class)) {
    		return annotation.annotationType().getAnnotation(JsonFilterEx.class);
    	} else if (annotation.annotationType().isAnnotationPresent(JsonFilterGroup.class)) {
    		return annotation.annotationType().getAnnotation(JsonFilterGroup.class);
    	} else {
    		return null;
    	}
    }


    /**
     * 从注释
     *
     * @param annotation 注释
     * @return {@link JsonFilterOption }
     * @author Tequila
     * @date 2022/06/25 00:10
     */
    public static JsonFilterOption fromAnnotations(Annotation[] annotation) {
    	JsonFilterOption option = new JsonFilterOption();
    	
    	Arrays.asList(annotation).forEach(a -> {
    		if (isSuperAnnotation(a)) {
    			a = getSuperAnnotation(a);
    		}
    			
    		if (a instanceof JsonFilterEx) {
            	option.addFilter((JsonFilterEx) a);
            } else if (a instanceof JsonFilterGroup) {
            	JsonFilterGroup filters = (JsonFilterGroup) a;
                Arrays.asList(filters.value()).forEach(option::addFilter);
            } 
        });
    	
    	if (option.getOptions().isEmpty()) {
    		throw new UnsupportedOperationException("Can not found JsonFilterEx or JsonFilterGroup annotation declare in annotations.");
    	}
    	
    	return option;
    }

    /**
     * 用于支持Jackson序列化的默认MixIn接口类
     * 
     * @author Tequila
     *
     */
    @JsonFilter(DEFAULT_FILTER_EX)
    static interface JsonFilterExMixIn {
        /* nothing */
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public Object getSerializeValue() {
        return serializeValue;
    }

    public void setSerializeValue(Object serializeValue) {
        this.serializeValue = serializeValue;
    }

    public JsonGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(JsonGenerator generator) {
        this.generator = generator;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public JsonFilterOption getOption() {
        return option;
    }

    public void setOption(JsonFilterOption option) {
        this.option = option;
    }
}
