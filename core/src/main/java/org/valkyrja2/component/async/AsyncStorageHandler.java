package org.valkyrja2.component.async;

import org.valkyrja2.util.ClassUtils;
import org.valkyrja2.util.Jackson2Utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 异步存储处理对象
 * @param <T> 具体的处理对象类型
 *
 * @author Tequila
 * @create 2022/08/11 21:45
 **/
public interface AsyncStorageHandler<T> {


    /**
     * 处理器执行具体保存操作
     *
     * @param obj obj
     * @author Tequila
     * @date 2022/08/11 22:31
     */
    boolean handleStorageSave(Object obj);


    /**
     * 获取异步存储的名称
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/11 22:13
     */
    default String getName() {
        Class<T> type = ClassUtils.getInterfaceGenericType(this.getClass(), 0);
        Objects.requireNonNull(type, "Can not get generic type, please define it.");

        return type.getName();
    }

    /**
     * 将对象转换成json
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/11 22:27
     */
    default String toJson(Object obj) {
        return Jackson2Utils.obj2json(obj, false);
    }


    /**
     * 解析json为对象
     *
     * @param json json
     * @return {@link T }
     * @author Tequila
     * @date 2022/08/11 22:27
     */
    default T parseJson(String json) throws IOException {
        Class<T> type = ClassUtils.getInterfaceGenericType(this.getClass(), 0);
        Objects.requireNonNull(type, "Can not get generic type, please define it.");

        return Jackson2Utils.json2obj(json, type);
    }

}
