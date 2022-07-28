/*
 * PROJECT valkyrja2
 * util/OSInfo.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

/**
 * 操作系统信息
 *
 * @author Tequila
 * @create 2022/06/29 01:42
 **/
public class OSInfo {

	/** 操作系统 */
	private static String os = System.getProperty("os.name").toLowerCase();
	
	private OSInfo() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean isLinux() {
		return os.contains("linux");
	}

	public static boolean isMacOS() {
		return os.contains("mac") && os.contains("os") && !os.contains("x");
	}

	public static boolean isMacOSX() {
		return os.contains("mac") && os.contains("os") && os.contains("x");
	}

	public static boolean isWindows() {
		return os.contains("windows");
	}

	public static boolean isOS2() {
		return os.contains("os/2");
	}

	public static boolean isSolaris() {
		return os.contains("solaris");
	}

	public static boolean isSunOS() {
		return os.contains("sunos");
	}

	public static boolean isMPEiX() {
		return os.contains("mpe/ix");
	}

	public static boolean isHPUX() {
		return os.contains("hp-ux");
	}

	public static boolean isAix() {
		return os.contains("aix");
	}

	public static boolean isOS390() {
		return os.contains("os/390");
	}

	public static boolean isFreeBSD() {
		return os.contains("freebsd");
	}

	public static boolean isIrix() {
		return os.contains("irix");
	}

	public static boolean isDigitalUnix() {
		return os.contains("digital") && os.contains("unix");
	}

	public static boolean isNetWare() {
		return os.contains("netware");
	}

	public static boolean isOSF1() {
		return os.contains("osf1");
	}

	public static boolean isOpenVMS() {
		return os.contains("openvms");
	}
}
