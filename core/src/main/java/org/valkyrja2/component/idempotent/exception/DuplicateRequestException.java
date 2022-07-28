/*
 * PROJECT valkyrja2
 * core/DuplicateRequestException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.idempotent.exception;

/**
 * 重复请求错误
 *
 * @author Tequila
 * @create 2022/07/14 22:12
 **/
public class DuplicateRequestException extends IdempotentRuntimeException {

    public DuplicateRequestException() {
    }

    public DuplicateRequestException(String message) {
        super(message);
    }

    public DuplicateRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRequestException(Throwable cause) {
        super(cause);
    }

    public DuplicateRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
