/*
 * PROJECT valkyrja2
 * core/DefaultAuditLogger.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 默认使用slf4j的logger对象输出日志
 *
 * @author Tequila
 * @create 2022/07/20 10:26
 **/
public class DefaultAuditLogger implements AuditLogger {

	private final Logger log;
	
	public DefaultAuditLogger() {
		log = LoggerFactory.getLogger(DefaultAuditLogger.class);
	}
	
	public DefaultAuditLogger(String loggerName) {
		log = LoggerFactory.getLogger(loggerName); 
	}
	
	@Override
	public void logger(String level, AuditDescription description) {
		logger(level, description, null);
	}
	
	@Override
	public void logger(String level, AuditDescription description, Exception e) {
		Object[] objs;
		if (e == null) {
			objs = new Object[] { description };
		} else {
			objs = new Object[] { description, e };
		}

		switch (level) {
			case AuditConst.LL_TRACE: log.trace("{}", objs); break;
			case AuditConst.LL_INFO : log.info("{}", objs); break;
			case AuditConst.LL_WARN : log.warn("{}", objs); break;
			case AuditConst.LL_ERROR : log.error("{}", objs); break;
			case AuditConst.LL_DEBUG :
			default: log.debug("{}", objs); break;
		}
	}
}
