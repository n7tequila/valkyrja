/*
 * PROJECT valkyrja2
 * util/LogFormatUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.function.Function;

/**
 * @author Tequila
 * @create 2022/05/25 08:23
 **/
public class LogFormatUtils {

    /**
     * 格式化value对象
     * 默认以{@code toString()}
     *
     * @param value 需要格式化的对象
     * @param len 最大长度
     * @param limitLength 是否需要对格式化字符串进行缩减
     * @return the formatted value
     */
    public static String formatValue(Object value, int len, boolean limitLength) {
        if (value == null) {
            return "";
        }
        String str;
        if (value instanceof CharSequence) {
            str = "\"" + value + "\"";
        }
        else {
            try {
                str = value.toString();
            } catch (Throwable ex) {
                str = ex.toString();
            }
        }
        return (limitLength && str.length() > len ? str.substring(0, len) + " (truncated)..." : str);
    }

    /**
     * Use this to log a message with different levels of detail (or different
     * messages) at TRACE vs DEBUG log levels. Effectively, a substitute for:
     * <pre class="code">
     * if (logger.isDebugEnabled()) {
     *   String str = logger.isTraceEnabled() ? "..." : "...";
     *   if (logger.isTraceEnabled()) {
     *     logger.trace(str);
     *   }
     *   else {
     *     logger.debug(str);
     *   }
     * }
     * </pre>
     * @param logger the logger to use to log the message
     * @param messageFactory function that accepts a boolean set to the value
     * of {@link Log#isTraceEnabled()}
     */
    public static void traceDebug(Log logger, Function<Boolean, String> messageFactory) {
        if (logger.isDebugEnabled()) {
            String logMessage = messageFactory.apply(logger.isTraceEnabled());
            if (logger.isTraceEnabled()) {
                logger.trace(logMessage);
            } else {
                logger.debug(logMessage);
            }
        }
    }

    private LogFormatUtils() {
        throw new IllegalStateException("Utility class");
    }
}
