/*
 * PROJECT valkyrja2
 * util/TestObject.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.io.Writer;

/**
 * @author Tequila
 * @create 2022/06/27 17:28
 **/
public class TestObject {

    @JsonFilterGroup({
            @JsonFilterEx(properties = {"idno"}, type = People.class),
            @JsonFilterEx(properties = {"id"}, type = Book.class)
    })
    public static class People extends AbstractJsonObject<People> {
        private String id;

        private String name;

        private int age;

        private String sex;

        private String idno;

        private Book<String, Float> book;

        public People() {
        }

        public People(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public People(String id, String name, int age, String sex, String idno) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.idno = idno;
        }

        public People(String id, String name, int age, String sex, String idno, Book<String, Float> book) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.idno = idno;
            this.book = book;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getIdno() {
            return idno;
        }

        public void setIdno(String idno) {
            this.idno = idno;
        }

        public Book<String, Float> getBook() {
            return book;
        }

        public void setBook(Book<String, Float> book) {
            this.book = book;
        }
    }

    public static class Book<N, P> extends AbstractJsonObject<String> {
        private String id;

        private N name;

        private String isbn;

        private P price;

        public Book() {
        }

        public Book(String id, N name, String isbn, P price) {
            this.id = id;
            this.name = name;
            this.isbn = isbn;
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public N getName() {
            return name;
        }

        public void setName(N name) {
            this.name = name;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public P getPrice() {
            return price;
        }

        public void setPrice(P price) {
            this.price = price;
        }
    }

    public static class StoryBook<W extends People> extends Book<String, Float> {

        private W writer;

        public StoryBook() {
        }

        public StoryBook(String id, String name, String isbn, float price) {
            super(id, name, isbn, price);
        }

        public W getWriter() {
            return writer;
        }

        public void setWriter(W writer) {
            this.writer = writer;
        }
    }

    public static class HarryPotter extends StoryBook<Writer> {

        public HarryPotter() {
        }

        public HarryPotter(String id, String name, String isbn, float price) {
            super(id, name, isbn, price);
        }
    }

    public static class Writer extends People {

        public Writer() {
        }

        public Writer(String id, String name) {
            super(id, name);
        }

        public Writer(String id, String name, int age, String sex, String idno) {
            super(id, name, age, sex, idno);
        }

        public Writer(String id, String name, int age, String sex, String idno, Book<String, Float> book) {
            super(id, name, age, sex, idno, book);
        }
    }

}
