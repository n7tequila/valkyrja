/*
 * PROJECT valkyrja2
 * util/HttpUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.exception.HttpRuntimeException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http工具包
 *
 * @author Tequila
 * @create 2022/06/30 22:26
 **/
public class HttpUtils {

	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

	/** connection manager */
	private static final PoolingHttpClientConnectionManager connManager;
	
	/** TEXT_PLAIN的UTF8类型定义 */
	public static final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", Consts.UTF_8);

	/** 默认字符集 */
	private static final Charset DEFAULT_CHARSET = Consts.UTF_8;
	
	/** 默认最大连接数 */
	private static final int DEFAULT_MAX_TOTAL = 128;

	private static final int DEFAULT_MAX_PER_ROUTE = 16;
	
	/** 默认socket timeout时间（秒） */
	private static final int DEFAULT_SOCKET_TIMEOUT = 180;
	
	/** 默认connect timeout时间（秒） */
	private static final int DEFAULT_CONNECT_TIMEOUT = 180;

	/** 默认重试 */
	private static final boolean DEFAULT_RETRY = false;

	/** 默认重试次数 */
	private static final int DEFAULT_RETRY_TIMES = 3;

	/** 默认keepalive时间（秒） */
	private static final int DEFAULT_KEEPALIVE_TIME = 5;
	
	/** SSL模式 */
	private static final String SSL_MODE = "TLS";
	
	/** https schema */
	private static final String HTTPS_SCHEMA = "https";
	
	/** http schema */
	private static final String HTTP_SCHEMA = "http";
	
	/** 忽略ssl域名校验 */
	private static final boolean DEFAULT_SKIP_SSL_CHECK = false;
	
	/** 下载缓存 */
	private static final int DOWNLOAD_BUFFER_SIZE = 8 * 1024;
	
	/** 默认hash值算法 */
	private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
	
	/** 默认是否计算hash值 */
	private static final boolean DEFAULT_COMPUTE_HASH = false;
	
	/** 字符串进行分块传输的容量阈值 */
	private static final long STRING_CHUNK_SIZE = 512L * 1024;

	/** 配置信息 */
	private static HttpUtilsConfig config;

	/** 连接维持策略 */
	private static final ConnectionKeepAliveStrategy CONNECTION_KEEP_ALIVE_STRATEGY = (response, context) -> {
		HeaderElementIterator it = new BasicHeaderElementIterator
			(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		while (it.hasNext()) {
			HeaderElement he = it.nextElement();
			String param = he.getName();
			String value = he.getValue();
			if (value != null && param.equalsIgnoreCase("timeout")) {
				return Long.parseLong(value) * 1000;
			}
		}
		return config.getKeepAliveTime() * 1000L;
	};
	
	/* 静态代码块配置连接池信息 */
    static {
		config = new HttpUtilsConfig();  // 初始化配置信息

    	ConnectionSocketFactory plainSF = PlainConnectionSocketFactory.getSocketFactory();
    	SSLConnectionSocketFactory sslSF = null;
		SSLContext sslcontext;
		try {
			sslcontext = SSLContext.getInstance(SSL_MODE);
			sslcontext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
			sslSF = new SSLConnectionSocketFactory(sslcontext, (hostname, session) -> {
				if (config.isSslCheck()) {  // 判断是否进行ssl host的判断
					return hostname.equalsIgnoreCase(session.getPeerHost());
				} else {
					return true;
				}
			});
		} catch (Exception e) {
			log.warn("Can not init SSL context", e);
		}
        
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create()
                	.register(HTTP_SCHEMA, plainSF)
                	.register(HTTPS_SCHEMA, sslSF)
				.build();
        connManager = new PoolingHttpClientConnectionManager(registry);
        
        // 设置最大连接数
        connManager.setMaxTotal(config.getMaxTotal());
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(config.getMaxPerRoute());
    }

	/**
	 * 缺省http连接构建器
	 *
	 * @param retry        重试
	 * @param retryHandler 重试处理程序
	 * @return {@link HttpClientBuilder }
	 * @author Tequila
	 * @date 2022/07/01 14:30
	 */
	public static HttpClientBuilder defaultHttpClientBuilder(boolean retry, HttpRequestRetryHandler retryHandler) {
		HttpClientBuilder builder = HttpClients.custom()
				.setConnectionManager(connManager);
		if (retry) {  // 判断是否需要自动retry
			builder.setRetryHandler(retryHandler);
		}

		return builder;
	}

	/**
	 * 缺省http连接构建器
	 *
	 * @return {@link HttpClientBuilder }
	 * @author Tequila
	 * @date 2022/07/01 14:34
	 */
	public static HttpClientBuilder defaultHttpClientBuilder() {
		return defaultHttpClientBuilder(config.isRetry(), new CustomHttpRequestRetryHandler());
	}

	/**
	 * 创建一个http连接
	 *
	 * @return {@link CloseableHttpClient }
	 * @author Tequila
	 * @date 2022/07/01 14:19
	 */
	public static CloseableHttpClient createHttpClient(boolean retry, HttpRequestRetryHandler retryHandler) {
		return defaultHttpClientBuilder(retry, retryHandler).build();
    }

	/**
	 * 创建一个http连接
	 *
	 * @return {@link CloseableHttpClient }
	 * @author Tequila
	 * @date 2022/07/01 14:32
	 */
	public static CloseableHttpClient createHttpClient() {
		return createHttpClient(config.isRetry(), new CustomHttpRequestRetryHandler());
	}

	/**
	 * 创建请求配置
	 *
	 * @return {@link RequestConfig }
	 * @author Tequila
	 * @date 2022/07/01 16:05
	 */
	public static RequestConfig createRequestConfig() {
		return RequestConfig.custom()  // 设置请求和传输超时时间
				.setSocketTimeout(config.getSocketTimeout())
				.setConnectTimeout(config.getConnectTimeout())
				.build();
	}

	/**
	 * 通用http request 操作
	 *
	 * @param httpRequest http请求，可以是get/post/put等
	 * @return {@link HttpResponse }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:24
	 */
	public static HttpResult executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
		// 发送请求
		try (CloseableHttpClient httpClient = createHttpClient();
			 CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {  // 执行http post

			/* 提取返回值 */
			HttpResult result = new HttpResult(httpResponse.getStatusLine().getStatusCode());
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				result.setContentType(ContentType.getOrDefault(httpEntity));
				result.setContent(EntityUtils.toByteArray(httpEntity));
			}

			/* 提取所有header */
			for (Header h: httpResponse.getAllHeaders()) {
				BufferedHeader resHeader = (BufferedHeader) h;
				result.addHeader(resHeader.getName(), resHeader.getValue());
			}

			return result;
		}
	}

	/**
	 * http post 操作
	 *
	 * @param url    url
	 * @param header header
	 * @param entity http实体
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 14:23
	 */
	public static HttpResult doPostReturnHttpResult(final String url, Map<String, String> header, HttpEntity entity) throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		Objects.requireNonNull(entity, "entity must not be null");

		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(createRequestConfig());
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		httpPost.setEntity(entity);

		return executeHttpRequest(httpPost);
	}

	/**
	 * http post 操作
	 *
	 * @param url    url
	 * @param header 头
	 * @param params 参数个数
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 14:49
	 */
	public static HttpResult doPostReturnHttpResult(final String url, Map<String, String> header, Map<String, String> params) throws IOException {
		List<NameValuePair> form = new ArrayList<>();
		params.forEach((k, v) -> form.add(new BasicNameValuePair(k, v)));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, config.getCharset());
		return doPostReturnHttpResult(url, header, entity);
	}

	/**
	 * http post 操作
	 *
	 * @param url     url
	 * @param header  header
	 * @param params  参数
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 14:48
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, Map<String, String> params, Charset charset) throws IOException {
		HttpResult result = doPostReturnHttpResult(url, header, params);
		return doReturnString(result, charset);
	}

	/**
	 * http post 操作
	 *
	 * @param url     url
	 * @param header  头
	 * @param entity  实体
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:02
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, HttpEntity entity, Charset charset) throws IOException {
		HttpResult result = doPostReturnHttpResult(url, header, entity);
		return doReturnString(result, charset);
	}

	/**
	 * http post 操作
	 *
	 * @param url    url
	 * @param header 头
	 * @param entity 实体
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:03
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, HttpEntity entity) throws IOException {
		return doPostReturnString(url, header, entity, config.getCharset());
	}

	/**
	 * http post json操作
	 *
	 * @param url    url
	 * @param header header
	 * @param json   json
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 17:52
	 */
	public static HttpResult doPostJsonReturnHttpResult(final String url, final Map<String, String> header, final String json) throws IOException {
		Map<String, String> jsonHeader = new HashMap<>();
		if (header != null) jsonHeader.putAll(header);
		jsonHeader.putIfAbsent(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		AbstractHttpEntity entity;
		if (STRING_CHUNK_SIZE > json.length()) {  // 如果字符串容量大于512K，则使用分块传输的模式
			entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		} else {
			entity = new InputStreamEntity(new ByteArrayInputStream(json.getBytes()), ContentType.APPLICATION_JSON);
			entity.setChunked(true);
		}

		return doPostReturnHttpResult(url, jsonHeader, entity);
	}

	/**
	 * http post json操作
	 *
	 * @param url    url
	 * @param header header
	 * @param json   json
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 18:10
	 */
	public static String doPostJsonReturnString(final String url, Map<String, String> header, final String json) throws IOException {
		return doPostJsonReturnString(url, header, json, config.charset);
	}

	/**
	 * http post json操作
	 *
	 * @param url     url
	 * @param header  头
	 * @param json    json
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 18:04
	 */
	public static String doPostJsonReturnString(final String url, Map<String, String> header, String json, Charset charset) throws IOException {
		HttpResult result = doPostJsonReturnHttpResult(url, header, json);
		return doReturnString(result, charset);
	}

	/**
	 * http post json操作
	 *
	 * @param url  url
	 * @param json json
	 * @return {@link String }
	 * @throws HttpRuntimeException http运行期错误
	 * @author Tequila
	 * @date 2022/07/01 18:24
	 */
	public static String doPostJson(final String url, final String json) throws HttpRuntimeException {
		try {
			return doPostJsonReturnString(url, null, json, config.charset);
		} catch (IOException e) {
			throw new HttpRuntimeException("Execute doPostJson raise exception", e);
		}
	}

	/**
	 * http post Multipart 操作
	 *
	 * @param url    url
	 * @param header header
	 * @param parts  Multipart对象，使用三元素Entry对象定义。其中key为name, v1为数据, v2为ContentType
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:04
	 */
	public static HttpResult doPostMultipartReturnHttpResult(final String url, Map<String, String> header, List<TripleEntry<String, Object, String>> parts) throws IOException {
		Objects.requireNonNull(parts, "parts must not be null");

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setCharset(config.getCharset());
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		for (TripleEntry<String, Object, String> entry: parts) {
			if (StringUtils.isNotBlank(entry.getV2()) 
					&& (!(entry.getV1() instanceof File))
					&& (!(entry.getV1() instanceof byte[]))) {
				/* 普通数据传输 */
				ContentType contentType;
				if (entry.getV2().indexOf(';') >= 0) {
					String[] strs = entry.getV2().split(";charset=");
					String mimeType = strs[0];
					String charset = strs[1];
					contentType = ContentType.create(mimeType, charset);
				} else {
					contentType = ContentType.create(entry.getV2());
				}
				 
				StringBody stringBody = new StringBody(entry.getV1() != null ? entry.getV1().toString() : "", contentType);
				builder.addPart(entry.getKey(), stringBody);
			} else {
				/* 文件或二进制数据传输 */
				if (entry.getV1() instanceof File) {  // File
					builder.addBinaryBody(entry.getKey(), (File) entry.getV1());
				} else if (entry.getV1() instanceof byte[]) {  // 二进制数据
					builder.addBinaryBody(entry.getKey(), (byte[]) entry.getV1(), ContentType.APPLICATION_OCTET_STREAM, UUID.randomUUID().toString());
				} else {
					builder.addTextBody(entry.getKey(), entry.getV1() != null ? entry.getV1().toString() : "", TEXT_PLAIN_UTF8);
				}
			}
		}
		
		return doPostReturnHttpResult(url, header, builder.build());
	}

	/**
	 * http post Multipart 操作
	 *
	 * @param url    url
	 * @param header header
	 * @param parts  Multipart对象，使用三元素Entry对象定义。其中key为name, v1为数据, v2为ContentType
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:09
	 */
	public static String doPostMultipartReturnString(final String url, Map<String, String> header, List<TripleEntry<String, Object, String>> parts) throws IOException {
		return doPostMultipartReturnString(url, header, parts, config.getCharset());
	}

	/**
	 * http post Multipart 操作
	 *
	 * @param url     url
	 * @param header  header
	 * @param parts   Multipart对象，使用三元素Entry对象定义。其中key为name, v1为数据, v2为ContentType
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:17
	 */
	public static String doPostMultipartReturnString(final String url, Map<String, String> header, List<TripleEntry<String, Object, String>> parts, Charset charset) throws IOException {
		HttpResult result = doPostMultipartReturnHttpResult(url, header, parts);
		return doReturnString(result, charset);
	}

	/**
	 * http post form 操作
	 *
	 * @param url     url
	 * @param header  header
	 * @param form    表单
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 18:13
	 */
	public static String doPostFormReturnString(final String url, Map<String, String> header, Map<String, String> form, Charset charset) throws IOException {
		HttpResult result = doPostFormReturnHttpResult(url, header, form);
		return doReturnString(result, charset);
	}

	/**
	 * http post form 操作
	 *
	 * @param url  url
	 * @param form 表单
	 * @return {@link String }
	 * @throws HttpRuntimeException http运行期错误
	 * @author Tequila
	 * @date 2022/07/01 18:25
	 */
	public static String doPostForm(final String url, Map<String, String> form) throws HttpRuntimeException {
		try {
			return doPostFormReturnString(url, null, form, config.getCharset());
		} catch (IOException e) {
			throw new HttpRuntimeException("Execute doPostForm raise exception", e);
		}
	}

	/**
	 * http post form 操作
	 *
	 * @param url    url
	 * @param header 头
	 * @param form   表单
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:19
	 */
	public static HttpResult doPostFormReturnHttpResult(final String url, Map<String, String> header, Map<String, String> form) throws IOException {
		List<NameValuePair> postForm = new ArrayList<>();
		form.forEach((k, v) -> postForm.add(new BasicNameValuePair(k, v)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postForm, config.getCharset());

		return doPostReturnHttpResult(url, header, entity);
	}

	/**
	 * http get 操作
	 *
	 * @param url    url
	 * @param header 头
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:20
	 */
	public static HttpResult doGetReturnHttpResult(final String url, Map<String, String> header) throws IOException {
		Objects.requireNonNull(url, "url must not be null");

		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(createRequestConfig());
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}

		return executeHttpRequest(httpGet);
	}

	/**
	 * http get 操作
	 *
	 * @param url    url
	 * @param header 头
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:33
	 */
	public static String doGetReturnString(final String url, Map<String, String> header) throws IOException {
		return doGetReturnString(url, header, config.getCharset());
	}

	/**
	 * http get 操作
	 *
	 * @param url     url
	 * @param header  头
	 * @param charset 字符集
	 * @return {@link String }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:44
	 */
	public static String doGetReturnString(final String url, Map<String, String> header, Charset charset) throws IOException {
		HttpResult result = doGetReturnHttpResult(url, header);
		return doReturnString(result, charset);
	}

	/**
	 * http get 操作
	 *
	 * @param url url
	 * @return {@link String }
	 * @throws HttpRuntimeException http运行期错误
	 * @author Tequila
	 * @date 2022/07/01 18:25
	 */
	public static String doGet(final String url) throws HttpRuntimeException {
		try {
			return doGetReturnString(url, null);
		} catch (IOException e) {
			throw new HttpRuntimeException("Execute doGet raise exception", e);
		}
	}

	/**
	 * 通用处理HttpResult转String操作
	 *
	 * @param result  HttpResult
	 * @param charset 字符集
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/07/01 18:16
	 */
	private static String doReturnString(HttpResult result, Charset charset) {
		if (result.getHttpStatus().is2xxSuccessful()) {
			return result.getContentString(charset);
		} else {
			return null;
		}
	}

	/** 不计算hash */
	public static final int NO_COMPUTE_HASH   = 0;
	/** 计算hash */
	public static final int COMPUTE_HASH      = 1;
	/** 只计算hash，跳过数据存储 */
	public static final int ONLY_COMPUTE_HASH = 2;

	/**
	 * 下载文件
	 *
	 * @param url             url
	 * @param header          header
	 * @param outputFile          如果下载成功，文件存放目标位置
	 * @param computeHashMode 计算哈希模式，0 不计算，1 计算并且下载，2 只计算hash
	 * @param hashAlgorithm   散列算法
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 16:40
	 */
	public static HttpResult doDownloadFile(final String url, Map<String, String> header, File outputFile, int computeHashMode, String hashAlgorithm) throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		if (computeHashMode < NO_COMPUTE_HASH || computeHashMode > ONLY_COMPUTE_HASH) throw new IllegalArgumentException("computeHashMode is 0,1,2");
		if (computeHashMode > NO_COMPUTE_HASH) Objects.requireNonNull(hashAlgorithm, "hashAlgorithm must not be null");

		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(createRequestConfig());
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}

		/* 生成hash计算对象 */
		MessageDigest hashSum = null;
		try {
			if (NO_COMPUTE_HASH != computeHashMode) {
				hashSum = MessageDigest.getInstance(hashAlgorithm);
			}
		} catch (NoSuchAlgorithmException e) {
			log.warn("Can not create digest object {}", hashAlgorithm, e);
		}
		InputStream in;
		try (
				CloseableHttpClient httpClient = createHttpClient();
				CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
				/*
				 * 如果仅计算hash，则不管outputFile是否定义，则都跳过文件信息缓存
				 * 否则根据outputFile来判断是输出文件或者byte[]
				 */
				OutputStream out = ONLY_COMPUTE_HASH == computeHashMode ? null : (outputFile != null ? Files.newOutputStream(outputFile.toPath()) : new ByteArrayOutputStream());
		) {
			HttpResult result = new HttpResult(httpResponse.getStatusLine().getStatusCode());
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				in = httpEntity.getContent();

				byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					if (out != null) {
						out.write(buffer, 0, bytesRead);
					}
					if (hashSum != null) hashSum.update(buffer, 0, bytesRead);
				}
				if (out != null) out.flush();
				EntityUtils.consume(httpEntity);

				/* 如果需要下载文件，则HttpResult返回文件路径，如果不需要下载文件，则返回文件byte[] */
				if (out != null) {
					if (outputFile != null) {
						result.setContent(outputFile.getPath().getBytes());
					} else {
						result.setContent(((ByteArrayOutputStream) out).toByteArray());
					}
				}

				result.setContentType(ContentType.getOrDefault(httpEntity));
				if (hashSum != null) result.setHash(Hex.encodeHexString(hashSum.digest()));
			}

			return result;
		}
	}

	/**
	 * 下载文件
	 *
	 * @param url    url
	 * @param header header
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 15:54
	 */
	public static HttpResult doDownloadFile(final String url, Map<String, String> header, File outputFile) throws IOException {
		return doDownloadFile(url, header, outputFile, config.intComputeHash(), config.getHashAlgorithm());
	}

	/**
	 * 下载文件
	 *
	 * @param url        url
	 * @param outputFile 输出文件
	 * @return {@link HttpResult }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/07/01 16:59
	 */
	public static HttpResult doDownloadFile(final String url, File outputFile) throws IOException {
		return doDownloadFile(url, null, outputFile, config.intComputeHash(), config.getHashAlgorithm());
	}

	/**
	 * 实现X509Trust管理，信任所有的证书
	 *
	 * @author Tequila
	 * @create 2022/07/01 17:00
	 **/
	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// nothing implement
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// nothing implement
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	/**
	 * 自定义http请求重试处理程序
	 *
	 * @author Tequila
	 * @create 2022/07/01 14:26
	 **/
	public static class CustomHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
		
		/** 默认的重试的Exception */
		private static final List<Class<? extends IOException>> RETRY_EXCEPTIONS = Arrays.asList(
				NoHttpResponseException.class, 
				ConnectTimeoutException.class
		);
		
		public CustomHttpRequestRetryHandler() {
			super(config.getRetryTimes(), false, RETRY_EXCEPTIONS);
		}

		protected CustomHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
			super(retryCount, requestSentRetryEnabled, RETRY_EXCEPTIONS);
		}
	}

	/**
	 * http工具包配置信息
	 *
	 * @author Tequila
	 * @create 2022/07/01 13:39
	 **/
	public static class HttpUtilsConfig {

		/** 字符集 */
		private Charset charset;

		/** 最大连接数 */
		private int maxTotal;

		/** maxRoute */
		private int maxPerRoute;

		/** socket超时 */
		private int socketTimeout;

		/** 连接超时 */
		private int connectTimeout;

		/** keepalive时间 */
		private int keepAliveTime;

		/** 跳过ssl域名检测 */
		private boolean sslCheck;

		/** 重试 */
		private boolean retry;

		/** 重试次数 */
		private int retryTimes;

		/** 当前hash值算法，可使用MD5,SHA256,SHA1等，默认为SHA256 */
		private String hashAlgorithm;

		/** 当前默认是否在下载时计算hash值 */
		private boolean computeHash;


		public HttpUtilsConfig() {
			this.charset = DEFAULT_CHARSET;
			this.maxTotal = DEFAULT_MAX_TOTAL;
			this.maxPerRoute = DEFAULT_MAX_PER_ROUTE;
			this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
			this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
			this.keepAliveTime = DEFAULT_KEEPALIVE_TIME;
			this.sslCheck = DEFAULT_SKIP_SSL_CHECK;
			this.retry = DEFAULT_RETRY;
			this.retryTimes = DEFAULT_RETRY_TIMES;
			this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
			this.computeHash = DEFAULT_COMPUTE_HASH;
		}

		/**
		 * 是否运算hash的int值
		 *
		 * @return int
		 * @author Tequila
		 * @date 2022/07/01 16:57
		 */
		public int intComputeHash() {
			return this.computeHash ? COMPUTE_HASH : NO_COMPUTE_HASH;
		}

		public Charset getCharset() {
			return charset;
		}

		public void setCharset(Charset charset) {
			this.charset = charset;
		}

		public int getMaxTotal() {
			return maxTotal;
		}

		public void setMaxTotal(int maxTotal) {
			this.maxTotal = maxTotal;
		}

		public int getMaxPerRoute() {
			return maxPerRoute;
		}

		public void setMaxPerRoute(int maxPerRoute) {
			this.maxPerRoute = maxPerRoute;
		}

		public int getSocketTimeout() {
			return socketTimeout;
		}

		public void setSocketTimeout(int socketTimeout) {
			this.socketTimeout = socketTimeout;
		}

		public int getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public int getKeepAliveTime() {
			return keepAliveTime;
		}

		public void setKeepAliveTime(int keepAliveTime) {
			this.keepAliveTime = keepAliveTime;
		}

		public boolean isSslCheck() {
			return sslCheck;
		}

		public void setSslCheck(boolean sslCheck) {
			this.sslCheck = sslCheck;
		}

		public String getHashAlgorithm() {
			return hashAlgorithm;
		}

		public void setHashAlgorithm(String hashAlgorithm) {
			this.hashAlgorithm = hashAlgorithm;
		}

		public boolean isComputeHash() {
			return computeHash;
		}

		public void setComputeHash(boolean computeHash) {
			this.computeHash = computeHash;
		}

		public boolean isRetry() {
			return retry;
		}

		public void setRetry(boolean retry) {
			this.retry = retry;
		}

		public int getRetryTimes() {
			return retryTimes;
		}

		public void setRetryTimes(int retryTimes) {
			this.retryTimes = retryTimes;
		}
	}
}
