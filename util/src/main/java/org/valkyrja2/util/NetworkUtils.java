/*
 * PROJECT valkyrja2
 * util/NetworkUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

/**
 * 网络工具包
 *
 * @author Tequila
 * @create 2022/06/28 22:39
 **/
public class NetworkUtils {
	private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

	private static final String X_FORWARDED_FOR = "x-forwarded-for";
	
	private static final String X_FORWARDED_PROTO = "x-forwarded-proto";
	
	private NetworkUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 获取HttpServletRequest中的远程访问IP地址<br>
	 * 如果request的header中有x-forwarded-for标记，则使用x-forwarded-for标记的ip地址
	 *
	 * @param httpRequest http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:44
	 */
	public static String getRemoteIPAddress(HttpServletRequest httpRequest) {
		if (httpRequest.getHeader(X_FORWARDED_FOR) != null) {
			return httpRequest.getHeader(X_FORWARDED_FOR);
		} else {
			return httpRequest.getRemoteAddr();
		}
	}

	/**
	 * 返回完整的server host信息<br>
	 * 格式：HostsName<HostsIP:HostPort>
	 *
	 * @return {@link String } 完整的host信息
	 * @author Tequila
	 * @date 2022/06/28 22:45
	 */
	public static String getFullHostId() {
		return String.format("%s<%s:%d>", getHostName(), getHostIP(), getHostPort());
	}

	/**
	 * 通过HttpServletRequest得到完整主机host信息
	 *
	 * @param httpRequest http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:46
	 */
	public static String getFullHostId(HttpServletRequest httpRequest) {
		return String.format("%s<%s:%d>", getHostName(), getHostIP(), httpRequest.getServerPort());
	}

	/**
	 * 获取本地计算机名
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:47
	 */
	public static String getHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();  //获得本机名称
		} catch (UnknownHostException e) {
			log.warn("Can not get host name.", e);
			return "UNKNOWN_HOST";
		}
	}

	/**
	 * 获取本地服务器的ip地址
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:48
	 */
	public static String getHostIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();  // 本机IP地址
		} catch (UnknownHostException e) {
			log.warn("Can not get host ip.", e);
			return "UNKNOWN_IPADDRESS";
		}
    }


	/**
	 * 获取本地服务器的http端口
	 *
	 * @return int
	 * @author Tequila
	 * @date 2022/06/29 00:08
	 */
	public static int getHostPort() {
		try {
			MBeanServer server = null;
			if (!MBeanServerFactory.findMBeanServer(null).isEmpty()) {
				server = MBeanServerFactory.findMBeanServer(null).get(0);
			}
			if (server != null) {
				Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);

				Iterator<ObjectName> iterator = names.iterator();
				ObjectName name = null;
				while (iterator.hasNext()) {
					name = iterator.next();

					String protocol = server.getAttribute(name, "protocol").toString();
					if (protocol.startsWith("HTTP")) {
						return (Integer) server.getAttribute(name, "port");
					}
				}
			}
		} catch (Exception e) { /* ignore */ }
		return -1;
	}

	/**
	 * 通过HttpServletRequest，获取请求的schema，例如：http、https<br>
	 * 如果request的header中有x-forwarded-proto标记，则使用x-forwarded-proto标记的schema
	 *
	 * @param request http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 01:18
	 */
	public static String getRequestSchema(HttpServletRequest request) {
		if (request.getHeader(X_FORWARDED_PROTO) != null) {
			return request.getHeader(X_FORWARDED_PROTO);
		} else {
			return request.getScheme();
		}
	}

	/**
	 * 通过HttpServletRequest，获取请求的host信息<br>
	 * 例如：http://localhost:8080
	 *
	 * @param request http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 01:24
	 */
	public static String getRequestHost(HttpServletRequest request) {
		return String.format("%s://%s%s",
				getRequestSchema(request),
				request.getServerName(),
				80 == request.getServerPort() ? "" : (":" + request.getServerPort()));
	}

	/**
	 * 通过HttpServletRequest，获取请求所对应的部署的项目信息<br>
	 * 例如，从http请求中获取的完整的url为 http://localhost:8080/project/service/function，
	 * 如果Context是/project，则返回值为http://localhost:8080/project
	 *
	 * @param request http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:51
	 */
	public static String getRequestContext(HttpServletRequest request) {
		return String.format("%s%s",
				getRequestHost(request),
				request.getContextPath());
	}

	/**
	 * 通过HttpServletRequest，获取请求的完整url
	 *
	 * @param request http请求
	 * @param query   是否包含QueryString
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 00:22
	 */
	public static String getRequestUrl(HttpServletRequest request, boolean query) {
		return String.format("%s%s%s",
				getRequestHost(request),
				request.getRequestURI(),
				query ? getRequestQuery(request, true) : "");
	}

	/**
	 * 通过HttpServletRequest，获取请求的完整url，默认包含QueryString
	 *
	 * @param request 请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 00:38
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		return getRequestUrl(request, true);
	}

	/**
	 * 通过HttpServletRequest获取完整的访问url，默认包含QueryString
	 *
	 * @param request http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 22:50
	 */
	public static String getRequestUrlQuery(HttpServletRequest request) {
		return getRequestUrl(request, true);
	}

	/**
	 * 通过HttpServletRequest获取请求中的QueryString
	 *
	 * @param request 请求
	 * @param markQ   添加问号
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 09:50
	 */
	public static String getRequestQuery(HttpServletRequest request, boolean markQ) {
		if (StringUtils.isBlank(request.getQueryString())) {
			return "";
		} else {
			return markQ ? "?" + request.getQueryString() : request.getQueryString();
		}
	}

	/**
	 * 通过HttpServletRequest获取请求中的QueryString信息，不包含?标记
	 *
	 * @param request 请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 09:47
	 */
	public static String getRequestQuery(HttpServletRequest request) {
		return getRequestQuery(request, false);
	}

	/**
	 * 通过HttpServletRequest获取请求路径（不含host信息）<br>
	 * 例如，从http请求中获取的完整的url为 http://localhost:8080/project/service/function
	 * 则返回值为/project/service/function
	 *
	 * @param request 请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 01:35
	 */
	public static String getRequestPath(HttpServletRequest request) {
		return request.getRequestURI();
	}

	/**
	 * 通过HttpServletRequest生成请求完整路径（不含host信息）
	 * 例如，从http请求中获取的完整的url为 http://localhost:8080/project/service/function?param1=1
	 * 则返回值为/project/service/function?param1=1
	 * @param request 请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 01:38
	 */
	public static String getRequestPathQuery(HttpServletRequest request) {
		return request.getRequestURI() + getRequestQuery(request, true);
	}

	/**
	 * 从HttpServletRequest对象中获取body数据
	 *
	 * @param request http请求
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/29 00:05
	 */
	public static String getHttpRequestBody(HttpServletRequest request) throws IOException {
    	BufferedReader br = request.getReader();
		StringBuilder body = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null){
			body.append(line);
		}
		return body.toString();
    }

	/**
	 * 根据名字获取Cookie对象
	 *
	 * @param name        cookie名字
	 * @param httpRequest http请求
	 * @return {@link Cookie }
	 * @author Tequila
	 * @date 2022/07/01 17:05
	 */
	public static Cookie getCookieByName(String name, HttpServletRequest httpRequest) {
		for (Cookie cookie: httpRequest.getCookies()) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * 根据名字获取Cookie的值
	 *
	 * @param name        cookie名字
	 * @param httpRequest http请求
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 17:06
	 */
	protected String getCookieValueByName(String name, HttpServletRequest httpRequest) {
		Cookie cookie = getCookieByName(name, httpRequest);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	/**
	 * 添加cookie值
	 *
	 * @param key          key
	 * @param value        值
	 * @param expire       到期
	 * @param path         路径
	 * @param httpResponse http响应
	 * @author Tequila
	 * @date 2022/07/01 17:06
	 */
	public static void addCookie(String key, String value, int expire, String path, HttpServletResponse httpResponse) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(expire);
		cookie.setPath(path);
		httpResponse.addCookie(cookie);
	}


	/**
	 * 删除cookie
	 *
	 * @param key          key
	 * @param httpRequest  http请求
	 * @param httpResponse http响应
	 * @author Tequila
	 * @date 2022/07/01 17:36
	 */
	public static void removeCookie(String key, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Cookie[] cookies = httpRequest.getCookies();
		if (null != cookies) {
			for (Cookie cookie: cookies) {
				if (key.equals(cookie.getName())) {
					cookie.setValue(null);
					cookie.setMaxAge(0); // 立即销毁cookie
					cookie.setPath("/");
					httpResponse.addCookie(cookie);
				}
			}
		}
	}

	/**
	 * 判断字符串是否是url地址
	 *
	 * @param s 需要判断字符串
	 * @return boolean true 字符串是一个合法的url地址，否则返回false
	 * @author Tequila
	 * @date 2022/06/22 10:28
	 */
	public static boolean isValidUrl(String s) {
		URI uri;
		try {
			uri = new URI(s);
		} catch (URISyntaxException e) {
			return false;
		}

		if(uri.getHost() == null){
			return false;
		}
		return HttpSchema.HTTP.name().equalsIgnoreCase(uri.getScheme()) || HttpSchema.HTTPS.name().equalsIgnoreCase(uri.getScheme());
	}

	/**
	 * 判断字符串是否是ip地址
	 *
	 * @param val 值
	 * @author Tequila
	 * @date 2022/07/15 09:58
	 */
	public static boolean isIPAddress(String val) {
		try {
			InetAddress ip = InetAddress.getByName(val);

			return ip instanceof Inet4Address || ip instanceof Inet6Address;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	/**
	 * 将unsigned long型ip地址转换成标准型ip地址
	 *
	 * @param ulong unsigned long IP
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/15 10:20
	 */
	public static String ulong2ip(long ulong) {
		if (ulong < 0) throw new IllegalArgumentException("val must be unsigned long value");

		try {
			InetAddress ip = InetAddress.getByName(String.valueOf(ulong));

			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * 将标准型ip地址转换成将unsigned long型
	 *
	 * @param ip ip地址
	 * @return {@link Long }
	 * @author Tequila
	 * @date 2022/07/15 11:21
	 */
	public static Long ip2ulong(String ip) {
		try {
			InetAddress inet = InetAddress.getByName(String.valueOf(ip));
			int v = ByteBuffer.wrap(inet.getAddress()).getInt();
			return Integer.toUnsignedLong(v);
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
