package org.valkyrja2.component.api;

import java.util.List;

/**
 * api user 仓库对象
 *
 * @author Tequila
 * @create 2022/08/01 21:56
 **/
public interface AccessKeyRepository {

    /**
     * 找到所有AccessKey
     *
     * @return {@link List }<{@link AccessKey }>
     * @author Tequila
     * @date 2022/08/02 00:27
     */
    List<AccessKey> findAll();

}
