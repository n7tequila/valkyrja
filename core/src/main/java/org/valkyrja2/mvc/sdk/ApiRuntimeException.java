package org.valkyrja2.mvc.sdk;

public class ApiRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1603343934967487992L;

	/** 错误响应对象 */
	private final ApiBaseResponse<?> errResponse;

	public ApiRuntimeException(ApiBaseResponse<?> errResponse) {
		super(errResponse.getMessage());
		this.errResponse = errResponse;
	}

	public ApiRuntimeException(String message, Throwable cause) {
		super(message, cause);
		this.errResponse = null;
	}

	public ApiRuntimeException(String message, ApiBaseResponse<?> errResponse) {
		super(message);
		this.errResponse = errResponse;
	}

	public ApiRuntimeException(String message, ApiBaseResponse<?> errResponse, Throwable cause) {
		super(message, cause);
		this.errResponse = errResponse;
	}

	public ApiRuntimeException(ApiBaseResponse<?> errResponse, Throwable cause) {
		super(errResponse.getMessage(), cause);
		this.errResponse = errResponse;
	}

	public static ApiRuntimeException of (String message, Throwable cause) {
		return new ApiRuntimeException(message, cause);
	}

	public ApiBaseResponse<?> getErrResponse() {
		return errResponse;
	}
}
