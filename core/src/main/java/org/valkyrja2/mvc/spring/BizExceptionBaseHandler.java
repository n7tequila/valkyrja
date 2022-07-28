/*
 * PROJECT valkyrja2
 * core/BizExceptionBaseHandler.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.mvc.ApiResponseObject;
import org.valkyrja2.mvc.ResponseObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Tequila
 * @create 2022/07/25 00:56
 **/
public abstract class BizExceptionBaseHandler {

    private static final Logger log = LoggerFactory.getLogger(BizExceptionBaseHandler.class);


    /**
     * 根据输入的错误对象，生成<code>ResponseObject</code>对象
     *
     * @param method 方法
     * @param e      e
     * @return {@link ResponseObject }<{@link ? }>
     * @author Tequila
     * @date 2022/07/25 00:57
     */
    protected  <T extends Exception> ResponseObject<?> createResponseObject(Method method, T e) {
        Class<?> respObjClass;
        if (method.getAnnotation(OpenAPI.class) != null) {
            respObjClass = ApiResponseObject.class;
        } else {
            respObjClass = ResponseObject.class;
        }

        Constructor<?> constructor;
        try {
            if (e instanceof ValidateException) {
                constructor = respObjClass.getConstructor(ValidateException.class);
            } else if (e instanceof BizRuntimeException) {
                constructor = respObjClass.getConstructor(BizRuntimeException.class);
            } else {
                constructor = respObjClass.getConstructor();
            }

            return (ResponseObject<?>) constructor.newInstance(e);
        } catch (Exception ex) {
            if (method.getAnnotation(OpenAPI.class) != null) {
                return new ApiResponseObject<>(e);
            } else {
                return new ResponseObject<>(e);
            }
        }
    }

    /**
     * 获取Exception.StackTrace中的mvc controller的方法
     *
     * @param stacks 栈
     * @return {@link Method }
     * @author Tequila
     * @date 2022/07/25 14:37
     */
    public Method getMVCControllerMethod(StackTraceElement[] stacks) {
        try {
            for (StackTraceElement stack : stacks) {
                Class<?> klass = Class.forName(stack.getClassName());
                // 检查当前类是不是一个RequestMapping对象
                if (klass.getAnnotation(RequestMapping.class) != null) {
                    for (Method method : klass.getMethods()) {
                        if (stack.getMethodName().equals(method.getName())) {
                            return method;
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {  /* nothing */ }
        return null;
    }
}
