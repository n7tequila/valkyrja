/*
 * PROJECT valkyrja2
 * core/ResponseCode.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import org.valkyrja2.util.HttpStatus;

/**
 * 响应代码
 *
 * @author Tequila
 * @create 2022/07/01 22:41
 **/
public enum ResponseCode {
	/* == 标准成功响应码 == */
	/**
	 * 正常成功代码
	 */
	SUCCESS("0000", "成功", HttpStatus.OK),
	
	/* 0001 - 0999 系统级别内部错误 */
	/* 幂等操作相关系统错误 */
	/** 客户端提交了幂等令牌，方法不支持幂等操作时的返回值 */
	IDEMPOTENT_NOT_SUPPORT("0100", "找不到幂等令牌，请重新提交数据", HttpStatus.NOT_ACCEPTABLE),
	/** 相同的幂等令牌已经在操作中，返回操作成功状态 */
	IDEMPOTENT_EXECUTING("0101", "操作正在进行中", HttpStatus.OK),
	/** 相同的幂等令牌已经在操作中，返回错误状态 */
	IDEMPOTENT_DUPLICATE_TOKEN("0102", "重复操作调用", HttpStatus.CONFLICT),
	/** 服务端幂等操作时，客户端未传输幂等令牌操作 */
	IDEMPOTENT_ILLEGAL_OPERATE("0103", "未授权操作", HttpStatus.NOT_ACCEPTABLE),
	/* 其他系统内部返回值 */
	ASYNC_EXECUTING("0150", "数据正在生成中，请稍后访问", HttpStatus.OK),
	
	/* 其他错误 */
	SQL_EXCEPTION("0200", "SQL Error", HttpStatus.INTERNAL_SERVER_ERROR),
	RESOURCE_FILE_NOT_FOUND("0201", "资源文件找不到", HttpStatus.NOT_FOUND),
	RESOURCE_FILE_BAD("0202", "资源文件无法读取或发生错误", HttpStatus.FORBIDDEN),
	RESOURCE_FILE_CAN_NOT_ACCESS("0203", "资源文件无法读取或发生错误", HttpStatus.NOT_ACCEPTABLE),
	
	
	/* 1000-1999 调用方（用户）引起错误时的响应码 */
	/* 界面上的错误从1000-1499保留，所有界面错误的返回值都是400 */
	/** 用户输入错误 */
	VALIDATE_ERROR("1000", "输入错误", HttpStatus.BAD_REQUEST),
	DUPLICATE_KEY_VALUE("1000", "重复数据", HttpStatus.BAD_REQUEST),
	CONFIRM_PASSWORD_WRONG("1000", "两次输入密码不一致", HttpStatus.BAD_REQUEST),
	OLD_PASSWORD_WRONG("1001", "旧密码错误", HttpStatus.BAD_REQUEST),
	SMS_CODE_FAIL("1002", "短信验证码错误", HttpStatus.NOT_ACCEPTABLE),
	VERIFY_CODE_FAIL("1002", "验证码错误", HttpStatus.NOT_ACCEPTABLE),
	REQUEST_OVERTIME("1003", "请求超时", HttpStatus.BAD_REQUEST),
	ORDER_NOT_PAY("1100", "订单尚未支付", HttpStatus.BAD_REQUEST),
	ORDER_PAID_ERROR("1101", "订单已支付", HttpStatus.ACCEPTED),
	ORDER_NOT_COMPLETE("1102", "订单未完成", HttpStatus.NOT_ACCEPTABLE),
	// NOT_FOUND_USER_BY_MOBILE("1005", "手机号码未注册，请确认后重试", HttpStatus.BAD_REQUEST),
	
	/* API保留错误码 */
	/** API接口调用是 */
	CONFIG_FAIL("1495", "配置错误", HttpStatus.INTERNAL_SERVER_ERROR),
	DATA_NOT_FOUND("1496", "数据没有找到", HttpStatus.NOT_FOUND),
	DUPLICATE_DATA("1497", "重复数据调用", HttpStatus.CONFLICT),
	NOT_ALLOW("1498", "不允许访问该方法", HttpStatus.METHOD_NOT_ALLOWED),  
	UNAUTH_ACCESS("1498", "未授权访问", HttpStatus.FORBIDDEN),
	SIGNATURE_ERROR("1499", "数据签名错误", HttpStatus.BAD_REQUEST),
	DNA_VERIFY_ERROR("1500", "存证数据没有找到或校验失败", HttpStatus.NOT_ACCEPTABLE),
	BLOCKCHAIN_VERIFY_ERROR("1501", "存证数据区块链校验失败", HttpStatus.NOT_ACCEPTABLE),
	DUPLICATE_APPLY("1502", "重复申请", HttpStatus.CONFLICT),
	APPLY_OVER_TIME("1503", "超出申请时效", HttpStatus.NOT_ACCEPTABLE),
	TOKEN_EXPIRED("1504", "令牌过期，请重新登录", HttpStatus.FORBIDDEN),
	DNA_PARSE_ERROR("1505", "DNA存证数据格式错误", HttpStatus.NOT_ACCEPTABLE),
	DNA_PARSE_FILELENGTH_ERROR("1505.1", "DNA存证数据格式错误", HttpStatus.NOT_ACCEPTABLE),
	DNA_PARSE_TIME_ERROR("1505.2", "DNA存证数据格式错误", HttpStatus.NOT_ACCEPTABLE),
	DNA_PARSE_VERSION_ERROR("1505.3", "DNA存证数据格式错误", HttpStatus.NOT_ACCEPTABLE),
	DNA_SDK_ERROR("1506", "DNA SDK错误", HttpStatus.NOT_ACCEPTABLE),
	APPLYING("1507", "申请正在处理中", HttpStatus.CREATED),
	
	/* 用户登录所有相关返回代码，从1900开始 */
	/** 如果用户没有登录成功，被Security模块拦截或者其他安全判断给拦截后的返回值 */
	NOT_LOGIN("1900", "用户未登录", HttpStatus.UNAUTHORIZED),
	/** 用户session超时错误 */
	SESSION_TIMEOUT("1901", "登录过期，请重新登录", HttpStatus.UNAUTHORIZED),
	/** 用户没有权限访问资源 */
	PERMISSION_DENIED("1902", "用户权限不足", HttpStatus.FORBIDDEN),
	/** 用户没有权限访问资源 */
	PARAMETER_ERROR("1903", "请求格式正确，但是由于含有语义错误，无法处理", HttpStatus.UNPROCESSABLE_ENTITY),
	/** 用户已登录 */
	ALREADY_LOGON("1904", "用户已登录", HttpStatus.ACCEPTED),

	// 2000-2999 服务端（本系统）发生错误时的响应码
	LOGIN_FAILED("2000", "用户名或密码错误", HttpStatus.NOT_ACCEPTABLE),
	SAVE_FAIL("2001", "保存失败", HttpStatus.NOT_ACCEPTABLE),
	SYNC_FAIL("2002", "数据同步失败", HttpStatus.NOT_ACCEPTABLE),
	ILLEGAL_STATE("2003", "业务状态错误", HttpStatus.NOT_ACCEPTABLE),
	OPERATE_FAIL("2004", "操作失败", HttpStatus.NOT_ACCEPTABLE),
	OBJECT_NOT_FOUND("2005", "操作对象未找到", HttpStatus.NOT_ACCEPTABLE),
	OBJECT_NULL("2006", "操作对象为空", HttpStatus.NOT_ACCEPTABLE),
	REMEMBER_ME_FAIL("2007", "暂存密码发生错误，请手动输入密码重新登录系统", HttpStatus.NOT_ACCEPTABLE),
	UPLOAD_ERROR("2008", "上传文件错误，请重新上传文件", HttpStatus.NOT_ACCEPTABLE),
//	UPLOAD_SESSION_LOST("2008.2", "上传文件缓存丢失，请重新上传文件", HttpStatus.NOT_ACCEPTABLE),
	DOCUMENT_BUILD_ERROR("2009", "文档生成错误", HttpStatus.NOT_ACCEPTABLE),
	NOT_SUPPORT("2010", "不支持该方法", HttpStatus.NOT_ACCEPTABLE),
	PACKAGE_ERROR("2020", "打包文件出错", HttpStatus.NOT_ACCEPTABLE),
	TEMPLATE_READ_ERROR("2030", "模板读取错误", HttpStatus.NOT_ACCEPTABLE),
	PDF_CREATE_ERROR("2040", "PDF生成错误", HttpStatus.NOT_ACCEPTABLE),
	RESOURCE_URL_GET_ERROR("2050", "资源文件下载错误", HttpStatus.NOT_ACCEPTABLE),
	MESSAGE_QUEUE_JOB_PUBLISH_ERROR("2060", "队列任务无法发布", HttpStatus.NOT_ACCEPTABLE),
	
	MISS_SYSTEM_CONFIG_ERROR("2998", "未知系统配置错误", HttpStatus.INTERNAL_SERVER_ERROR),
	RUNTIME_EXCEPTION("2999", "未知错误", HttpStatus.INTERNAL_SERVER_ERROR),

	// 3000-3999 上游服务端（如OSS、上游接口）发生错误时的响应码
	/* 3000-3299 Aliyun 报错 */
	ALIYUN_ERROR("3000", "Aliyun接口调用错误", HttpStatus.NOT_ACCEPTABLE),
	ALIYUN_OSS_ERROR("3001", "OSS接口调用错误", HttpStatus.NOT_ACCEPTABLE),
	SMS_SEND_ERROR("3101", "短信发送错误", HttpStatus.NOT_ACCEPTABLE),
	ZM_CHAIN_ERROR("3299","芝麻存证错误", HttpStatus.NOT_ACCEPTABLE),
	PAY_SDK_ERROR("3399","支付SDK调用错误", HttpStatus.NOT_ACCEPTABLE),
	SDK_ERROR("3400", "SDK调用错误", HttpStatus.NOT_ACCEPTABLE),
	/* 3300-3499 腾通云 报错 */
	/* 3500 签约平台报错 */
	SIGN_PLATFORM_NOTFOUND("3500", "无法注册签约系统", HttpStatus.NOT_ACCEPTABLE),
	SIGN_REGISTRY_ERROR("3501", "无法注册签约系统", HttpStatus.NOT_ACCEPTABLE),
	SIGN_DOC_ERROR("3502", "签约系统报错", HttpStatus.NOT_ACCEPTABLE),
	/* 3600 实名认证平台报错 */
	WANT_CERT("3600", "请先实名认证，在进行数据提交", HttpStatus.NOT_ACCEPTABLE),
	CERT_FAIL("3601", "实名认证错误，请检查您输入的认证数据", HttpStatus.NOT_ACCEPTABLE),
	CERT_BIO_FAIL("3602", "活体认证错误，请重新进行检验", HttpStatus.NOT_ACCEPTABLE),
	CERT_FACE_FAIL("3603", "人脸识别错误，请重新进行检验", HttpStatus.NOT_ACCEPTABLE),
	ALREADY_CERT("3699", "已实名认证", HttpStatus.ACCEPTED),
	/* 3700 区块链平台报错 */
	BLOCKCHAIN_INIT_FAIL("3700", "区块链平台初始化错误", HttpStatus.INTERNAL_SERVER_ERROR),
	BLOCKCHAIN_CALL_FAIL("3701", "区块链平台调用错误", HttpStatus.INTERNAL_SERVER_ERROR),
	
	/* 3800 远程设备报错 */
	REMOTE_DEVICE_NOT_FOUND("3800", "远程设备没有找到", HttpStatus.NOT_ACCEPTABLE),
	REMOTE_DEVICE_CALL_ERROR("3801", "远程设备调用错误", HttpStatus.NOT_ACCEPTABLE),
	
	/* 3900 微信小程序报错 */
	WX_SDK_ERROR("3900", "远程设备调用错误", HttpStatus.NOT_ACCEPTABLE),
	
	/* 系统未知错误 */
	SYSTEM_UNKNOWN_ERROR("9999", "系统未知错误，请与管理员联系", HttpStatus.INTERNAL_SERVER_ERROR);
	
	private final String code;

	private final String message;
	
	private final HttpStatus httpStatus;


	private ResponseCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String code() {
		return this.code;
	}
	
	public String message() {
		return this.message;
	}
	
	public HttpStatus httpStatus() {
		return this.httpStatus;
	}
	
	public int httpCode() {
		return this.httpStatus.value();
	}
	
}
