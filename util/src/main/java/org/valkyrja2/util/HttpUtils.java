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
 * http?????????
 *
 * @author Tequila
 * @create 2022/06/30 22:26
 **/
public class HttpUtils {

	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

	/** connection manager */
	private static final PoolingHttpClientConnectionManager connManager;
	
	/** TEXT_PLAIN???UTF8???????????? */
	public static final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", Consts.UTF_8);

	/** ??????????????? */
	private static final Charset DEFAULT_CHARSET = Consts.UTF_8;
	
	/** ????????????????????? */
	private static final int DEFAULT_MAX_TOTAL = 128;

	private static final int DEFAULT_MAX_PER_ROUTE = 16;
	
	/** ??????socket timeout??????????????? */
	private static final int DEFAULT_SOCKET_TIMEOUT = 180;
	
	/** ??????connect timeout??????????????? */
	private static final int DEFAULT_CONNECT_TIMEOUT = 180;

	/** ???????????? */
	private static final boolean DEFAULT_RETRY = false;

	/** ?????????????????? */
	private static final int DEFAULT_RETRY_TIMES = 3;

	/** ??????keepalive??????????????? */
	private static final int DEFAULT_KEEPALIVE_TIME = 5;
	
	/** SSL?????? */
	private static final String SSL_MODE = "TLS";
	
	/** https schema */
	private static final String HTTPS_SCHEMA = "https";
	
	/** http schema */
	private static final String HTTP_SCHEMA = "http";
	
	/** ??????ssl???????????? */
	private static final boolean DEFAULT_SKIP_SSL_CHECK = false;
	
	/** ???????????? */
	private static final int DOWNLOAD_BUFFER_SIZE = 8 * 1024;
	
	/** ??????hash????????? */
	private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
	
	/** ??????????????????hash??? */
	private static final boolean DEFAULT_COMPUTE_HASH = false;
	
	/** ?????????????????????????????????????????? */
	private static final long STRING_CHUNK_SIZE = 512L * 1024;

	/** ???????????? */
	private static HttpUtilsConfig config;

	/** ?????????????????? */
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
	
	/* ???????????????????????????????????? */
    static {
		config = new HttpUtilsConfig();  // ?????????????????????

    	ConnectionSocketFactory plainSF = PlainConnectionSocketFactory.getSocketFactory();
    	SSLConnectionSocketFactory sslSF = null;
		SSLContext sslcontext;
		try {
			sslcontext = SSLContext.getInstance(SSL_MODE);
			sslcontext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
			sslSF = new SSLConnectionSocketFactory(sslcontext, (hostname, session) -> {
				if (config.isSslCheck()) {  // ??????????????????ssl host?????????
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
        
        // ?????????????????????
        connManager.setMaxTotal(config.getMaxTotal());
        // ??????????????????????????????
        connManager.setDefaultMaxPerRoute(config.getMaxPerRoute());
    }

	/**
	 * ??????http???????????????
	 *
	 * @param retry        ??????
	 * @param retryHandler ??????????????????
	 * @return {@link HttpClientBuilder }
	 * @author Tequila
	 * @date 2022/07/01 14:30
	 */
	public static HttpClientBuilder defaultHttpClientBuilder(boolean retry, HttpRequestRetryHandler retryHandler) {
		HttpClientBuilder builder = HttpClients.custom()
				.setConnectionManager(connManager);
		if (retry) {  // ????????????????????????retry
			builder.setRetryHandler(retryHandler);
		}

		return builder;
	}

	/**
	 * ??????http???????????????
	 *
	 * @return {@link HttpClientBuilder }
	 * @author Tequila
	 * @date 2022/07/01 14:34
	 */
	public static HttpClientBuilder defaultHttpClientBuilder() {
		return defaultHttpClientBuilder(config.isRetry(), new CustomHttpRequestRetryHandler());
	}

	/**
	 * ????????????http??????
	 *
	 * @return {@link CloseableHttpClient }
	 * @author Tequila
	 * @date 2022/07/01 14:19
	 */
	public static CloseableHttpClient createHttpClient(boolean retry, HttpRequestRetryHandler retryHandler) {
		return defaultHttpClientBuilder(retry, retryHandler).build();
    }

	/**
	 * ????????????http??????
	 *
	 * @return {@link CloseableHttpClient }
	 * @author Tequila
	 * @date 2022/07/01 14:32
	 */
	public static CloseableHttpClient createHttpClient() {
		return createHttpClient(config.isRetry(), new CustomHttpRequestRetryHandler());
	}

	/**
	 * ??????????????????
	 *
	 * @return {@link RequestConfig }
	 * @author Tequila
	 * @date 2022/07/01 16:05
	 */
	public static RequestConfig createRequestConfig() {
		return RequestConfig.custom()  // ?????????????????????????????????
				.setSocketTimeout(config.getSocketTimeout())
				.setConnectTimeout(config.getConnectTimeout())
				.build();
	}

	/**
	 * ??????http request ??????
	 *
	 * @param httpRequest http??????????????????get/post/put???
	 * @return {@link HttpResponse }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:24
	 */
	public static HttpResult executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
		// ????????????
		try (CloseableHttpClient httpClient = createHttpClient();
			 CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {  // ??????http post

			/* ??????????????? */
			HttpResult result = new HttpResult(httpResponse.getStatusLine().getStatusCode());
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				result.setContentType(ContentType.getOrDefault(httpEntity));
				result.setContent(EntityUtils.toByteArray(httpEntity));
			}

			/* ????????????header */
			for (Header h: httpResponse.getAllHeaders()) {
				BufferedHeader resHeader = (BufferedHeader) h;
				result.addHeader(resHeader.getName(), resHeader.getValue());
			}

			return result;
		}
	}

	/**
	 * http post ??????
	 *
	 * @param url    url
	 * @param header header
	 * @param entity http??????
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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
	 * http post ??????
	 *
	 * @param url    url
	 * @param header ???
	 * @param params ????????????
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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
	 * http post ??????
	 *
	 * @param url     url
	 * @param header  header
	 * @param params  ??????
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 14:48
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, Map<String, String> params, Charset charset) throws IOException {
		HttpResult result = doPostReturnHttpResult(url, header, params);
		return doReturnString(result, charset);
	}

	/**
	 * http post ??????
	 *
	 * @param url     url
	 * @param header  ???
	 * @param entity  ??????
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:02
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, HttpEntity entity, Charset charset) throws IOException {
		HttpResult result = doPostReturnHttpResult(url, header, entity);
		return doReturnString(result, charset);
	}

	/**
	 * http post ??????
	 *
	 * @param url    url
	 * @param header ???
	 * @param entity ??????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:03
	 */
	public static String doPostReturnString(final String url, Map<String, String> header, HttpEntity entity) throws IOException {
		return doPostReturnString(url, header, entity, config.getCharset());
	}

	/**
	 * http post json??????
	 *
	 * @param url    url
	 * @param header header
	 * @param json   json
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 17:52
	 */
	public static HttpResult doPostJsonReturnHttpResult(final String url, final Map<String, String> header, final String json) throws IOException {
		Map<String, String> jsonHeader = new HashMap<>();
		if (header != null) jsonHeader.putAll(header);
		jsonHeader.putIfAbsent(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		AbstractHttpEntity entity;
		if (STRING_CHUNK_SIZE > json.length()) {  // ???????????????????????????512K?????????????????????????????????
			entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		} else {
			entity = new InputStreamEntity(new ByteArrayInputStream(json.getBytes()), ContentType.APPLICATION_JSON);
			entity.setChunked(true);
		}

		return doPostReturnHttpResult(url, jsonHeader, entity);
	}

	/**
	 * http post json??????
	 *
	 * @param url    url
	 * @param header header
	 * @param json   json
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 18:10
	 */
	public static String doPostJsonReturnString(final String url, Map<String, String> header, final String json) throws IOException {
		return doPostJsonReturnString(url, header, json, config.charset);
	}

	/**
	 * http post json??????
	 *
	 * @param url     url
	 * @param header  ???
	 * @param json    json
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 18:04
	 */
	public static String doPostJsonReturnString(final String url, Map<String, String> header, String json, Charset charset) throws IOException {
		HttpResult result = doPostJsonReturnHttpResult(url, header, json);
		return doReturnString(result, charset);
	}

	/**
	 * http post json??????
	 *
	 * @param url  url
	 * @param json json
	 * @return {@link String }
	 * @throws HttpRuntimeException http???????????????
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
	 * http post Multipart ??????
	 *
	 * @param url    url
	 * @param header header
	 * @param parts  Multipart????????????????????????Entry?????????????????????key???name, v1?????????, v2???ContentType
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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
				/* ?????????????????? */
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
				/* ?????????????????????????????? */
				if (entry.getV1() instanceof File) {  // File
					builder.addBinaryBody(entry.getKey(), (File) entry.getV1());
				} else if (entry.getV1() instanceof byte[]) {  // ???????????????
					builder.addBinaryBody(entry.getKey(), (byte[]) entry.getV1(), ContentType.APPLICATION_OCTET_STREAM, UUID.randomUUID().toString());
				} else {
					builder.addTextBody(entry.getKey(), entry.getV1() != null ? entry.getV1().toString() : "", TEXT_PLAIN_UTF8);
				}
			}
		}
		
		return doPostReturnHttpResult(url, header, builder.build());
	}

	/**
	 * http post Multipart ??????
	 *
	 * @param url    url
	 * @param header header
	 * @param parts  Multipart????????????????????????Entry?????????????????????key???name, v1?????????, v2???ContentType
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:09
	 */
	public static String doPostMultipartReturnString(final String url, Map<String, String> header, List<TripleEntry<String, Object, String>> parts) throws IOException {
		return doPostMultipartReturnString(url, header, parts, config.getCharset());
	}

	/**
	 * http post Multipart ??????
	 *
	 * @param url     url
	 * @param header  header
	 * @param parts   Multipart????????????????????????Entry?????????????????????key???name, v1?????????, v2???ContentType
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:17
	 */
	public static String doPostMultipartReturnString(final String url, Map<String, String> header, List<TripleEntry<String, Object, String>> parts, Charset charset) throws IOException {
		HttpResult result = doPostMultipartReturnHttpResult(url, header, parts);
		return doReturnString(result, charset);
	}

	/**
	 * http post form ??????
	 *
	 * @param url     url
	 * @param header  header
	 * @param form    ??????
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 18:13
	 */
	public static String doPostFormReturnString(final String url, Map<String, String> header, Map<String, String> form, Charset charset) throws IOException {
		HttpResult result = doPostFormReturnHttpResult(url, header, form);
		return doReturnString(result, charset);
	}

	/**
	 * http post form ??????
	 *
	 * @param url  url
	 * @param form ??????
	 * @return {@link String }
	 * @throws HttpRuntimeException http???????????????
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
	 * http post form ??????
	 *
	 * @param url    url
	 * @param header ???
	 * @param form   ??????
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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
	 * http get ??????
	 *
	 * @param url    url
	 * @param header ???
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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
	 * http get ??????
	 *
	 * @param url    url
	 * @param header ???
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:33
	 */
	public static String doGetReturnString(final String url, Map<String, String> header) throws IOException {
		return doGetReturnString(url, header, config.getCharset());
	}

	/**
	 * http get ??????
	 *
	 * @param url     url
	 * @param header  ???
	 * @param charset ?????????
	 * @return {@link String }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:44
	 */
	public static String doGetReturnString(final String url, Map<String, String> header, Charset charset) throws IOException {
		HttpResult result = doGetReturnHttpResult(url, header);
		return doReturnString(result, charset);
	}

	/**
	 * http get ??????
	 *
	 * @param url url
	 * @return {@link String }
	 * @throws HttpRuntimeException http???????????????
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
	 * ????????????HttpResult???String??????
	 *
	 * @param result  HttpResult
	 * @param charset ?????????
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

	/** ?????????hash */
	public static final int NO_COMPUTE_HASH   = 0;
	/** ??????hash */
	public static final int COMPUTE_HASH      = 1;
	/** ?????????hash????????????????????? */
	public static final int ONLY_COMPUTE_HASH = 2;

	/**
	 * ????????????
	 *
	 * @param url             url
	 * @param header          header
	 * @param outputFile          ?????????????????????????????????????????????
	 * @param computeHashMode ?????????????????????0 ????????????1 ?????????????????????2 ?????????hash
	 * @param hashAlgorithm   ????????????
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
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

		/* ??????hash???????????? */
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
				 * ???????????????hash????????????outputFile?????????????????????????????????????????????
				 * ????????????outputFile??????????????????????????????byte[]
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

				/* ??????????????????????????????HttpResult??????????????????????????????????????????????????????????????????byte[] */
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
	 * ????????????
	 *
	 * @param url    url
	 * @param header header
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 15:54
	 */
	public static HttpResult doDownloadFile(final String url, Map<String, String> header, File outputFile) throws IOException {
		return doDownloadFile(url, header, outputFile, config.intComputeHash(), config.getHashAlgorithm());
	}

	/**
	 * ????????????
	 *
	 * @param url        url
	 * @param outputFile ????????????
	 * @return {@link HttpResult }
	 * @throws IOException IO??????
	 * @author Tequila
	 * @date 2022/07/01 16:59
	 */
	public static HttpResult doDownloadFile(final String url, File outputFile) throws IOException {
		return doDownloadFile(url, null, outputFile, config.intComputeHash(), config.getHashAlgorithm());
	}

	/**
	 * ??????X509Trust??????????????????????????????
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
	 * ?????????http????????????????????????
	 *
	 * @author Tequila
	 * @create 2022/07/01 14:26
	 **/
	public static class CustomHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
		
		/** ??????????????????Exception */
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
	 * http?????????????????????
	 *
	 * @author Tequila
	 * @create 2022/07/01 13:39
	 **/
	public static class HttpUtilsConfig {

		/** ????????? */
		private Charset charset;

		/** ??????????????? */
		private int maxTotal;

		/** maxRoute */
		private int maxPerRoute;

		/** socket?????? */
		private int socketTimeout;

		/** ???????????? */
		private int connectTimeout;

		/** keepalive?????? */
		private int keepAliveTime;

		/** ??????ssl???????????? */
		private boolean sslCheck;

		/** ?????? */
		private boolean retry;

		/** ???????????? */
		private int retryTimes;

		/** ??????hash?????????????????????MD5,SHA256,SHA1???????????????SHA256 */
		private String hashAlgorithm;

		/** ????????????????????????????????????hash??? */
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
		 * ????????????hash???int???
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
