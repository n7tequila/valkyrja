/*
 * PROJECT valkyrja2
 * core/BizExceptionBean.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.exception;

import org.valkyrja2.mvc.RequestContext;

/**
 * 错误Bean对象，用于统一BizException和BizRuntimeException的判断
 *
 * @author Tequila
 * @create 2022/07/03 00:00
 **/
public interface BizExceptionBean {

    String getMessage();

    String getCode();

    int getHttpCode();

    RequestContext getRequestContext();

    Object getData();
}
