/*
 * PROJECT valkyrja2
 * core/SecurityConst.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.security;

/**
 * 安全模块常量定义
 *
 * @author Tequila
 * @create 2022/07/04 23:55
 **/
public class SecurityConst {

    private SecurityConst() {
        throw new IllegalStateException("Const class");
    }

    public static final String HTTP_AUTH_TOKEN = "x-auth-token";

    public static final String HTTP_API_TOKEN  = "x-aip-token";
}
