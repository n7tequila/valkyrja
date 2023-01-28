package org.valkyrja2.mvc;

import org.valkyrja2.component.api.AccessKey;
import org.valkyrja2.component.api.AccessKeyForm;
import org.valkyrja2.component.api.ApiManager;
import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.util.SystemSetting;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * api controller抽奖类
 *
 * @author Tequila
 * @create 2022/08/01 21:24
 **/
public abstract class AbstractApiController {

    private static final long OVER_TIME = 60L * 1000;

    /**
     * api签名是否有效
     *
     * @param apiForm api表单
     * @param signKey 签名key
     * @return boolean
     * @author Tequila
     * @date 2022/08/01 21:28
     */
    protected boolean isApiSignatureValid(StandardApiForm<? extends FormObject> apiForm, String signKey) {
        return (apiForm.isSignatureValid(signKey));
    }

    /**
     * api签名是否有效
     *
     * @param apiForm api表单
     * @return boolean
     * @author Tequila
     * @date 2022/08/02 00:03
     */
    protected boolean isApiSignatureValid(StandardApiForm<? extends FormObject> apiForm) {
        AccessKey accessKey = apiForm.getAccessKey();

        if (accessKey != null) {
            return isApiSignatureValid(apiForm, accessKey.getSignKey());
        } else {
            throw new BizRuntimeException(ResponseCode.UNAUTH_ACCESS);
        }
    }

    /**
     * 检查api签名
     *
     * @param apiForm api表单
     * @param signKey 签名key
     * @author Tequila
     * @date 2022/08/01 21:28
     */
    protected void checkApiSignature(StandardApiForm<? extends FormObject> apiForm, String signKey) {
        if (!isApiSignatureValid(apiForm, signKey)) {
            throw new BizRuntimeException(ResponseCode.SIGNATURE_ERROR);
        }
    }

    /**
     * 检查api签名
     *
     * @param apiForm api表单
     * @author Tequila
     * @date 2022/08/01 23:43
     */
    protected void checkApiSignature(StandardApiForm<? extends FormObject> apiForm) {
        AccessKey accessKey = apiForm.getAccessKey();

         if (accessKey != null) {
            checkApiSignature(apiForm, accessKey.getSignKey());
        } else {
            throw new BizRuntimeException(ResponseCode.UNAUTH_ACCESS);
        }
    }

    /**
     * 请求是否超时
     *
     * @param apiForm api表单
     * @return boolean
     * @author Tequila
     * @date 2022/08/01 21:16
     */
    protected boolean isRequestOvertime(RequestTimeForm apiForm) {
        return (!SystemSetting.getInstance().isDebugMode()
                && Math.abs(System.currentTimeMillis() - formatTimestamp(apiForm.getTime())) > OVER_TIME);
    }

    /**
     * 检查请求超时
     *
     * @param apiForm api表单
     * @author Tequila
     * @date 2022/08/01 21:18
     */
    protected void checkRequestOvertime(StandardApiForm<? extends FormObject> apiForm) {
        if (isRequestOvertime(apiForm)) {
            throw new BizRuntimeException(ResponseCode.REQUEST_OVERTIME);
        }
    }

    /**
     * 校验bodyForm
     *
     * @param apiForm   api表单
     * @param bodyClass 表单类
     * @param groups    组
     * @author Tequila
     * @date 2022/08/01 20:38
     */
    protected <T extends FormObject> void validBodyForm(StandardApiForm<T> apiForm, Class<T> bodyClass, Class<?>...groups) {
        FormValidResult formValidResult = apiForm.isBodyValid(bodyClass, groups);
        if (formValidResult.isNotValid()) {
            throw new ValidateException(formValidResult.getViolations());
        }
    }

    /**
     * 批处理检查api表单
     *
     * @param apiForm   api表单
     * @param bodyClass 体类
     * @param groups    组
     * @author Tequila
     * @date 2022/08/02 01:01
     */
    protected <T extends FormObject> void batchCheckApiForm(StandardApiForm<T> apiForm, Class<T> bodyClass, Class<?>...groups) {
        checkApiSignature(apiForm);
        checkRequestOvertime(apiForm);
        validBodyForm(apiForm, bodyClass, groups);
    }

    /**
     * 时间戳格式，如果是10位，则自动延长为13位
     *
     * @param time 时间
     * @return long
     * @author Tequila
     * @date 2022/07/21 16:49
     */
    private long formatTimestamp(String time) {
        if (time.length() == 10) {
            return Long.parseLong(time) * 1000L;
        } else {
            return Long.parseLong(time);
        }
    }
}
