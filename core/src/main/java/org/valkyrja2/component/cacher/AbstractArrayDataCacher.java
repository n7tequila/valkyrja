/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: AbstractArrayDataCacher.java
 * Date: 2022/4/7 下午4:36
 * Author: Tequila
 */

package org.valkyrja2.component.cacher;


import org.valkyrja2.component.cacher.bean.ArrayCacheData;
import org.valkyrja2.util.ClassUtils;

/**
 * Set-缓存对象
 *
 * @author: Tequila
 * @create: 2022/04/07 16:35
 **/
public abstract class AbstractArrayDataCacher<T extends ArrayCacheData> extends DataCacher<T> {

    protected AbstractArrayDataCacher() {
        super();
    }

    protected AbstractArrayDataCacher(T data) {
        super(data);
    }

    protected AbstractArrayDataCacher(String prefix) {
        super(prefix);
    }

    protected AbstractArrayDataCacher(String prefix, long expire) {
        super(prefix, expire);
    }

    protected AbstractArrayDataCacher(String prefix, long expire, boolean randomExpire, boolean autoTouch) {
        super(prefix, expire, randomExpire, autoTouch);
    }

    /**
     * 判断set中数据是否存在
     *
     * @param id    id
     * @param value 值
     * @return boolean
     * @author Tequila
     * @date 2022/04/15 16:41
     */
    public abstract boolean valueExists(String id, String value);

    /**
     * 判断set中数据是否存在
     *
     * @param value 值
     * @return boolean
     * @author Tequila
     * @date 2022/04/15 16:42
     */
    public boolean valueExists(String value) {
        if (this.getData() != null) {
            return valueExists(this.getData().getId(), value);
        } else {
            return false;
        }
    }

    @Override
    public boolean cache() {
        /* 缓存时，先处理新增的数据，再处理删除数据 */

        for (String v : this.getData().newValues()) {
            if (!cacheValue(this.getData().getId(), v)) {
                return false;
            }

            this.getData().values().add(v);
        }
        this.getData().newValues().clear();  // 全部缓存成功，将newValues中的数据清空

        for (String v: this.getData().delValues()) {
            if (!delValue(this.getData().getId(), v)) {
                return false;
            }

            this.getData().newValues().remove(v);
        }
        this.getData().delValues().clear();  // 全部删除成功，则将delValues中的数据清空

        handleAutoTouch();

        return true;
    }

    /**
     * 缓存一个值
     *
     * @param id    id
     * @param value 值
     * @return boolean
     * @author Tequila
     * @date 2022/04/15 17:14
     */
    protected abstract boolean cacheValue(String id, String value);

    /**
     * 删除值
     *
     * @param id    id
     * @param value 值
     * @return boolean
     * @author Tequila
     * @date 2022/04/19 11:47
     */
    protected abstract boolean delValue(String id, String value);

    /**
     * 设置缓存数据对象
     *
     * @param id id
     * @author Tequila
     * @date 2022/04/15 17:03
     */
    public void setData(String id) {
        setData(id, false);
    }

    /**
     * 设置缓存数据对象
     *
     * @param id    id
     * @param force 强制
     * @author Tequila
     * @date 2022/04/19 10:59
     */
    public void setData(String id, boolean force) {
        if (!force && this.getData() != null && id.equals(this.getData().getId())) return;  // 如果id和已经设置的值是一致的，则不重新设置数据对象

        Class<T> klass = getDataClass();
        T t;
        try {
            assert klass != null;
            t = klass.newInstance();
            t.setId(id);
            this.setData(t);
        } catch (Exception e) {
            throw new NullPointerException("Get data class is null.");
        }
    }

    /**
     * 获取data class type
     *
     * @return {@link Class<T> }
     * @author Tequila
     * @date 2022/04/18 11:02
     */
    protected Class<T> getDataClass() {
        return ClassUtils.getClassGenericType(this.getClass(), 0);
    }

    /**
     * 添加
     *
     * @param s 字符串
     * @author Tequila
     * @date 2022/04/18 11:25
     */
    public boolean add(String s) {
        return this.getData().newValues().add(s);
    }

    /**
     * 删除
     *
     * @param s 字符串
     * @return boolean
     * @author Tequila
     * @date 2022/04/18 11:31
     */
    public boolean remove(String s) {
        if  (this.getData().newValues().contains(s)) {
            return this.getData().newValues().remove(s);
        } else {
            this.getData().delValues().add(s);
            return this.getData().values().remove(s);
        }
    }
}
