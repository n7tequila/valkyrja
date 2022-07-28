/*
 * PROJECT valkyrja2
 * core/ApiException.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

import java.io.IOException;

/**
 * Api Exception
 *
 * @author Tequila
 * @create 2022/07/27 17:07
 **/
public class ApiException extends IOException {

    private static final long serialVersionUID = 5860928089577516556L;

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }
}
