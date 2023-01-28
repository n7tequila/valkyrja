/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: ValueDataCacher.java
 * Date: 2022/4/7 下午4:24
 * Author: Tequila
 */

package org.valkyrja2.component.cacher;

import org.springframework.data.redis.core.BoundValueOperations;
import org.valkyrja2.component.cacher.bean.AbstractCacheData;
import org.valkyrja2.component.cacher.bean.CacheData;
import org.valkyrja2.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 值-缓存对象
 *
 * @author: Tequila
 * @create: 2022/04/07 16:24
 **/
public class ValueDataCacher<T extends CacheData> extends DataCacher<T> {

    /** 设置模式 */
    public static final int MODE_NORMAL = 0;  // use set
    public static final int MODE_NX     = 1;  // use set ... nx, 这是默认模式
    public static final int MODE_XX     = 2;  // use set ... xx

    /** 默认模式 */
    protected int mode = MODE_NX;

    public ValueDataCacher() {
        super();
        init(MODE_NX);
    }

    public ValueDataCacher(T data) {
        super(data);
        init(MODE_NX);
    }

    public ValueDataCacher(T data, int mode) {
        super(data);
        init(mode);
    }

    public ValueDataCacher(String prefix) {
        super(prefix);
        init(MODE_NX);
    }

    public ValueDataCacher(String prefix, long expire) {
        super(prefix, expire);
        init(MODE_NX);
    }

    public ValueDataCacher(String prefix, long expire, boolean randomExpire, boolean autoTouch) {
        super(prefix, expire, randomExpire, autoTouch);
        init(MODE_NX);
    }

    /**
     * 构造方法
     *
     * @param prefix       前缀
     * @param expire       到期时间
     * @param randomExpire 随机增加到期时间
     * @param autoTouch    读取时自动延长时间
     * @param mode         模式
     * @author Tequila
     * @date 2022/04/08 01:03
     */
    public ValueDataCacher(String prefix, long expire, boolean randomExpire, boolean autoTouch, int mode) {
        super(prefix, expire, randomExpire, autoTouch);
        init(mode);
    }

    /**
     * 初始化
     *
     * @param mode 模式
     * @author Tequila
     * @date 2022/04/07 23:54
     */
    private void init(int mode) {
        this.mode = mode;
    }

    /**
     * 默认缓存操作
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/06 20:35
     */
    @Override
    public boolean cache() {
        return cache(this.mode);
    }

    /**
     * 默认缓存操作 nx mode
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/07 12:54
     */
    public boolean cacheNX() {
        return cache(MODE_NX);
    }

    /**
     * 默认缓存操作 xx mode
     *
     * @return boolean
     * @author Tequila
     * @date 2022/04/07 12:54
     */
    public boolean cacheXX() {
        return cache(MODE_XX);
    }

    /**
     * 更新
     *
     * @return boolean
     * @author Tequila
     * @date 2022/05/31 15:01
     */
    public boolean update() {
        return cache(MODE_NORMAL);
    }

    /**
     * 更新(使用xx模式）
     *
     * @return boolean
     * @author Tequila
     * @date 2022/05/31 15:01
     */
    public boolean updateXX() {
        return cache(MODE_XX);
    }

    /**
     * 默认缓存操作
     *
     * @param data 数据
     * @param mode 模式
     * @return boolean
     * @author Tequila
     * @date 2022/04/06 21:28
     */
    private boolean cache(int mode) {
        String key = buildKey();
        String value = getData().value();
        BoundValueOperations<String, String> opts = getRedisTemplate().boundValueOps(key);

        Boolean result;
        switch (mode) {
            case MODE_NORMAL:
                opts.set(value, this.expire(), TimeUnit.SECONDS);
                result = Boolean.TRUE;
                break;
            case MODE_NX:
                result = opts.setIfAbsent(value, this.expire(), TimeUnit.SECONDS);
                break;
            case MODE_XX:
                result = opts.setIfPresent(value, this.expire(), TimeUnit.SECONDS);
                break;
            default:
                throw new IllegalStateException(String.format("mode `%d` is error", this.mode));
        }

        return Boolean.TRUE.equals(result);
    }

    /**
     * 获取数据
     *
     * @return {@link T }
     * @author Tequila
     * @date 2022/04/07 17:40
     */
    public T load() {
        if (getData() != null && this.getData().getId() != null) {
            return load(this.getData().getId());
        } else {
            return null;
        }
    }

    /**
     * 获取数据
     *
     * @return {@link T }
     * @author Tequila
     * @date 2022/04/07 17:17
     */
    public T load(String id) {
        T data;

        String key = buildKey(id);
        String json = getRedisTemplate().opsForValue().get(key);
        Class<T> klass = ClassUtils.getClassGenericType(this.getClass(), 0);
        if (json != null) {
            data = AbstractCacheData.of(json, klass);
        } else {
            data = null;
        }
        this.setData(data);
        handleAutoTouch();  // 根据配置执行touch()
        return data;
    }
}
