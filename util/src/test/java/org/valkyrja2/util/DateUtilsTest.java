/*
 * PROJECT valkyrja2
 * util/DateUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(DateUtilsTest.class);

    @Test
    void testSecond2timeline() {
        double d;
        String s;

        d = 60d;
        s = DateUtils.second2timeline(d);
        assertEquals("00:01:00", s);

        d = 60.123d;
        s = DateUtils.second2timeline(d);
        assertEquals("00:01:00", s);
    }

    @Test
    void testSecond2timeline2Param() {
        double d;
        String s;

        d = 60d;
        s = DateUtils.second2timeline(d, true);
        assertEquals("00:01:00.0", s);

        d = 60.123d;
        s = DateUtils.second2timeline(d, true);
        assertEquals("00:01:00.123", s);
    }
}