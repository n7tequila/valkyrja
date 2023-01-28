/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: SetDataCacher.java
 * Date: 2022/4/15 下午7:44
 * Author: Tequila
 */

package org.valkyrja2.component.cacher;

import org.springframework.data.redis.core.BoundSetOperations;
import org.valkyrja2.component.cacher.SetDataCacher.SetCacheData;
import org.valkyrja2.component.cacher.bean.AbstractCacheData;
import org.valkyrja2.component.cacher.bean.ArrayCacheData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Set缓存对象
 * <pre>
 *      SetDataCacher cacher = new SetDataCacher("prefix:", "key", -1);
 *      cacher.add("value1");
 *      cacher.add("value2");
 *      cacher.cache();
 *      cacher.valueExists("1");
 *      cacher.valueExists("3");
 * </pre>
 *
 * @author: Tequila
 * @create: 2022/04/15 19:44
 **/
public class SetDataCacher extends AbstractArrayDataCacher<SetCacheData> {

    public SetDataCacher() {
    }

    public SetDataCacher(SetCacheData data) {
        super(data);
    }

    public SetDataCacher(String prefix) {
        super(prefix);
    }

    public SetDataCacher(String prefix, String id, long expire) {
        super(prefix, expire);
        setData(id);
    }

    public SetDataCacher(String prefix, String id, long expire, boolean randomExpire, boolean autoTouch) {
        super(prefix, expire, randomExpire, autoTouch);
        setData(id);
    }

    @Override
    public boolean valueExists(String id, String value) {
        String key = buildKey(id);
        return this.getData().valueExists(value) ||
                Boolean.TRUE.equals(getRedisTemplate().boundSetOps(key).isMember(value)
        );
    }

    @Override
    protected boolean cacheValue(String id, String value) {
        String key = buildKey(id);
        Long result = getRedisTemplate().boundSetOps(key).add(value);
        return (result != null && result.intValue() == 1);  // 如果缓存成功，则将新的值添加到values中
    }

    @Override
    protected boolean delValue(String id, String value) {
        String key = buildKey(id);
        Long result = getRedisTemplate().boundSetOps(key).remove(value);
        return (result != null && result.intValue() == 1);
    }

    @Override
    public SetCacheData load() {
        return load(this.getData().getId());
    }

    @Override
    public SetCacheData load(String id) {
        SetCacheData data = new SetCacheData();

        String key = buildKey(id);
        if (exists(id)) {
            BoundSetOperations<String, String> opts = getRedisTemplate().boundSetOps(key);
            Set<String> members = opts.members();
            if (members != null) {
                data.values().addAll(members);
            }
            setData(data);

            handleAutoTouch();
        }

        return data;
    }

    @Override
    protected Class<SetCacheData> getDataClass() {
        return SetCacheData.class;
    }

    /**
     * Set缓存数据
     *
     * @author Tequila
     * @create 2022/04/18 11:18
     **/
    public static class SetCacheData extends AbstractCacheData implements ArrayCacheData {

        private final Set<String> values = new HashSet<>();

        private final Set<String> newValues = new HashSet<>();

        private final Set<String> delValues = new HashSet<>();

        public SetCacheData() {
        }

        public SetCacheData(String id) {
            super(id);
        }

        @Override
        public String toString() {
            return String.format("values: %s, newValues: %s", values.toString(), newValues.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            SetCacheData that = (SetCacheData) o;
            return values.equals(that.values) && newValues.equals(that.newValues) && delValues.equals(that.delValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), values, newValues, delValues);
        }

        @Override
        public Collection<String> values() {
            return values;
        }

        @Override
        public Collection<String> newValues() {
            return newValues;
        }

        @Override
        public Collection<String> delValues() {
            return delValues;
        }
    }
}
