/*
 * PROJECT valkyrja2
 * core/RedisFactory.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.redis;

import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.valkyrja2.mvc.spring.SpringUtils;

import java.util.Objects;

/**
 * Redis工厂方法
 *
 * @author Tequila
 * @create 2022/07/18 13:31
 **/
public final class RedisFactory {

    public static final String REDIS_TEMPLATE = "redisTemplate";

    public static final String STRING_REDIS_TEMPLATE = "stringRedisTemplate";

    public static final String REDISSON_CLIENT = "redissonClient";

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

    /**
     * 得到RedissonClient
     *
     * @return {@link RedissonClient }
     * @author Tequila
     * @date 2022/08/05 00:57
     */
    public static RedissonClient getRedissonClient(String beanName) {
        return SpringUtils.getBean(beanName);
    }

    /**
     * 得到RedissonClient
     *
     * @return {@link RedissonClient }
     * @author Tequila
     * @date 2022/08/05 01:05
     */
    public static RedissonClient getRedissonClient() {
        return getRedissonClient(REDISSON_CLIENT);
    }

    /**
     * 创建消息发布者
     *
     * @param topic 主题
     * @return {@link RedisMessagePublisher }
     * @author Tequila
     * @date 2022/08/03 08:29
     */
    public static RedisMessagePublisher createMessagePublisher(Topic topic) {
        return new RedisMessagePublisher(getStringRedisTemplate(), topic);
    }

    /**
     * 创建消息侦听器
     *
     * @param listener 侦听器
     * @param topic    主题
     * @param start    开始
     * @return {@link RedisMessageListenerContainer }
     * @author Tequila
     * @date 2022/08/03 08:39
     */
    public static RedisMessageListenerContainer createMessageListener(MessageListener listener, Topic topic, boolean start) {
        StringRedisTemplate redisTemplate = getStringRedisTemplate();
        RedisConnectionFactory connFactory = redisTemplate.getConnectionFactory();
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(Objects.requireNonNull(connFactory));
        container.addMessageListener(listener, topic);
        container.afterPropertiesSet();
        if (start) container.start();

        return container;
    }

    /**
     * 创建消息侦听器，并启动监听
     *
     * @param listener 侦听器
     * @param topic    主题
     * @return {@link RedisMessageListenerContainer }
     * @author Tequila
     * @date 2022/08/03 08:39
     */
    public static RedisMessageListenerContainer createMessageListener(MessageListener listener, Topic topic) {
        return createMessageListener(listener, topic, true);
    }

    private RedisFactory() {
        throw new IllegalStateException("Factory class");
    }
}
