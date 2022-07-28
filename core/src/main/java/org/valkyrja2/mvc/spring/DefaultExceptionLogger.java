/*
 * PROJECT valkyrja2
 * core/DefaultExceptionLogger.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.mvc.RequestContextFactory;
import org.valkyrja2.mvc.ResponseCode;

/**
 * 默认的错误日志输出操作对象
 *
 * @author Tequila
 * @create 2022/07/13 23:36
 **/
public class DefaultExceptionLogger implements ExceptionLogger {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultExceptionLogger.class);
	
	@Override
	public void logger(Exception e) {
		/*
		 * 判断是否存在RequestContext对象，有的话则输出requestId
		 */
		String requestId = "";
		if (RequestContextFactory.getContext() != null) {
			requestId = String.format("[%s] ", RequestContextFactory.getContext().getRequestId());
		}
		
		if (e instanceof BizRuntimeException) {
			BizRuntimeException be = (BizRuntimeException) e;
			if (be.isResponseCode(ResponseCode.SIGNATURE_ERROR)) {
				log.error("{} {}", requestId, e.getMessage());
				return;
			}
		}
		log.error("{} {}", requestId, e.getMessage(), e);
	}
}
