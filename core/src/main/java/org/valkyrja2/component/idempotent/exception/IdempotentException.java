package org.valkyrja2.component.idempotent.exception;

import java.io.IOException;

/**
 * 幂等报错
 *
 * @author Tequila
 * @create 2022/08/05 11:49
 **/
public class IdempotentException extends Exception {

    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
