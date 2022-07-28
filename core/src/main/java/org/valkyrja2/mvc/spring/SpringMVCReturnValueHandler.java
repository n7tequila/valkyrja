/*
 * PROJECT valkyrja2
 * core/SpringMVCReturnValueHandler.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.springframework.beans.BeansException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.valkyrja2.mvc.ResponseObject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义SpringMVC返回值处理对象
 *
 * @author Tequila
 * @create 2022/07/23 22:00
 **/
public interface SpringMVCReturnValueHandler {

    /**
     * 是否返回值定义了<code>@ResponseBody</code>
     *
     * @param returnType 返回类型
     * @return boolean
     * @author Tequila
     * @date 2022/07/24 11:56
     */
    default boolean isResponseBody(MethodParameter returnType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) || returnType.hasMethodAnnotation(ResponseBody.class);
    }

    /**
     * 判断一个方法上是否有指定的注释（annotation）
     *
     * @param returnType      返回类型
     * @param annotationTypes 注释类型
     * @return boolean
     * @author Tequila
     * @date 2022/07/23 22:10
     */
    @SuppressWarnings({"unchecked", "varargs"})
    default boolean hasAnnotation(MethodParameter returnType, Class<? extends Annotation> ... annotationTypes) {
        if (annotationTypes == null) return false;

        for (Class<? extends Annotation> annotationType: annotationTypes) {
            if (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), annotationType)
                    || returnType.hasMethodAnnotation(annotationType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 返回对象是否是ResponseObject
     *
     * @param returnType 返回类型
     * @return boolean
     * @author Tequila
     * @date 2022/07/25 19:54
     */
    default boolean isResponseObject(MethodParameter returnType) {
        Class<?> returnObjClass = returnType.getParameterType();
        return (ResponseObject.class.equals(returnObjClass));
    }
}
