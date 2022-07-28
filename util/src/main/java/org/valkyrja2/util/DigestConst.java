/*
 * PROJECT valkyrja2
 * util/DigestConst.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

/**
 * 摘要算法常量定义
 *
 * @author Tequila
 * @create 2022/05/20 12:33
 **/
public class DigestConst {

    /** 算法MD5 */
    public static final String ALGORITHM_MD5 = "MD5";

    /** 算法SHA256 */
    public static final String ALGORITHM_SHA256 = "SHA-256";

    /** 算法RSA */
    public static final String ALGORITHM_RSA = "RSA";

    /** 算法SHA256WithRSA */
    public static final String ALGORITHM_SHA256RSA = "SHA256withRSA";

    /** RSA key的头部和尾部标记 */
    public static final String RSA_KEY_HEAD = "-----BEGIN PRIVATE KEY-----";
    public static final String RSA_KEY_TAIL = "-----END PRIVATE KEY-----";

    private DigestConst() {
        throw new IllegalStateException("Const class");
    }
}
