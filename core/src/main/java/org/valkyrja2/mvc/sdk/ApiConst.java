/*
 * PROJECT valkyrja2
 * core/ApiConst.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc.sdk;

/**
 * api 常量
 *
 * @author Tequila
 * @create 2022/07/27 16:06
 **/
public class ApiConst {

    public static final String MSG_PROP_FILE_NOT_FOUND = "Api client properties file is not found.";

    public static final String MSG_PROP_FILE_LOAD_ERROR = "Api client properties file load error.";

    public static final String MSG_CALL_API_ERROR = "Call api `%s` raise error.";


    public static final String PROP_API_HOST = "apiHost";
    public static final String PROP_APP_ID = "appId";
    public static final String PROP_API_KEY = "apiKey";
    public static final String PROP_API_SECRET = "apiSecret";
    public static final String PROP_AES_KEY = "aesKey";

    public static final String LOG_FORMAT = "========== %s - %s: %s";

    public static final String S_INPUT  = "入参";
    public static final String S_OUTPUT = "出参";
    public static final String S_ERROR  = "错误";

    private ApiConst() {
        throw new IllegalStateException("Const class");
    }
}
