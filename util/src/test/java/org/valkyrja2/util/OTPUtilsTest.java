/*
 * PROJECT valkyrja2
 * util/OPTUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Tequila
 * @create 2022/07/27 00:59
 **/
class OTPUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(OTPUtilsTest.class);

    @Test
    public void testBuild() {
        for (int i = 0; i < 100; i++) {
            String code = OTPUtils.buildTOTP("1234", 6);
            log.info("{} - {}", DateUtils.formatDate(new Date()), code);

            ThreadUtils.sleep(1000);
        }
    }
}
