/*
 * PROJECT valkyrja2
 * util/OTPUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTP;

import java.time.Duration;

/**
 * One-time password 工具包
 *
 * @author Tequila
 * @create 2022/07/27 01:04
 **/
public class OTPUtils {

    public static final int DEFAULT_PERIOD = 60;

    /**
     * 生成一个TOTP的秘钥
     *
     * @param key           key
     * @param length        长度
     * @param hmacAlgorithm hmac算法, SH1, SH256, SHA512
     * @param period        周期
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 01:11
     */
    public static String buildTOTP(String key, int length, HMACAlgorithm hmacAlgorithm, int period) {
        TOTP.Builder builder = new TOTP.Builder(key.getBytes());
        builder
                .withPasswordLength(length)
                .withAlgorithm(hmacAlgorithm) // SHA256 and SHA512 are also supported
                .withPeriod(Duration.ofSeconds(period));
        TOTP totp = builder.build();
        return totp.now();
    }

    /**
     * 构建你觉得
     *
     * @param key    key
     * @param length 长度
     * @return {@link String }
     * @author Tequila
     * @date 2022/07/27 01:13
     */
    public static String buildTOTP(String key, int length) {
        return buildTOTP(key, length, HMACAlgorithm.SHA1, DEFAULT_PERIOD);
    }
}
