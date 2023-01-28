/*
 * copyright(c) 2021 优证
 * projectName: saas saas.core
 * fileName: CacheData.java
 * Date: 2022/4/7 下午4:21
 * Author: Tequila
 */

package org.valkyrja2.component.cacher.bean;

/**
 * @author: Tequila
 * @create: 2022/04/07 16:20
 **/

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.valkyrja2.util.Jackson2Utils;

/**
 * 缓存Bean对象
 *
 * @author Tequila
 * @create 2022/11/16 14:42
 **/
public interface CacheData {

    /**
     * get id
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/04/06 17:58
     */
    String getId();

    /**
     * set id
     *
     * @param id id
     * @author Tequila
     * @date 2022/04/15 19:59
     */
    void setId(String id);

    /**
     * 转换成value
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/04/06 17:23
     */
    @JsonIgnore
    default String value() {
        return Jackson2Utils.obj2json(this, true);
    }
}
