/*
 * PROJECT valkyrja2
 * core/IdempotentRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.idempotent.exception;

/**
 * 幂等错误
 *
 * @author Tequila
 * @create 2022/07/14 21:13
 **/
public class IdempotentRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 672474548526839428L;

    public IdempotentRuntimeException() {
    }

    public IdempotentRuntimeException(String message) {
        super(message);
    }

    public IdempotentRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentRuntimeException(Throwable cause) {
        super(cause);
    }

    public IdempotentRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
