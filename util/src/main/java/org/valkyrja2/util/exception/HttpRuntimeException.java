/*
 * PROJECT valkyrja2
 * util/HttpRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util.exception;

/**
 * Http运行期错误
 *
 * @author Tequila
 * @create 2022/07/01 18:21
 **/
public class HttpRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -238421841095493156L;

    public HttpRuntimeException() {
    }

    public HttpRuntimeException(String message) {
        super(message);
    }

    public HttpRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRuntimeException(Throwable cause) {
        super(cause);
    }

    public HttpRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
