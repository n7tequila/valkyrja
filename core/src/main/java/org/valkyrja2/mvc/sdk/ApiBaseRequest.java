/*
 * PROJECT valkyrja2
 * core/ApiBaseRequest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.valkyrja2.util.StringUtils;

import javax.persistence.Transient;

/**
 * api 基础请求对象
 *
 * @author Tequila
 * @create 2022/07/27 15:36
 **/
public class ApiBaseRequest<T> {

    private String appCode;

    private String body;

    private String nonceStr;

    private String time;

    private String sign;

    @JsonIgnore @Transient
    private T bodyForm;

    public ApiBaseRequest() {
        init();
    }

    public ApiBaseRequest(String appCode, T bodyForm) {
        init();

        this.appCode = appCode;
        this.bodyForm = bodyForm;
    }

    private void init() {
        this.nonceStr = StringUtils.randomStr(32);
        this.time = String.valueOf(System.currentTimeMillis());
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public T getBodyForm() {
        return bodyForm;
    }

    public void setBodyForm(T bodyForm) {
        this.bodyForm = bodyForm;
    }
}
