/*
 * PROJECT valkyrja2
 * util/JsonObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

/**
 * json可转换对象接口<br>
 * 标记了对象可以直接序列化成json字符串
 *
 * @author Tequila
 * @create 2022/05/25 09:38
 **/
public interface JsonObject {

    /**
     * 将对象转换为json字符串
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/25 09:43
     */
    default String toJson() {
        return Jackson2Utils.obj2json(this, true);
    }
}
