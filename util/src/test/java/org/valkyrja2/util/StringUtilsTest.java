/*
 * PROJECT valkyrja2
 * util/StringUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(StringUtilsTest.class);

    @Test
    void testIdnoMasking() {
        String idno1 = "310103199303061021";
        String idno1Masking = StringUtils.idnoMasking(idno1);
        log.info("idno1: {}, masking: {}", idno1, idno1Masking);
        assertEquals("31010**********021", idno1Masking);

        String idno2 = "310103930306102";
        String idno2Masking = StringUtils.idnoMasking(idno2);
        log.info("idno2: {}, masking: {}", idno2, idno2Masking);
        assertEquals("31010********02", idno2Masking);

        String idno3 = "3101031993030610211234";
        String idno3Masking = StringUtils.idnoMasking(idno3);
        log.info("idno3: {}, masking: {}", idno3, idno3Masking);
        assertEquals("310****************234", idno3Masking);
    }

    @Test
    void testCommonStrMasking() {
        String str1 = "ab";
        String str1Masking = StringUtils.commonStrMasking(str1);
        log.info("str1: {}, masking: {}", str1, str1Masking);
        assertEquals("a*", str1Masking);

        String str2 = "abc";
        String str2Masking = StringUtils.commonStrMasking(str2);
        log.info("str2: {}, masking: {}", str2, str2Masking);
        assertEquals("a**", str2Masking);

        String str3 = "abcd";
        String str3Masking = StringUtils.commonStrMasking(str3);
        log.info("str3: {}, masking: {}", str3, str3Masking);
        assertEquals("a***", str3Masking);

        String str4 = "abcde";
        String str4Masking = StringUtils.commonStrMasking(str4);
        log.info("str4: {}, masking: {}", str4, str4Masking);
        assertEquals("ab*de", str4Masking);

        String str5 = "abcdefg";
        String str5Masking = StringUtils.commonStrMasking(str5);
        log.info("str5: {}, masking: {}", str5, str5Masking);
        assertEquals("ab***fg", str5Masking);

        String str6 = "abcdefghi";
        String str6Masking = StringUtils.commonStrMasking(str6);
        log.info("str6: {}, masking: {}", str6, str6Masking);
        assertEquals("abc***ghi", str6Masking);

        String str7 = "abcdefghi";
        String str7Masking = StringUtils.commonStrMasking(str7, ".");
        log.info("str7: {}, masking: {}", str7, str7Masking);
        assertEquals("abc...ghi", str7Masking);
    }
}