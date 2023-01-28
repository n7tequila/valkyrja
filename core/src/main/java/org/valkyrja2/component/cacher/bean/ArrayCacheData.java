/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: ArrayCacheData.java
 * Date: 2022/4/15 下午4:27
 * Author: Tequila
 */

package org.valkyrja2.component.cacher.bean;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 缓存Bean对象(Set)
 *
 * @author: Tequila
 * @create: 2022/04/15 16:27
 **/
public interface ArrayCacheData extends CacheData {

    /**
     * 值列表
     *
     * @return {@link Set<String> }
     * @author Tequila
     * @date 2022/04/15 16:33
     */
    Collection<String> values();

    /**
     * 新值列表
     *
     * @return {@link Set<String> }
     * @author Tequila
     * @date 2022/04/15 16:33
     */
    Collection<String> newValues();

    /**
     * 删除值列表
     *
     * @return {@link Collection<String> }
     * @author Tequila
     * @date 2022/04/19 10:51
     */
    Collection<String> delValues();

    /**
     * 值是否存在
     * newValues不参与判断
     *
     * @param value 值
     * @return boolean
     * @author Tequila
     * @date 2022/04/18 12:01
     */
    default boolean valueExists(String value) {
        return values().contains(value);
    }
}
