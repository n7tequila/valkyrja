package org.valkyrja2.component.cacher.bean;

import com.fasterxml.jackson.core.JacksonException;
import org.valkyrja2.util.Jackson2Utils;

import java.io.IOException;

/**
 * json缓存数据对象
 *
 * @author Tequila
 * @create 2022/11/16 16:12
 **/
public class JsonCacheData<T> extends AbstractCacheData {

    private String json;

    public JsonCacheData() {
        super();
    }

    public JsonCacheData(String id) {
        super(id);
    }

    public JsonCacheData(String id, String json) {
        super(id);
        this.json = json;
    }

    /**
     * @param obj obj
     * @return {@link JsonCacheData }
     * @throws JacksonException 杰克逊例外
     * @author Tequila
     * @date 2022/11/16 16:37
     */
    public JsonCacheData<T> of(T obj) throws JacksonException {
        this.json = Jackson2Utils.obj2json(obj);

        return this;
    }

    /**
     * 解析json
     *
     * @param klass klass
     * @return {@link T }
     * @author Tequila
     * @date 2022/11/16 16:21
     */
    public T parseJson(Class<T> klass) throws IOException {
        return Jackson2Utils.json2obj(json, klass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonCacheData)) return false;
        if (!super.equals(o)) return false;

        JsonCacheData that = (JsonCacheData) o;

        return json != null ? json.equals(that.json) : that.json == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
