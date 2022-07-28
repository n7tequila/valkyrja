/*
 * PROJECT valkyrja2
 * util/JsonObjectTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.TestObject.Book;
import org.valkyrja2.util.TestObject.People;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Tequila
 * @create 2022/06/27 16:49
 **/
class JsonObjectTest {
    private static final Logger log = LoggerFactory.getLogger(JsonObjectTest.class);

    private People people = new People("1", "张三", 20, null, "idno",
            new Book("1", "冰与火之歌", "ISBN-18481-2939-11", 200f));

    private String source = "{\"id\":\"1\",\"name\":\"张三\"}";

    @Test
    void testToString() {
        String json;
        json = people.toString();
        log.info("toString(), {}", json);

        json = people.toJson();
        log.info("toJson(), {}", json);

        assertNotNull(json);
    }

    @Test
    void testFromJson() throws IOException {
        People p1 = People.fromJson(source, People.class);
        assertEquals(people.getId(), p1.getId());
        assertEquals(people.getName(), p1.getName());

        People p2 = people.fromJson(source);
        assertEquals(people.getId(), p2.getId());
        assertEquals(people.getName(), p2.getName());
    }

    @Test
    void copyProperties() throws InvocationTargetException, IllegalAccessException {
        People dest = new People();
        dest.setSex("女");
        BeanUtils.copyProperties(dest, people, false, "id", "name");
        log.info(dest.toJson());
    }
}
