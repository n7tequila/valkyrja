/*
 * PROJECT valkyrja2
 * core/SysConfig.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.config;

/**
 * @author Tequila
 * @create 2022/07/19 14:01
 **/
public class SysConfig extends Configuration {

    private boolean debugMode;

    /** 跳过api签名检查标志 */
    private boolean skipApiSignCheck;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isSkipApiSignCheck() {
        return skipApiSignCheck;
    }

    public void setSkipApiSignCheck(boolean skipApiSignCheck) {
        this.skipApiSignCheck = skipApiSignCheck;
    }
}
