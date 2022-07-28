/*
 * PROJECT valkyrja2
 * core/RequestWatching.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.valkyrja2.mvc.RequestContextFactory;
import org.valkyrja2.util.DateUtils;
import org.valkyrja2.util.NetworkUtils;
import org.valkyrja2.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;

import static org.valkyrja2.component.auditor.AuditConst.LL_TRACE;

/**
 * 用于监视每一个请求的数据，并输出到日志中。<br>
 * 配置时可用参数对自动输出的日志进行配置<br>
 * <pre>
 * useRequestContext 是否使用RequestContext信息输出日志
 * loggerLevel       日志输出级别
 * bodyLogger        是否输出body对象的日志，默认为不输出，如需输出，需要对使用CachingRequestBodyFilter进行配置
 * bodyLogLength     body日志最大的输出总长度
 * bodyLogMode       body数据超限输出模式，默认为跳过输出。
 *                   FULL 全部输出
 *                   CUT  根据bodyLogLength截取输出
 *                   SKIP 跳过输出，并以%ltBOLB:sha256:length&gt格式进行替换输出
 * simplyMode        简要输出模式，仅输出url,requestId,query,body
 * </pre>
 * spring配置
 * <pre>
 * &lt;mvc:interceptors&gt;
 *   &lt;mvc:interceptor&gt;
 *     &lt;mvc:mapping path="/services/**" /&gt;
 *     &lt;bean class="org.valkyrja.framework.core.spring.RequestWatching"&gt;
 *       &lt;property name="useRequestContext" value="true" /&gt;
 *       &lt;property name="loggerLevel" value="INFO" /&gt;
 *       &lt;property name="bodyLogger" value="true" /&gt;
 *     &lt;/bean&gt;
 *   &lt;/mvc:interceptor&gt;
 * &lt;/mvc:interceptors&gt;
 * </pre>
 *
 * @author Tequila
 * @create 2022/07/19 18:05
 **/
public class RequestWatching implements AsyncHandlerInterceptor {

	protected static final String[] LOGGER_LEVEL = { AuditConst.LL_TRACE, AuditConst.LL_DEBUG, AuditConst.LL_INFO };

	/** body对象输出日志模式 */
	public static final String BLM_FULL       = "FULL";       // 全部输出
	public static final String BLM_TRUNCATED  = "TRUNCATED";  // 裁剪输出
	public static final String BLM_SKIP       = "SKIP";       // 跳过输出
	private static final String[] BODY_LOG_MODE = { BLM_FULL, BLM_TRUNCATED, BLM_SKIP };

	/** StopWatch 模式常量 */
	public static final String SWM_NONE   = "NONE";    // 不进行StopWatch
	public static final String SWM_SIMPLY = "SIMPLY";  // 简单模式
	public static final String SWM_DETAIL = "DETAIL";  // 详细模式
	private static final String[] STOPWATCH_MODES = { SWM_NONE, SWM_SIMPLY, SWM_DETAIL };

	/**
	 * 是否使用RequestContext信息输出日志<br>
	 * 可在Spring配置文件中进行配置。因为是static变量，因此也可以在程序层进行切换
	 */
	private boolean useRequestContext = true;
	
	/** 日志输出级别 */
	private String loggerLevel = AuditConst.LL_DEBUG;
	
	/** 是否输出body对象的日志，默认为不输出 */
	private boolean bodyLogger = true;
	
	/** body日志最大的输出总长度 */
	private int bodyLogLength = 512;
	
	/** body数据超限输出模式，默认为跳过输出 */
	private String bodyLogMode = BLM_TRUNCATED;
	
	/** 简略模式 */
	private boolean simplyMode = false;

	/** 计时器模式 */
	private String stopWatchMode = SWM_SIMPLY;

	/** 默认的Logger输出对象 */
	private AuditLogger auditLogger = new DefaultAuditLogger("RequestWatching");

	/** 会话StopWatch */
	private static NamedThreadLocal<StopWatch> sessionStopWatch = new NamedThreadLocal<>("RequestWatchingStopWatch");

	/**
	 * 统一的日志输出操作函数<br>
	 * 默认方法将会输出所有内容而不限定长度<br>
	 * 默认方法将会定义信息为详细信息
	 *
	 * @param s 字符串
	 * @author Tequila
	 * @date 2022/07/19 18:18
	 */
	private void logger(String s) {
		logger(s, -1, true);
	}

	/**
	 * 统一的日志输出操作函数<br>
	 * 默认方法将会输出所有内容而不限定长度
	 *
	 * @param s          日志输出内容
	 * @param showDetail 显示细节
	 * @author Tequila
	 * @date 2022/07/19 20:10
	 */
	private void logger(String s, boolean showDetail) {
		logger(s, bodyLogLength, showDetail);
	}

	/**
	 * 统一的日志输出操作函数，可通过length变量控制输出的长度
	 *
	 * @param s          日志输出内容
	 * @param length     最大输出日志长度
	 * @param showDetail 显示细节
	 * @author Tequila
	 * @date 2022/07/19 18:18
	 */
	private void logger(String s, int length, boolean showDetail) {
		if (simplyMode && showDetail) return;  // 如果是简要模式，且当前信息为详细信息，则忽略输出
		
		String logOut = "";
		/* 处理body对象日志输出模式 */
		if (length != -1 && s.length() > length) {
			switch (bodyLogMode) {
				case BLM_FULL: logOut = s; break;
				case BLM_TRUNCATED: logOut = StringUtils.abbreviate(s, bodyLogLength); break;
				case BLM_SKIP: logOut = String.format("<BLOB:%d>", s.length()); break;
			}
		} else {
			logOut = s;
		}
		
		if (useRequestContext && RequestContextFactory.getContext() != null) {
			logOut = String.format("[%s] %s", RequestContextFactory.getContext().getRequestId(), logOut);
		}
		auditLogger.logger(loggerLevel, new AuditDescription(logOut));
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestSignature = String.format("[%s] %s", request.getMethod(), request.getRequestURI());
		StopWatch sw = null;
		if (!SWM_NONE.equals(stopWatchMode)) {
			sw = new StopWatch(RequestContextFactory.getContext().getRequestId());
			sessionStopWatch.set(sw);
			sw.start(requestSignature);
		}

		logger(String.format("*** API-URL:%s ***", requestSignature), false);

		if (useRequestContext && RequestContextFactory.getContext() != null) {
			logger("**********requestContext-start**********");
			logger("RequestId:" + RequestContextFactory.getContext().getRequestId(), false);
			logger("HostId:" + RequestContextFactory.getContext().getHostId(), false);
			logger("HostIPAddress:" + RequestContextFactory.getContext().getHostIPAddress(), false);
			logger("RemoteIPAddress:" + RequestContextFactory.getContext().getRemoteIPAddress());
			logger("ReceiveDate:" + DateUtils.formatDate(RequestContextFactory.getContext().getReceiveDate()));
			logger("**********requestContext-end************");
		}

		logger("**********requestHead-start*************");
		Enumeration<String> heads = request.getHeaderNames();
		while (heads.hasMoreElements()) {
			String headName = heads.nextElement();
			logger(headName + "=" + request.getHeader(headName));
		}
		logger("**********requestHead-end***************");

		logger("**********Parameter-start***************");
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String pars = names.nextElement();
			logger(String.format("%s=%s", pars, request.getParameter(pars)), false);
		}
		logger("**********Parameter-end*****************");
		
		if (bodyLogger) {
			logger("**********requestBody-start*************");
			String body = NetworkUtils.getHttpRequestBody(request);
			if (StringUtils.isNotBlank(body)) {
				logger(body, bodyLogLength, false);
			}
			logger("**********requestBody-end***************");
		}
		if (!SWM_NONE.equals(stopWatchMode) && sw != null) {
			sw.stop();
		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (!SWM_NONE.equals(stopWatchMode)) {
			StopWatch sw = sessionStopWatch.get();
			if (sw.isRunning()) sw.stop();

			logger(String.format("[%s] %s 消耗时间：%sms", request.getMethod(), request.getRequestURI(), sw.getTotalTimeMillis()), false);
			if (SWM_DETAIL.equals(stopWatchMode)) {
				logger("运行时间详情：\n" + sw.prettyPrint());
			}
		}

		if (sessionStopWatch.get() != null) sessionStopWatch.remove();
	}

	public boolean isUseRequestContext() {
		return useRequestContext;
	}

	public void setUseRequestContext(boolean useRequestContext) {
		this.useRequestContext = useRequestContext;
	}

	public String getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(String loggerLevel) {
		if (Arrays.asList(LOGGER_LEVEL).contains(loggerLevel)) {
			this.loggerLevel = loggerLevel;
		} else {
			this.loggerLevel = AuditConst.LL_DEBUG;
		}
	}

	public boolean isBodyLogger() {
		return bodyLogger;
	}

	public void setBodyLogger(boolean bodyLogger) {
		this.bodyLogger = bodyLogger;
	}

	public int getBodyLogLength() {
		return bodyLogLength;
	}

	public void setBodyLogLength(int bodyLogLength) {
		this.bodyLogLength = bodyLogLength;
	}

	public String getBodyLogMode() {
		return bodyLogMode;
	}

	public void setBodyLogMode(String bodyLogMode) {
		if (Arrays.asList(BODY_LOG_MODE).contains(bodyLogMode)) {
			this.bodyLogMode = bodyLogMode;
		} else {
			this.bodyLogMode = BLM_TRUNCATED;
		}
	}

	public boolean isSimplyMode() {
		return simplyMode;
	}

	public void setSimplyMode(boolean simplyMode) {
		this.simplyMode = simplyMode;
	}

	public AuditLogger getAuditLogger() {
		return auditLogger;
	}

	public void setAuditLogger(AuditLogger auditLogger) {
		this.auditLogger = auditLogger;
	}

	public String getStopWatchMode() {
		return stopWatchMode;
	}

	public void setStopWatchMode(String stopWatchMode) {
		if (Arrays.asList(STOPWATCH_MODES).contains(stopWatchMode)) {
			this.stopWatchMode = stopWatchMode;
		} else {
			this.stopWatchMode = SWM_SIMPLY;
		}
	}

	/**
	 * 获得会话秒表
	 *
	 * @return {@link StopWatch }
	 * @author Tequila
	 * @date 2022/07/19 23:49
	 */
	public static StopWatch getSessionStopWatch() {
		StopWatch sw = sessionStopWatch.get();
		if (sw == null) {
			sw = new StopWatch(RequestContextFactory.getContext().getRequestId());
			sessionStopWatch.set(sw);
		}

		return sw;
	}

	/**
	 * 启动秒表
	 *
	 * @param taskName 任务名称
	 * @author Tequila
	 * @date 2022/07/20 10:14
	 */
	public static void startStopWatch(String taskName) {
		getSessionStopWatch().start(taskName);
	}

	/**
	 * 启动秒表
	 *
	 * @author Tequila
	 * @date 2022/07/20 10:15
	 */
	public static void startStopWatch() {
		getSessionStopWatch().start();
	}

	/**
	 * 停止秒表
	 *
	 * @author Tequila
	 * @date 2022/07/20 10:15
	 */
	public static void stopStopWatch() {
		getSessionStopWatch().stop();
	}
}
