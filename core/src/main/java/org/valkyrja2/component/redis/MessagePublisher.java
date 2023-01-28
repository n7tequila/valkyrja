package org.valkyrja2.component.redis;

/**
 * Redis pub/sub Publish接口
 *
 * @author Tequila
 * @create 2022/08/02 21:15
 **/
public interface MessagePublisher {

    /**
     * 发布消息
     *
     * @param message 消息
     * @author Tequila
     * @date 2022/08/03 08:36
     */
    void publish(String message);
}
