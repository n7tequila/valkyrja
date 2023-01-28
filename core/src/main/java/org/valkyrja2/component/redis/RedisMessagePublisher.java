package org.valkyrja2.component.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.Topic;

/**
 * redis 消息发布者
 *
 * @author Tequila
 * @create 2022/08/03 08:36
 **/
public class RedisMessagePublisher implements MessagePublisher {

    private StringRedisTemplate redisTemplate;

    private Topic topic;

    public RedisMessagePublisher() {
        super();
    }

    public RedisMessagePublisher(StringRedisTemplate redisTemplate, Topic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
