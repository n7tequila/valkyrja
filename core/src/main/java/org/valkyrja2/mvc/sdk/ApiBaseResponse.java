/*
 * PROJECT valkyrja2
 * core/ApiBaseResponse.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Base64;
import org.valkyrja2.mvc.ResponseCode;
import org.valkyrja2.util.AESCoder;
import org.valkyrja2.util.StringUtils;

import javax.persistence.Transient;

/**
 * api 基础响应对象
 *
 * @author Tequila
 * @create 2022/07/27 15:44
 **/
public class ApiBaseResponse<T> {

    private String code;

    private String message;

    private String hostId;

    private String requestId;

    private String time;

    private String httpCode;

    private T data;

    private Object errData;

    public ApiBaseResponse() {
        super();
    }

    @JsonIgnore @Transient
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.code().equals(code);
    }

    /**
     * 解密数据
     *
     * @param encryptText 加密文本
     * @param key         key
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 15:47
     */
    public String aesDecrypt(String encryptText, String key) {
        if (StringUtils.isNotBlank(encryptText) && StringUtils.isNotBlank(key)) {
            try {
                return new String(AESCoder.decrypt(Base64.decodeBase64(encryptText), key));
            } catch (Exception e) {
                // 如果报错，则返回原数值
            }
        }

        return encryptText;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getErrData() {
        return errData;
    }

    public void setErrData(Object errData) {
        this.errData = errData;
    }
}
