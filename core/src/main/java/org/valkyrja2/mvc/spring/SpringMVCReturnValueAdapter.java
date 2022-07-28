/*
 * PROJECT valkyrja2
 * core/SpringMVCReturnValueAdapter.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tequila
 * @create 2022/07/24 12:11
 **/
public class SpringMVCReturnValueAdapter implements BeanPostProcessor {

    List<ResponseBodyAdvice<Object>> advices = new ArrayList<>();

    private List<SpringMVCReturnValueHandler> returnValueHandlers;

    /**
     * BeanPostProcessor.postProcessBeforeInitialization 默认处理方法
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object }
     * @throws BeansException 豆子例外
     * @author Tequila
     * @date 2022/07/24 01:31
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * BeanPostProcessor.postProcessAfterInitialization默认处理方法
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object }
     * @throws BeansException 豆子例外
     * @author Tequila
     * @date 2022/07/24 01:35
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ResponseBodyAdvice) {
            advices.add((ResponseBodyAdvice<Object>) bean);
        } else if (bean instanceof RequestMappingHandlerAdapter) {
            List<HandlerMethodReturnValueHandler> handlers = ((RequestMappingHandlerAdapter) bean).getReturnValueHandlers();
            if (returnValueHandlers != null && handlers != null) {
                List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<>(handlers);
                for (SpringMVCReturnValueHandler returnValueHandler: returnValueHandlers) {
                    final int deferredPos = getValueHandlerPosition(originalHandlers,
                            RequestResponseBodyMethodProcessor.class);

                    HandlerMethodReturnValueHandler mvcHandler = getValueHandler(originalHandlers, returnValueHandler.getClass());
                    if (mvcHandler != null) {
                        if (mvcHandler instanceof RequestResponseBodyMethodProcessor) {

                        }

                        originalHandlers.remove(mvcHandler);
                        originalHandlers.add(deferredPos, mvcHandler);
                    }
                }
                ((RequestMappingHandlerAdapter) bean).setReturnValueHandlers(originalHandlers);
            }
        }
        return bean;
    }

    /**
     * 得到值处理程序位置
     *
     * @param originalHandlers 原来处理程序
     * @param handlerClass     处理程序类
     * @return int 如果有对应的handlerClass则返回位置，如果找不到返回-1
     * @author Tequila
     * @date 2022/07/24 00:47
     */
    public int getValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers,
                                        Class<?> handlerClass) {
        for (int i = 0; i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 得到值处理程序
     *
     * @param originalHandlers 原来处理程序
     * @param handlerClass     处理程序类
     * @return {@link HandlerMethodReturnValueHandler }
     * @author Tequila
     * @date 2022/07/24 00:45
     */
    public HandlerMethodReturnValueHandler getValueHandler(final List<HandlerMethodReturnValueHandler> originalHandlers,
                                                            Class<?> handlerClass) {
        for (HandlerMethodReturnValueHandler valueHandler: originalHandlers) {
            if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
                return valueHandler;
            }
        }
        return null;
    }

    /**
     * 创建输出消息
     *
     * @param webRequest web请求
     * @return {@link ServletServerHttpResponse }
     * @author Tequila
     * @date 2022/07/25 09:17
     */
    public static ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    public List<ResponseBodyAdvice<Object>> getAdvices() {
        return advices;
    }

    public List<SpringMVCReturnValueHandler> getReturnValueHandlers() {
        return returnValueHandlers;
    }

    public void setReturnValueHandlers(List<SpringMVCReturnValueHandler> returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }
}
