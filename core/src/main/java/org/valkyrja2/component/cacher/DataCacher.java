/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: DataCacher.java
 * Date: 2022/4/7 下午4:20
 * Author: Tequila
 */

package org.valkyrja2.component.cacher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.valkyrja2.component.cacher.bean.CacheData;
import org.valkyrja2.mvc.spring.SpringUtils;
import org.valkyrja2.util.RedisConst;
import org.valkyrja2.util.RedisUtils;
import org.valkyrja2.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 数据缓存对象
 * T 需要缓存的数据对象
 *
 * @author Tequila
 * @create 2022/04/06 17:10
 **/
public abstract class DataCacher<T extends CacheData> {

    private static final Logger log = LoggerFactory.getLogger(DataCacher.class);

    /** 没有到期时间 */
    public static final int NO_EXPIRE = -1;

    /** 默认前缀 */
    private static final String DEFAULT_PREFIX = "";

    /** 默认超时 */
    private static final int DEFAULT_EXPIRE = 30;

    /** key前缀 */
    private String prefix;

    /** 默认超时时间，-1为永久 */
    private long expire;

    /** 是否添加动态时间 */
    private boolean randomExpire;

    /** 自动延长expire */
    private boolean autoTouch;

    /**
     * 上一次判断结果
     * 如果cacherExists == null，则表示没有进行过判断
     * 如果有true或者false的值，则表示已经从redis中判断过一次了
     */
    private Boolean lastExists;

    /** 当前数据 */
    private T data;

    /** RedisTemplate */
    private StringRedisTemplate redisTemplate;

    /**
     * 抽象构造方法
     *
     * @author Tequila
     * @date 2022/04/06 20:34
     */
    protected DataCacher() {
        init(DEFAULT_PREFIX, DEFAULT_EXPIRE, false, true);  // 设置默认配置
    }

    /**
     * 抽象构造方法
     *
     * @param data 数据
     * @author Tequila
     * @date 2022/04/07 23:56
     */
    protected DataCacher(T data) {
        this();
        this.data = data;
    }

    /**
     * 抽象构造方法
     *
     * @param prefix 前缀
     * @author Tequila
     * @date 2022/04/07 13:21
     */
    protected DataCacher(String prefix) {
        init(prefix, DEFAULT_EXPIRE, false, true);
    }

    /**
     * 抽象构造方法
     *
     * @param prefix 前缀
     * @param expire 到期时间
     * @author Tequila
     * @date 2022/04/06 20:34
     */
    protected DataCacher(String prefix, long expire) {
        init(prefix, expire, false, true);
    }

    /**
     * 抽象构造方法
     *
     * @param prefix       前缀
     * @param expire       到期时间
     * @param randomExpire 随机增加到期时间
     * @param autoTouch    读取时自动延长时间
     * @author Tequila
     * @date 2022/04/08 00:59
     */
    protected DataCacher(String prefix, long expire, boolean randomExpire, boolean autoTouch) {
        init(prefix, expire, randomExpire, autoTouch);
    }

    /**
     * 初始化
     *
     * @param prefix       前缀
     * @param expire       到期
     * @param randomExpire 随机增加到期时间
     * @param autoTouch    读取时自动延长到期时间
     * @author Tequila
     * @date 2022/04/08 00:58
     */
    protected void init(String prefix, long expire, boolean randomExpire, boolean autoTouch) {
        this.prefix = prefix;
        this.expire = expire;
        this.randomExpire = randomExpire;
        this.autoTouch = autoTouch;
    }

    /**
     * 是否存在
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/07 14:35
     */
    public boolean exists() {
        return exists(true);
    }

    /**
     * 是否存在
     *
     * @param useLastCheck 使用最后一次检查
     * @return boolean
     * @author Tequila
     * @date 2022/05/27 11:03
     */
    public boolean exists(boolean useLastCheck) {
        if (this.getData() != null) {
            return exists(this.getData().getId(), useLastCheck);
        } else {
            return false;
        }
    }

    /**
     * 是否存在
     *
     * @param id id
     * @return boolean
     * @author Tequila
     * @date 2022/04/08 01:26
     */
    public boolean exists(String id) {
        return exists(id, true);
    }

    /**
     * 是否存在
     *
     * @param id           id
     * @param useLastCheck 使用最后一次检查
     * @return boolean
     * @author Tequila
     * @date 2022/05/27 10:42
     */
    public boolean exists(String id, boolean useLastCheck) {
        if (!useLastCheck || lastExists == null ) {  // 如果不适用最后一次结果，或者最后一次结果是null，都重新从redis中获取
            String key = buildKey(id);
            lastExists = Boolean.TRUE.equals(getRedisTemplate().hasKey(key));
        }
        return lastExists;
    }

    /**
     * 缓存
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/08 00:05
     */
    public abstract boolean cache();

    /**
     * 异步缓存
     *
     * @param handle 处理
     * @author Tequila
     * @date 2022/04/18 17:27
     */
    public void asyncCache(AsyncHandle handle) {
        AsyncCacheThread thread = new AsyncCacheThread(this, handle);
        thread.start();
    }

    /**
     * 异步缓存
     *
     * @author Tequila
     * @date 2022/04/18 17:26
     */
    public void asyncCache() {
        asyncCache(DataCacher::cache);
    }

    /**
     * 载入数据
     *
     * @return {@link T }
     * @author Tequila
     * @date 2022/04/08 00:22
     */
    public abstract T load();

    /**
     * 载入数据
     *
     * @param id id
     * @return {@link T }
     * @author Tequila
     * @date 2022/04/08 00:22
     */
    public abstract T load(String id);

    /**
     * 延长到期时间
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/08 01:00
     */
    public boolean touch() {
        if (expire != -1) {
            String key = buildKey();
            return Boolean.TRUE.equals(getRedisTemplate().expire(key, expire(), TimeUnit.SECONDS));
        } else {
            return true;
        }
    }

    /**
     * 根据配置延长到期时间
     *
     * @author Tequila
     * @date 2022/04/08 01:12
     */
    protected void handleAutoTouch() {
        if (this.autoTouch) touch();
    }

    /**
     * 构建key
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/04/06 18:11
     */
    public String buildKey() {
        return buildKey(this.getData().getId());
    }

    /**
     * 构建key
     * 如果prefix最后没有以:结尾，这自动添加:
     *
     * @param id id
     * @return {@link String }
     * @author Tequila
     * @date 2022/04/07 17:38
     */
    protected String buildKey(String id) {
        if (this.prefix.endsWith(":")) {
            return this.prefix + id;
        } else {
            return StringUtils.concat(this.prefix, ":", id);
        }
    }

    /**
     * 获取到期时间
     *
     * @return long
     * @author Tequila
     * @date 2022/04/08 01:09
     */
    protected long expire() {
        if (expire != -1 && isRandomExpire()) {
            return RedisUtils.randomTimeout(expire);
        } else {
            return expire;
        }
    }

    /**
     * 获取RedisTemplate
     *
     * @return {@link StringRedisTemplate }
     * @author Tequila
     * @date 2022/04/06 20:51
     */
    protected StringRedisTemplate getRedisTemplate() {
        StringRedisTemplate localRedisTemplate = this.redisTemplate;
        if (localRedisTemplate == null) {
            synchronized (this) {
                localRedisTemplate = this.redisTemplate;
                if (localRedisTemplate == null) {
                    this.redisTemplate = localRedisTemplate = SpringUtils.getBean(RedisConst.STRING_REDIS_TEMPLATE);
                }
            }
        }

        return localRedisTemplate;
    }

    /**
     * 异步缓存Thread
     *
     * @author Tequila
     * @create 2022/04/18 16:23
     **/
    protected static class AsyncCacheThread extends Thread {

        private final DataCacher<?> cacher;

        private final AsyncHandle async;

        public AsyncCacheThread(DataCacher<?> cacher, AsyncHandle async) {
            super();
            this.cacher = cacher;
            this.async = async;

            this.setName(getThreadName());
        }

        @Override
        public void run() {
            log.debug("{} is running...", getThreadName());
            async.handle(this.cacher);
        }

        private String getThreadName() {
            return String.format("AsyncCacheThread(%s)", this.cacher.getClass().getName());
        }
    }

    /**
     * 异步处理
     *
     * @author Tequila
     * @create 2022/04/18 16:10
     **/
    @FunctionalInterface
    public interface AsyncHandle {
        void handle(DataCacher<?> cacher);
    }

    /* ========== Getter/Setter ========== */

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean isRandomExpire() {
        return randomExpire;
    }

    public void setRandomExpire(boolean randomExpire) {
        this.randomExpire = randomExpire;
    }

    public boolean isAutoTouch() {
        return autoTouch;
    }

    public void setAutoTouch(boolean autoTouch) {
        this.autoTouch = autoTouch;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
