/*
 * PROJECT valkyrja2
 * util/SystemSettingTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Tequila
 * @create 2022/06/28 15:41
 **/
class SystemSettingTest {
    private static final Logger log = LoggerFactory.getLogger(SystemSettingTest.class);

    @Test
    void testLoadSetting() throws IOException {
        SystemSetting.getInstance().load();
        assertTrue(SystemSetting.getInstance().isDebugMode());
        assertTrue(SystemSetting.getInstance().isSwitchEnabled("include_value", false));
    }

}
