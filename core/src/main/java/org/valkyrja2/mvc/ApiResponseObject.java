/*
 * PROJECT valkyrja2
 * core/ApiResponseObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.exception.ValidateException;
import org.valkyrja2.util.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * API返回值
 *
 * @author Tequila
 * @create 2022/07/25 00:12
 **/
public class ApiResponseObject<T> extends ResponseObject<T> {

    public ApiResponseObject() {
    }

    public ApiResponseObject(Object data) {
        super(data);
    }

    public ApiResponseObject(ResponseCode responseCode, T data) {
        super(responseCode, data);
    }

    public ApiResponseObject(ResponseCode responseCode) {
        super(responseCode);
    }

    public ApiResponseObject(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }

    public ApiResponseObject(BizRuntimeException re) {
        super(re);
    }

    public ApiResponseObject(ValidateException ve) {
        super(ve);
    }

    public ApiResponseObject(BizRuntimeException re, Object errData) {
        super(re, errData);
    }

    public ApiResponseObject(RuntimeException re) {
        super(re);
    }

    public ApiResponseObject(RuntimeException re, Object errData) {
        super(re, errData);
    }

    @Override
    public void flushResponseHttpCode(HttpServletResponse response) {
        lockHttpStatus();
        super.flushResponseHttpCode(response);
    }

    private void lockHttpStatus() {
        this.setHttpCode(HttpStatus.OK.value());
    }
}
