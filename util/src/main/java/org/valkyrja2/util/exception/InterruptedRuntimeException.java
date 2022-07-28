/*
 * PROJECT valkyrja2
 * util/InterruptedRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util.exception;

/**
 * 线程中断的运行期错误
 *
 * @author Tequila
 * @create 2022/06/28 14:37
 **/
public class InterruptedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1234383243823552565L;

    public InterruptedRuntimeException() {
        super();
    }

    public InterruptedRuntimeException(String message) {
        super(message);
    }

    public InterruptedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterruptedRuntimeException(Throwable cause) {
        super(cause);
    }
}
