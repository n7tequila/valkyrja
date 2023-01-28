/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: AbstractCacheData.java
 * Date: 2022/4/15 下午8:01
 * Author: Tequila
 */

package org.valkyrja2.component.cacher.bean;

import org.valkyrja2.util.Jackson2Utils;

import java.io.IOException;
import java.util.Objects;

/**
 * 抽象缓存数据对象
 *
 * @author: Tequila
 * @create: 2022/04/07 17:41
 **/
public class AbstractCacheData implements CacheData {

    private String id;

    protected AbstractCacheData() {
        super();
    }

    protected AbstractCacheData(String id) {
        this.id = id;
    }

    /**
     * 从值转换成对象
     *
     * @param json  json
     * @param klass klass
     * @return {@link T }
     * @author Tequila
     * @date 2022/04/07 15:42
     */
    public static <T extends CacheData> T of(String json, Class <T> klass) {
        try {
            return Jackson2Utils.json2obj(json, klass);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCacheData that = (AbstractCacheData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
