/*
 * PROJECT valkyrja2
 * util/BeanUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.TestObject.People;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * @author Tequila
 * @create 2022/06/27 17:26
 **/
class BeanUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(BeanUtilsTest.class);

    private People people = new People("1", "张三");

    @Test
    void testCopyProperties() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        BeanUtils.copyProperties(new People(), people, true);
    }
}
