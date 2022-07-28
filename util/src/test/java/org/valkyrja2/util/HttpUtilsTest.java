/*
 * PROJECT valkyrja2
 * util/HttpUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilsTest {

    @Test
    void testDoGet() {
        HttpUtils.doGet("http://www.baidu.com");
    }
}