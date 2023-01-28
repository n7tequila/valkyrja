/*
 * PROJECT valkyrja2
 * util/ClassUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class工具类
 *
 * @author Tequila
 * @create 2022/06/27 16:37
 **/
public class ClassUtils {
    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

    private static final String MSG_CLASS_MUST_NOT_NULL = "klass must not be null";
    private static final String MSG_INDEX_GTE_ZERO = "index must gather then or equal to 0";
    private static final String MSG_SUPER_MUST_NOT_NULL = "superClass must not be null";

    private ClassUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的所有泛型参数的类型。
     *
     * @param klass klass
     * @return {@link Type[] }
     * @author Tequila
     * @date 2022/07/04 19:20
     */
    public static Type[] getClassGenericTypes(final Class<?> klass) {
        Objects.requireNonNull(klass, MSG_CLASS_MUST_NOT_NULL);

        Type genType = klass.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn("{}'s superclass not ParameterizedType", klass.getSimpleName());
            return new Type[0];
        }

        return ((ParameterizedType) genType).getActualTypeArguments();
    }


    /**
     * 通过反射, 获得Class定义中声明的Superclass的指定位置的泛型参数的类型。
     * <br>
     * 注意泛型必须定义在父类处. 这是唯一可以通过反射从泛型获得Class实例的地方。如无法找到, 返回null。<br>
     * 如public UserDao extends HibernateDao<User, Long>
     *
     * @param klass 类对象
     * @param index 需要获取第几个泛型对象，从0开始
     * @return {@link Class }<{@link T }> 指定的泛型对象，如果没有则返回null
     * @author Tequila
     * @date 2022/06/27 16:39
     */
    public static <T> Class<T> getClassGenericType(final Class<?> klass, final int index) {
        Objects.requireNonNull(klass, MSG_CLASS_MUST_NOT_NULL);
        if (index < 0) throw new IllegalArgumentException(MSG_INDEX_GTE_ZERO);

        Type genType = klass.getGenericSuperclass();

        return getGenericType(klass, genType, index, "superclass");
    }

    /**
     * 通过反射, 获得Class定义中声明的Interface的指定位置的泛型参数的类型。
     * <br>
     * 注意泛型必须定义在Interface处. 这是唯一可以通过反射从泛型获得Class实例的地方。如无法找到, 返回null。<br>
     * 如public UserDao extends HibernateDao<User, Long>
     *
     * @param klass klass
     * @param index 需要获取第几个泛型对象，从0开始
     * @return {@link Class }<{@link T }>
     * @author Tequila
     * @date 2022/08/11 23:42
     */
    public static <T> Class<T> getInterfaceGenericType(final Class<?> klass, final int index) {
        Objects.requireNonNull(klass, MSG_CLASS_MUST_NOT_NULL);
        if (index < 0) throw new IllegalArgumentException(MSG_INDEX_GTE_ZERO);

        Type[] genTypes = klass.getGenericInterfaces();
        if (genTypes.length == 0) {
            log.warn("{}'s interface not ParameterizedType", klass.getSimpleName());
            return null;
        }
        Type genType = genTypes[0];

        return getGenericType(klass, genType, index, "interface");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getGenericType(final Class<?> klass, final Type genType, final int index, String type) {
        if (!(genType instanceof ParameterizedType)) {
            log.warn("{}'s {} not ParameterizedType", klass.getSimpleName(), type);
            return null;
        }

        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        if ((index >= types.length)) {
            log.warn("Index: {}, Size of {}'s Parameterized Type: {}", index, klass.getSimpleName(), types.length);
            return null;
        }
        if (types[index] instanceof Class<?>) {
            return (Class<T>) types[index];
        } else if (types[index] instanceof ParameterizedType &&
                ((ParameterizedType) types[index]).getRawType() instanceof  Class<?>) {
            return (Class<T>) ((ParameterizedType) types[index]).getRawType();
        }

        // 所有条件都不符合，则返回null
        log.warn("{} not set the actual class on {} generic parameter", klass.getSimpleName(), type);
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的指定位置的泛型参数的类型，
     * 如果获取不到，则获取上一层的泛型参数，一直到获取不到为止
     *
     * @param klass klass
     * @param index 指数
     * @return {@link Class }<{@link T }>
     * @author Tequila
     * @date 2022/07/04 17:51
     */
    public static <T> Class<T> getSuperClassGenericType(final Class<?> klass, final int index) {
        Objects.requireNonNull(klass, MSG_CLASS_MUST_NOT_NULL);
        if (index < 0) throw new IllegalArgumentException(MSG_INDEX_GTE_ZERO);

        Class<?> curClass = klass;
        Class<T> resClass = null;
        do {
            resClass = getClassGenericType(curClass, index);
            curClass = curClass.getSuperclass();
        } while(resClass == null && curClass != null && !Object.class.equals(curClass));

        return resClass;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的superClass指定类型的泛型参数的类型，
     * 如果获取不到，则获取上一层的泛型参数，一直到获取不到为止
     *
     * @param klass      klass
     * @param superClass 超类
     * @return {@link Class }<{@link T }>
     * @author Tequila
     * @date 2022/07/04 19:14
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperClassGenericType(final Class<?> klass, final Class<?> superClass) {
        Objects.requireNonNull(klass, MSG_CLASS_MUST_NOT_NULL);
        Objects.requireNonNull(superClass, MSG_SUPER_MUST_NOT_NULL);

        Class<?> curClass = klass;
        do {
            Type[] types = getClassGenericTypes(curClass);
            if (types != null) {
                for (Type type : types) {
                    if (org.apache.commons.lang3.ClassUtils.isAssignable((Class<?>) type, superClass)) {
                        return (Class<T>) type;
                    }
                }
            }
            curClass = curClass.getSuperclass();
        } while(curClass != null && !Object.class.equals(curClass));

        return null;
    }

    /**
     * 根据class name获取class对象
     *
     * @param typeName 类型名称
     * @return {@link Type }
     * @author Tequila
     * @date 2022/08/12 01:08
     */
    public static Class<?> forName(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found " + typeName, e);
        }
    }

    /**
     * @see org.apache.commons.lang3.ClassUtils#isAssignable(Class, Class)
     */
    public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
        return org.apache.commons.lang3.ClassUtils.isAssignable(cls, toClass);
    }

    /**
     * @see org.apache.commons.lang3.ClassUtils#isAssignable(Class, Class, boolean) 
     */
    public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
        return org.apache.commons.lang3.ClassUtils.isAssignable(cls,toClass, autoboxing);
    }

    /**
     * @see org.apache.commons.lang3.ClassUtils#isAssignable(Class[], Class[])
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray) {
        return org.apache.commons.lang3.ClassUtils.isAssignable(classArray, toClassArray);
    }

    /**
     * @see org.apache.commons.lang3.ClassUtils#isAssignable(Class[], Class[], boolean)
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, boolean autoboxing) {
        return org.apache.commons.lang3.ClassUtils.isAssignable(classArray, toClassArray, autoboxing);
    }

}

