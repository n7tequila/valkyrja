/*
 * PROJECT valkyrja2
 * util/ThreadUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.valkyrja2.util.exception.InterruptedRuntimeException;

/**
 * 线程工具包
 *
 * @author Tequila
 * @create 2022/06/28 14:32
 **/
public class ThreadUtils {
	
	private ThreadUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Thread.sleep方法，但是不会抛出InterruptedException。但是如果raise为true，则会抛出一个RuntimeException。
	 *
	 * @param millis 毫秒
	 * @param raise  true如果sleep抛出InterruptedException，则会抛出一个Run
	 * @throws InterruptedRuntimeException 中断运行时异常
	 * @author Tequila
	 * @date 2022/06/28 14:41
	 */
	public static void sleep(long millis, boolean raise) throws InterruptedRuntimeException {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			if (raise) throw new InterruptedRuntimeException(e);
		}
	}

	/**
	 * Thread.sleep方法，但是不会抛出InterruptedException
	 *
	 * @param millis 米尔斯
	 * @author Tequila
	 * @date 2022/06/28 14:34
	 */
	public static void sleep(long millis) {
		sleep(millis, false);
	}
}
