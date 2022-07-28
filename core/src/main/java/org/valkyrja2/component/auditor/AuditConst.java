/*
 * PROJECT valkyrja2
 * core/AuditConst.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

/**
 * @author Tequila
 * @create 2022/07/20 10:26
 **/
public class AuditConst {

    /** LoggerLevel常量定义 */
    public static final String LL_TRACE = "TRACE";
    public static final String LL_DEBUG = "DEBUG";
    public static final String LL_INFO  = "INFO";
    public static final String LL_WARN  = "WARN";
    public static final String LL_ERROR = "ERROR";


    private AuditConst() {
        throw new IllegalStateException("Const class");
    }
}
