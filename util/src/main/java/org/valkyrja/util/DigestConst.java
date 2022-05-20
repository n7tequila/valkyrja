package org.valkyrja.util;

/**
 * 摘要算法常量定义
 *
 * @author Tequila
 * @create 2022/05/20 12:33
 **/
public class DigestConst {

    /** md5算法 */
    public static final String ALGORITHM_MD5 = "MD5";

    /** 算法sha256 */
    public static final String ALGORITHM_SHA256 = "SHA-256";

    private DigestConst() {
        throw new IllegalStateException("Const class");
    }
}
