package org.valkyrja2.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 工具包
 *
 * @author Tequila
 * @create 2022/11/16 14:58
 **/
public class SpringUtils implements BeanFactoryAware, ApplicationContextAware {

    /** 静态缓存系统中的BeanFactory对象 */
    private static BeanFactory beanFactory;
    
    /** 静态缓存系统中的ApplicationContext对象 */
    private static ApplicationContext ctx;

    /**
     * 获取当前系统进程中的BeanFactory
     *
     * @return {@link BeanFactory }
     * @author Tequila
     * @date 2022/11/16 14:58
     */
    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * 获取当前系统进程中的ApplicationContext
     *
     * @return {@link ApplicationContext }
     * @author Tequila
     * @date 2022/11/16 14:58
     */
    public static ApplicationContext getApplicationContext() {
    	return ctx;
    }

    /**
     * 获取name指向的bean实例
     *
     * @param name bean名称
     * @return {@link T }
     * @author Tequila
     * @date 2022/11/16 14:58
     */
    public static <T> T getBean(String name) {
        if (beanFactory != null) {
            return (T) beanFactory.getBean(name);
        } else {
            return null;
        }
    }

    /**
     * 获取klass指向的bean的实例
     *
     * @param klass 类对象
     * @return {@link T }
     * @author Tequila
     * @date 2022/11/16 14:59
     */
    public static <T> T getBean(Class<T> klass) {
        if (beanFactory != null) {
            return (T) beanFactory.getBean(klass);
        } else {
            return null;
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }


	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		SpringUtils.ctx = ctx;
	}
    
}