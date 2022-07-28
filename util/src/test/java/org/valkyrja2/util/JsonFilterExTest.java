/*
 * PROJECT valkyrja2
 * util/JsonFilterExTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.TestObject.Book;
import org.valkyrja2.util.TestObject.People;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Tequila
 * @create 2022/06/25 00:24
 **/
class JsonFilterExTest {
    private static final Logger log = LoggerFactory.getLogger(JsonFilterExTest.class);

    private People people = new People("1", "张三", 20, "男", "idno",
            new Book("1", "冰与火之歌", "ISBN-18481-2939-11", 200f));

    private JsonFilterExSerialization jfex = new JsonFilterExSerialization(people);

    @Test
    void testObj2Json() throws IOException {
        String json = jfex.writeAsString();
        log.info(json);
        assertNotNull(json);
    }

    @Test
    void testObjWriteJson() throws IOException {
        jfex.createJsonGenerator(System.out, StandardCharsets.UTF_8).writeObject(people);
        jfex.write();
    }

    @Test
    void testClassMode() throws IOException {
        JsonFilterExSerialization jfex = new JsonFilterExSerialization(People.class);
        String json = jfex.writeAsString(people);
        log.info(json);
        assertNotNull(json);
    }
}
