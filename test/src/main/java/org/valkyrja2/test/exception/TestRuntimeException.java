/*
 * PROJECT valkyrja2
 * test/TestRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.test.exception;

/**
 * 测试用运行期错误
 *
 * @author Tequila
 * @create 2022/06/30 21:26
 **/
public class TestRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 885064339019114943L;

    public TestRuntimeException() {
        super();
    }

    public TestRuntimeException(String message) {
        super(message);
    }

    public TestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestRuntimeException(Throwable cause) {
        super(cause);
    }

    public TestRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
