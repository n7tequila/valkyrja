/*
 * PROJECT valkyrja2
 * util/BeanUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

/**
 * Bean工具类<br>
 * 工具类以Apache BeanUtils作为基础，继承了<code>org.apache.commons.beanutils.BeanUtils</code>
 * 因此可以通过当前工具类调用所有的Apache BeanUtils中的方法
 *
 * @author Tequila
 * @create 2022/06/27 17:07
 **/
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    private BeanUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 复制对象属性<br>
     * 可跳过源数据是null的字段赋值，以及可以以黑名单的方式过滤拷贝字段
     *
     * @param dest             目标对象
     * @param orig             源对象
     * @param skipNullCopy     跳过null拷贝。如果源对象的某个属性是null，则跳过赋值，保留目标对象的数据
     * @param ignoreProperties 忽略属性
     * @throws InvocationTargetException 调用目标异常
     * @throws IllegalAccessException    非法访问异常
     * @author Tequila
     * @date 2022/06/27 18:18
     */
    public static void copyProperties(Object dest, Object orig, boolean skipNullCopy, String...ignoreProperties) throws InvocationTargetException, IllegalAccessException {
        Objects.requireNonNull(dest, "dest must not be null");
        Objects.requireNonNull(orig, "orig must not be null");
        log.debug("BeanUtils.copyProperties({}, {}, {}, ...)", dest.getClass().getSimpleName(), orig.getClass().getSimpleName(), skipNullCopy);

        if (orig instanceof DynaBean) {
            final DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if (ignoreProperties != null && ArrayUtils.contains(ignoreProperties, name)) {
                    continue;
                }
                if (getPropertyUtils().isReadable(orig, name)
                        && getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = ((DynaBean) orig).get(name);
                    copyProperty(dest, name, value, skipNullCopy);
                }
            }
        } else if (orig instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> propMap = (Map<String, Object>) orig;
            for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
                final String name = entry.getKey();
                if (ignoreProperties != null && ArrayUtils.contains(ignoreProperties, name)) {
                    continue;
                }
                if (getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = entry.getValue();
                    copyProperty(dest, name, value, skipNullCopy);
                }
            }
        } else {
            final PropertyDescriptor[] origDescriptors = getPropertyUtils().getPropertyDescriptors(orig);
            for (PropertyDescriptor origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                /* 判断如果name是class，或者在忽略字段里包含了该字段，则跳过 */
                if ("class".equals(name)
                        || (ignoreProperties != null && ArrayUtils.contains(ignoreProperties, name))) {
                    continue;
                }
                if (getPropertyUtils().isReadable(orig, name)
                        && getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        final Object value = getPropertyUtils().getSimpleProperty(orig, name);
                        copyProperty(dest, name, value, skipNullCopy);
                    } catch (NoSuchMethodException e) { /* nothing to do */ }
                }
            }
        }
    }

    /**
     * 复制属性
     *
     * @param bean         bean
     * @param name         名字
     * @param value        值
     * @param skipNullCopy 跳过null拷贝。如果value是null，则跳过赋值，保留目标对象的数据
     * @throws IllegalAccessException    非法访问异常
     * @throws InvocationTargetException 调用目标异常
     * @author Tequila
     * @date 2022/06/27 22:27
     */
    public static void copyProperty(Object bean, String name, Object value, boolean skipNullCopy) throws IllegalAccessException, InvocationTargetException {
        if (value != null || !skipNullCopy) {
            copyProperty(bean, name, value);
        }
    }

    /**
     * 获取PropertyUtils
     *
     * @return {@link PropertyUtilsBean }
     * @author Tequila
     * @date 2022/06/27 22:54
     */
    private static PropertyUtilsBean getPropertyUtils() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }

    /**
     * 将map对象转换成bean对象
     *
     * @param map         map
     * @param returnType  返回类型
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/27 17:16
     */
    public static <T> T map2bean(Map<String, Object> map, Class<T> returnType) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        T output = returnType.newInstance();

        copyProperties(output, map, false);

        return output;
    }
}
