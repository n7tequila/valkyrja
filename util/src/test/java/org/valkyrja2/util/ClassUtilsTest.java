/*
 * PROJECT valkyrja2
 * util/ClassUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.TestObject.Book;
import org.valkyrja2.util.TestObject.HarryPotter;
import org.valkyrja2.util.TestObject.People;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class ClassUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(ClassUtilsTest.class);

    @Test
    void testIsAssignable() {
        assertTrue(ClassUtils.isAssignable(HashMap.class, Map.class));
    }

    @Test
    void testGetSuperClassGenericType() {
        Class<?> klass = ClassUtils.getSuperClassGenericType(HarryPotter.class, People.class);
        log.info(String.valueOf(klass));
//        List<Type> types = new ArrayList<>();
//        Class<?> klass = HarryPotter.class;
//        do {
//            Type genType = klass.getGenericSuperclass();
//            types.add(genType);
//            klass = klass.getSuperclass();
//        } while(!Object.class.equals(klass));
//
//        log.info("end, {}", types);

//        Type genType = baseClass.getGenericSuperclass();
//        log.info(genType.toString());
//        while (true) {
//            genType = baseClass.getSuperclass().getGenericSuperclass();
//            log.info(genType.toString());
//        }
    }
}