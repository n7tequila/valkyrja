package org.valkyrja2.component.api;

/**
 * 含有appCode字段的form
 *
 * @author Tequila
 * @create 2022/08/01 23:47
 **/
public interface AccessKeyForm {

    /**
     * 获取 appCode
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/01 23:47
     */
    String getAppCode();

    default AccessKey getAccessKey() {
        return ApiManager.getInstance().get(getAppCode());
    }
}
