/*
 * PROJECT valkyrja2
 * core/MapResponseObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.exception.ValidateException;

import java.util.TreeMap;

/**
 * Map类型的ReponseObject对象
 *
 * @author Tequila
 * @create 2022/07/03 22:15
 **/
public class MapResponseObject extends ResponseObject<TreeMap<String, Object>> {

    public MapResponseObject() {
        super();
    }

    public MapResponseObject(Object data) {
        super(data);
    }

	public MapResponseObject(String key, Object value) {
        initDefaultValue(key, value);
    }

    public MapResponseObject(ResponseCode responseCode, TreeMap<String, Object> data) {
        super(responseCode, data);
    }

    public MapResponseObject(ResponseCode responseCode) {
        super(responseCode);
    }

    public MapResponseObject(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }

    public MapResponseObject(BizRuntimeException re) {
        super(re);
    }

    public MapResponseObject(ValidateException ve) {
        super(ve);
    }

    public MapResponseObject(BizRuntimeException re, Object errData) {
        super(re, errData);
    }

    public MapResponseObject(RuntimeException re) {
        super(re);
    }

    public MapResponseObject(RuntimeException re, Object errData) {
        super(re, errData);
    }

    /**
     * 初始化默认值
     *
     * @param key   key
     * @param value 值
     * @author Tequila
     * @date 2022/07/02 23:45
     */
    protected void initDefaultValue(String key, Object value) {
        initDefaultValue(ResponseCode.SUCCESS);
        setKVData(key, value);
    }

    /**
     * 设置key-value类型的数据
     *
     * @param key   key
     * @param value 值
     * @return {@link MapResponseObject }
     * @author Tequila
     * @date 2022/07/03 23:21
     */
    public MapResponseObject setKVData(String key, Object value) {
        if (getData() == null) {
            setData(new TreeMap<>());
        }
        getData().put(key, value);

        return this;
    }
}
