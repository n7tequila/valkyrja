/*
 * PROJECT valkyrja2
 * core/SpringUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Spring 工具包
 *
 * @author Tequila
 * @create 2022/07/01 22:49
 **/
public class SpringUtils implements BeanFactoryAware, ApplicationContextAware {

    private static final String MSG_BEAN_FACTORY_NULL_ERR = "BeanFactory is null";

    /** 静态缓存系统中的BeanFactory对象 */
    private static BeanFactory beanFactory;
    
    /** 静态缓存系统中的ApplicationContext对象 */
    private static ApplicationContext ctx;

    private static final StandardEvaluationContext context = new StandardEvaluationContext();

    private static final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 获取当前系统进程中的BeanFactory
     *
     * @return {@link BeanFactory }
     * @author Tequila
     * @date 2022/07/01 22:50
     */
    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * 获取当前系统进程中的ApplicationContext
     *
     * @return {@link ApplicationContext }
     * @author Tequila
     * @date 2022/07/01 22:50
     */
    public static ApplicationContext getApplicationContext() {
    	return ctx;
    }

    /**
     * 获取当前系统进程中的StandardEvaluationContext
     *
     * @return {@link StandardEvaluationContext }
     * @author Tequila
     * @date 2022/07/16 00:41
     */
    public static StandardEvaluationContext getEvaluationContext() {
        return context;
    }

    /**
     * 直接从BeanFactory对象中获取Bean对象
     *
     * @param name Bean名称
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/01 22:50
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        if (beanFactory != null) {
            return (T) beanFactory.getBean(name);
        } else {
            throw new NullPointerException(MSG_BEAN_FACTORY_NULL_ERR);
        }
    }

    /**
     * 得到bean
     *
     * @param klass 通过spring初始化的
     * @return {@link T }
     * @author Tequila
     * @date 2022/07/13 19:55
     */
    public static <T> T getBean(Class<T> klass) {
        if (beanFactory != null) {
            return klass.cast(beanFactory.getBean(klass));
        } else {
            throw new NullPointerException(MSG_BEAN_FACTORY_NULL_ERR);
        }
    }


    
    /** (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }


    /** (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.ctx = applicationContext;
        SpringUtils.context.addPropertyAccessor(new BeanFactoryAccessor());
        SpringUtils.context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        SpringUtils.context.setRootObject(applicationContext);
    }
    
}