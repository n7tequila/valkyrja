/*
 * PROJECT valkyrja2
 * util/PackageUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PackageUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(PackageUtilsTest.class);

    @Test
    void testGetClassName() throws IOException {
        List<String> pkgs = PackageUtils.getClassName("org.valkyrja2.util");
        log.info("testGetClassName, {}", pkgs);
        assertTrue(pkgs.contains("org.valkyrja2.util.encrypt.AESCoderTest"));
    }

    @Test
    void testGetClassNameNotIncludeSubPackage() throws IOException {
        List<String> pkgs = PackageUtils.getClassName("org.valkyrja2.util", false);
        log.info("testGetClassNameNotIncludeSubPackage, {}", pkgs);
        assertFalse(pkgs.contains("org.valkyrja2.util.encrypt.AESCoderTest"));
    }

    @Test
    void testGetClassNameWithJar() throws IOException {
        List<String> pkgs = PackageUtils.getClassName("org.apache.commons.beanutils", false);
        log.info("testGetClassNameWithJar, {}", pkgs);
    }
}