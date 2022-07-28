/*
 * PROJECT valkyrja2
 * core/RedisFactory.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.valkyrja2.mvc.spring.SpringUtils;

/**
 * Redis工厂方法
 *
 * @author Tequila
 * @create 2022/07/18 13:31
 **/
public final class RedisFactory {

    public static final String REDIS_TEMPLATE = "redisTemplate";

    public static final String STRING_REDIS_TEMPLATE = "stringRedisTemplate";

    /**
     * getRedisTemplate
     *
     * @param beanName bean名字
     * @return {@link RedisTemplate }<{@link String }, {@link V }>
     * @author Tequila
     * @date 2022/07/18 15:33
     */
    public static <V> RedisTemplate<String, V> getRedisTemplate(String beanName) {
        return SpringUtils.getBean(beanName);
    }

    /**
     * getRedisTemplate
     *
     * @return {@link RedisTemplate }<{@link String }, {@link V }>
     * @author Tequila
     * @date 2022/07/18 15:33
     */
    public static <V> RedisTemplate<String, V> getRedisTemplate() {
        return SpringUtils.getBean(REDIS_TEMPLATE);
    }

    /**
     * getStringRedisTemplate
     *
     * @param beanName bean名字
     * @return {@link StringRedisTemplate }
     * @author Tequila
     * @date 2022/07/18 15:33
     */
    public static StringRedisTemplate getStringRedisTemplate(String beanName) {
        return SpringUtils.getBean(beanName);
    }

    /**
     * getStringRedisTemplate
     *
     * @return {@link StringRedisTemplate }
     * @author Tequila
     * @date 2022/07/18 15:33
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(STRING_REDIS_TEMPLATE);
    }

    private RedisFactory() {
        throw new IllegalStateException("Factory class");
    }
}
