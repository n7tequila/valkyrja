/*
 * PROJECT valkyrja2
 * util/NetworkUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.valkyrja2.test.TestUtils;

import javax.print.attribute.standard.RequestingUserName;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Tequila
 * @create 2022/06/29 10:13
 **/
class NetworkUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(NetworkUtilsTest.class);

    String url = "https://www.example.com/project/service/test/func?param1=value1&param";
    MockHttpServletRequest getRequest = TestUtils.newHttpRequestGet(url, "/project", null);

    @Test
    void testGetRequestSchema() {
        assertEquals("https", NetworkUtils.getRequestSchema(getRequest));
    }

    @Test
    void testGetRequestHost() {
        String host = NetworkUtils.getRequestHost(getRequest);
        log.info("GetRequestHost: {}", host);
        assertEquals("https://www.example.com", host);
    }

    @Test
    void testGetRequestContext() {
        String context = NetworkUtils.getRequestContext(getRequest);
        log.info("GetRequestContext: {}", context);
        assertEquals("https://www.example.com/project", context);
    }

    @Test
    void testGetRequestUrl() {
        String url;

        url = NetworkUtils.getRequestUrl(getRequest, false);
        log.info("GetRequestUrl: {}", url);
        assertEquals("https://www.example.com/project/service/test/func", url);

        url = NetworkUtils.getRequestUrl(getRequest, true);
        log.info("GetRequestUrl: {}", url);
        assertEquals("https://www.example.com/project/service/test/func?param1=value1&param", url);
    }

    @Test
    void testGetRequestUrlQuery() {
        String urlQuery = NetworkUtils.getRequestUrlQuery(getRequest);
        log.info("GetRequestUrlQuery: {}", urlQuery);
        assertEquals("https://www.example.com/project/service/test/func?param1=value1&param", urlQuery);
    }

    @Test
    void testGetRequestQuery() {
        String query = NetworkUtils.getRequestQuery(getRequest);
        log.info("GetRequestQuery: {}", query);
        assertEquals("param1=value1&param", query);
    }

    @Test
    void testGetRequestQueryMarkQ() {
        String query = NetworkUtils.getRequestQuery(getRequest, true);
        log.info("GetRequestUrlQuery: {}", query);
        assertEquals("?param1=value1&param", query);
    }

    @Test
    void testGetRequestPath() {
        String path = NetworkUtils.getRequestPath(getRequest);
        log.info("GetRequestPath: {}", path);
        assertEquals("/project/service/test/func", path);
    }

    @Test
    void testGetRequestPathQuery() {
        String pathQuery = NetworkUtils.getRequestPathQuery(getRequest);
        log.info("GetRequestPathQuery: {}", pathQuery);
        assertEquals("/project/service/test/func?param1=value1&param", pathQuery);
    }

    @Test
    void testIsIPAddress() {
        String val;

        val = "255.255.255.255";
        assertTrue(NetworkUtils.isIPAddress(val));

        val = "255.255.255.256";
        assertFalse(NetworkUtils.isIPAddress(val));

        val = "4294967295";
        assertTrue(NetworkUtils.isIPAddress(val));
    }

    @Test
    void testUlong2ip() {
        long val;
        val = 4294967295L;
        assertEquals("255.255.255.255", NetworkUtils.ulong2ip(val));

        val = -1L;
        assertEquals("255.255.255.255", NetworkUtils.ulong2ip(val));
    }

    @Test
    void testIP2ulong() throws UnknownHostException {
        String ip = "255.255.255.255";
        assertEquals(4294967295L, NetworkUtils.ip2ulong(ip));
//        long val = 4294967295L;
//        long v2 = Integer.toUnsignedLong(-1);
//        System.out.println(v2);

    }
}
