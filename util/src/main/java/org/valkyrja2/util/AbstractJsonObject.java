/*
 * PROJECT valkyrja2
 * util/AbstractJsonObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.valkyrja2.util.exception.JsonRuntimeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * JsonObject抽象类<br>
 * 提供默认的将对象序列化和反序列化成的默认操作
 *
 * @author Tequila
 * @create 2022/06/27 16:27
 **/
public abstract class AbstractJsonObject<T> implements JsonObject {

    /**
     * 字符串
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/06/27 16:28
     */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * 解析json
     *
     * @param json json
     * @return {@link T } 解析后的对象实例
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/27 16:48
     */
    public T fromJson(String json) throws IOException {
        Class<T> type = ClassUtils.getClassGenericType(this.getClass(), 0);
        if (type == null) {
            throw new IllegalArgumentException("No generic object is defined");
        }

        return fromJson(json,type);
    }

    /**
     * 解析json，并且对当前对象中的字段赋值
     *
     * @param json         json
     * @param ignoreFields 跳过字段
     * @throws JsonRuntimeException json序列化时报错
     * @author Tequila
     * @date 2022/06/27 17:06
     */
    public void parseJson(String json, String...ignoreFields) throws IOException {
        T jsonObj = fromJson(json);
        try {
            BeanUtils.copyProperties(this, jsonObj, false, ignoreFields);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JsonRuntimeException(String.format("Can not parseJson to object %s", this.getClass().getSimpleName()), e);
        }
    }

    /**
     * 解析json
     *
     * @param json json
     * @param type 类型
     * @return {@link T } 解析后的对象实例
     * @throws IOException IO异常
     * @author Tequila
     * @date 2022/06/27 16:48
     */
    public static <T> T fromJson(String json, Class<T> type) throws IOException {
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(type, "type must not be null");

        return Jackson2Utils.json2obj(json, type);
    }
}
