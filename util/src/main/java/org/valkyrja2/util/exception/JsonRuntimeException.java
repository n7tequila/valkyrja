/*
 * PROJECT valkyrja2
 * util/JsonRuntimeException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util.exception;

/**
 * json运行期错误
 *
 * @author Tequila
 * @create 2022/05/05 12:03
 **/
public class JsonRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 7679153304131230689L;

    public JsonRuntimeException() {
        super();
    }

    public JsonRuntimeException(String message) {
        super(message);
    }

    public JsonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonRuntimeException(Throwable cause) {
        super(cause);
    }
}
