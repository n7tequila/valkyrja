/*
 * PROJECT valkyrja2
 * util/AESCoderTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.valkyrja2.util.AESCoder;

import java.security.InvalidKeyException;

class AESCoderTest {

    @Test
    public void testInitKey() throws InvalidKeyException {
        AESCoder coder = new AESCoder("");
    }
}