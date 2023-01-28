package org.valkyrja2.component.idempotent.exception;

/**
 * 重复请求错误
 *
 * @author Tequila
 * @create 2022/08/05 11:54
 **/
public class DuplicateRequestException extends IdempotentException {

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
