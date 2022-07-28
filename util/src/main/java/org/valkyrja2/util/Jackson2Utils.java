/*
 * PROJECT valkyrja2
 * util/Jackson2Utils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.exception.JsonRuntimeException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * jackson2 json 工具包<br>
 *
 *
 * @author Tequila
 * @create 2022/05/25 08:18
 **/
public class Jackson2Utils {

    private static final Logger log = LoggerFactory.getLogger(Jackson2Utils.class);

    /** 使用漂亮模式输出 */
    private static boolean usePrettyMode = false;

    /** 默认json过滤器名 */
    public static final String DEFAULT_JSON_FILTER = "_DEFAULT_JSON_FILTER_";

    public static final String ERR_OBJ_2_JSON = "Jackson2Utils.obj2json(...) raise error.";

    /**
     * 构建默认json对象映射器
     *
     * @return {@link Builder } 带有默认配置信息的JsonMapper.Builder对象
     * @author Tequila
     * @date 2022/05/25 11:00
     */
    public static JsonMapper.Builder defaultObjectMapperBuilder() {
        return JsonMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 将Object转换成json字符串
     *
     * @param obj 需要转换的object
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误时抛出错误
     * @author Tequila
     * @date 2022/05/05 12:06
     */
    public static String obj2json(Object obj) throws JacksonException {
        try {
            return obj2json(obj, false, usePrettyMode);
        } catch (JsonRuntimeException e) {
            throw (JacksonException) e.getCause();
        }
    }

    /**
     * 将Object转换成json字符串
     *
     * @param obj       需要转换的object
     * @param skipError 当该参数生效时，如果在转换过程中报错，则跳过错误，并且返回默认的对象toString()字符串，
     *                  否则抛出{@link JacksonException}错误
     * @return {@link String } 转换后的json字符串
     * @author Tequila
     * @date 2022/05/05 12:04
     */
    public static String obj2json(Object obj, boolean skipError) {
        return obj2json(obj, skipError, usePrettyMode);
    }

    /**
     * 将Object转换成json字符串
     *
     * @param obj        需要转换的object
     * @param skipError  当该参数生效时，如果在转换过程中报错，则跳过错误，并且返回默认的对象toString()字符串，
     *                   否则抛出{@link JacksonException}错误
     * @param prettyMode 使用漂亮格式输出
     * @return {@link String } 转换后的json字符串
     * @author Tequila
     * @date 2022/05/05 14:06
     */
    public static String obj2json(Object obj, boolean skipError, boolean prettyMode) {
        ObjectMapper mapper = defaultObjectMapperBuilder().build();
        try {
            if (prettyMode) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return mapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            log.warn("Can not execute obj2json({})", obj.getClass(), e);
            if (!skipError) {
                throw new JsonRuntimeException(ERR_OBJ_2_JSON, e);
            } else {
                return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
            }
        }
    }

    /**
     * 使用View方式，将Object转换成json字符串
     *
     * @param obj       需要转换的object
     * @param viewClass 视图类
     * @return {@link String } 转换后的json字符串
     * @throws JsonProcessingException json处理异常时抛出错误
     * @see <a href="https://www.baeldung.com/jackson-json-view-annotation">Jackson JSON Views@baeldung</a>
     * @author Tequila
     * @date 2022/05/05 12:02
     */
    public static String obj2json(Object obj, Class<?> viewClass) throws JacksonException {
        ObjectMapper mapper = defaultObjectMapperBuilder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        if (usePrettyMode) {
            return mapper.writerWithView(viewClass).withDefaultPrettyPrinter().writeValueAsString(obj);
        } else {
            return mapper.writerWithView(viewClass).writeValueAsString(obj);
        }
    }


    /**
     * 使用FilterProvider方式，将Object转换成json字符串
     *
     * @param obj            需要转换的object
     * @param filterProvider FilterProvider对象
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误
     * @author Tequila
     * @date 2022/05/05 12:01
     */
    public static String obj2json(Object obj, FilterProvider filterProvider) throws JacksonException {
        return obj2json(obj, filterProvider, null);
    }

    /**
     * 使用FilterProvider方式，将Object转换成json字符串
     *
     * @param obj            需要转换的object
     * @param filterProvider FilterProvider对象
     * @param mixInClass     mixInClass
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误
     * @author Tequila
     * @date 2022/05/05 12:01
     */
    public static String obj2json(Object obj, FilterProvider filterProvider, Class<?> mixInClass) throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(filterProvider);
        if (mixInClass != null) mapper.addMixIn(obj.getClass(), mixInClass);
        if (usePrettyMode) {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } else {
            return mapper.writeValueAsString(obj);
        }
    }


    /**
     * obj2json，并排除excludeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param excludeFields 排除字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:27
     */
    public static String obj2jsonExcludeFields(Object obj, String...excludeFields) throws JacksonException {
        return obj2jsonExcludeFields(obj, DefaultFilter.class, DEFAULT_JSON_FILTER, excludeFields);
    }


    /**
     * obj2json，并排除excludeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param filterName    过滤器名字
     * @param excludeFields 排除字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:33
     */
    public static String obj2jsonExcludeFields(Object obj, String filterName, String[] excludeFields) throws JacksonException {
        return obj2jsonExcludeFields(obj, null, filterName, excludeFields);
    }


    /**
     * obj2json，并排除excludeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param mixInClass    在课堂上混合
     * @param filterName    过滤器名字
     * @param excludeFields 排除字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:35
     */
    public static String obj2jsonExcludeFields(Object obj, Class<?> mixInClass, String filterName, String...excludeFields) throws JacksonException {
        String locFilterName = StringUtils.isBlank(filterName) ? DEFAULT_JSON_FILTER : filterName;
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(locFilterName,
                SimpleBeanPropertyFilter.serializeAllExcept(excludeFields));

        return obj2json(obj, filterProvider, mixInClass);
    }


    /**
     * obj2json，仅输出includeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param includeFields 包含字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:52
     */
    public static String obj2jsonIncludeFields(Object obj, String...includeFields) throws JacksonException {
        return obj2jsonIncludeFields(obj, null, DEFAULT_JSON_FILTER, includeFields);
    }


    /**
     * obj2json，仅输出includeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param filterName    过滤器名字
     * @param includeFields 包含字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:52
     */
    public static String obj2jsonIncludeFields(Object obj, String filterName, String[] includeFields) throws JacksonException {
        return obj2jsonIncludeFields(obj, null, filterName, includeFields);
    }


    /**
     * obj2json，仅输出includeFields描述的字段
     *
     * @param obj           需要转换的object
     * @param mixInClass    mixInClass
     * @param filterName    过滤器名字
     * @param includeFields 包含字段数组
     * @return {@link String } 转换后的json字符串
     * @throws JacksonException json处理异常时抛出错误时抛出错误
     * @author Tequila
     * @date 2022/05/26 11:54
     */
    public static String obj2jsonIncludeFields(Object obj, Class<?> mixInClass, String filterName, String...includeFields) throws JacksonException {
        String locFilterName = StringUtils.isBlank(filterName) ? DEFAULT_JSON_FILTER : filterName;
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(locFilterName,
                SimpleBeanPropertyFilter.filterOutAllExcept(includeFields));

        return obj2json(obj, filterProvider, mixInClass);
    }


    /**
     * 将json字符串转换成klass指定的object
     *
     * @param json  json字符串
     * @param klass 指定的转换的类
     * @return {@link T } 转换后的对象实例
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/24 23:42
     */
    public static <T> T json2obj(String json, Class<T> klass) throws IOException  {
        ObjectMapper mapper = defaultObjectMapperBuilder().build();

        return mapper.readValue(json, klass);
    }


    /**
     * 将json字符串转换成指定的Collection集合对象
     *
     * @param json            json字符串
     * @param collectionClass 集合类
     * @param elementClass    元素类
     * @return {@link T } 转换后的Collection对象实例
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/24 23:52
     */
    public static <T, E> T json2obj(String json, Class<? extends Collection<E>> collectionClass, Class<E> elementClass) throws IOException {
        ObjectMapper mapper = defaultObjectMapperBuilder().build();

        JavaType klass = mapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        return mapper.readValue(json, klass);
    }


    /**
     * 将json字符串转换成指定的Map对象
     *
     * @param json       json字符串
     * @param mapClass   map类
     * @param keyClass   map的key类
     * @param valueClass map的value类
     * @return {@link T } 转换后的Map对象实例
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/24 23:55
     */
    public static <T extends Map<K, V>, K, V> T json2obj(String json, Class<T> mapClass, Class<K> keyClass, Class<V> valueClass) throws IOException {
        ObjectMapper mapper = defaultObjectMapperBuilder().build();

        JavaType klass = mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
        return mapper.readValue(json, klass);
    }

    /**
     * 是使用漂亮模式
     *
     * @return boolean
     * @author Tequila
     * @date 2022/06/24 23:57
     */
    public static boolean isUsePrettyMode() {
        return usePrettyMode;
    }

    /**
     * 设置使用漂亮模式
     *
     * @param usePrettyMode 使用漂亮模式
     * @author Tequila
     * @date 2022/06/24 23:57
     */
    public static void setUsePrettyMode(boolean usePrettyMode) {
        Jackson2Utils.usePrettyMode = usePrettyMode;
    }

    /**
     * 默认json过滤器
     *
     * @author Tequila
     * @create 2022/05/25 10:32
     **/
    @JsonFilter(DEFAULT_JSON_FILTER)
    public static interface DefaultFilter {}

    private Jackson2Utils() {
        throw new IllegalStateException("Utility class");
    }
}
