/*
 * PROJECT valkyrja2
 * core/ApiResponseParseException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

/**
 * api 响应信息body转换错误
 *
 * @author Tequila
 * @create 2022/07/27 17:06
 **/
public class ApiResponseParseException extends ApiException {

    private static final long serialVersionUID = -1003850708680337423L;

    public ApiResponseParseException() {
    }

    public ApiResponseParseException(String message) {
        super(message);
    }

    public ApiResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiResponseParseException(Throwable cause) {
        super(cause);
    }
}
