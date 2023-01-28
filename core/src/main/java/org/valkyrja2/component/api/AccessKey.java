package org.valkyrja2.component.api;

import org.apache.shiro.authz.Permission;
import org.valkyrja2.core.domain.IdObject;

import java.util.Set;

/**
 * api接入用户接口
 *
 * @author Tequila
 * @create 2022/08/01 21:33
 **/
public interface AccessKey extends IdObject<String> {

    /**
     * 获取 appCode
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/01 21:34
     */
    String getAppCode();

    /**
     * 获取签名key
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/01 21:34
     */
    String getSignKey();

    /**
     * 是否加密
     *
     * @return boolean
     * @author Tequila
     * @date 2022/08/01 21:34
     */
    boolean isEncrypt();

    /**
     * 获取加密key
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/01 21:50
     */
    String getEncryptKey();

    /**
     * 获取加密模式
     *
     * @return {@link EncryptMode }
     * @author Tequila
     * @date 2022/08/01 21:51
     */
    EncryptMode getEncryptMode();

    /**
     * 获得许可
     *
     * @return {@link Set }<{@link Permission }>
     * @author Tequila
     * @date 2022/08/01 23:40
     */
    Set<String> getPermissions();

    /**
     * 是否可用
     *
     * @return boolean
     * @author Tequila
     * @date 2022/08/02 00:09
     */
    boolean isAvailable();
}
