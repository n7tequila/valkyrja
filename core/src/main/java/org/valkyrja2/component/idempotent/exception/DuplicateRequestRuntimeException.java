/*
 * PROJECT valkyrja2
 * core/DuplicateRequestRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.idempotent.exception;

/**
 * 重复请求错误
 *
 * @author Tequila
 * @create 2022/07/14 22:12
 **/
public class DuplicateRequestRuntimeException extends IdempotentRuntimeException {

    public DuplicateRequestRuntimeException() {
    }

    public DuplicateRequestRuntimeException(String message) {
        super(message);
    }

    public DuplicateRequestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRequestRuntimeException(Throwable cause) {
        super(cause);
    }

    public DuplicateRequestRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
