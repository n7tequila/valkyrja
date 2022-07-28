/*
 * PROJECT valkyrja2
 * util/ValidateUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串校验工具类
 *
 * @author Tequila
 *
 */
public class ValidateUtils {

	private static final Logger log = LoggerFactory.getLogger(ValidateUtils.class);

	private ValidateUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 判断字符串是否是email地址
	 *
	 * @param s 需要判断的email地址
	 * @return boolean 如果email地址格式正确则返回true，否则返回false
	 * @author Tequila
	 * @date 2022/06/28 14:30
	 */
	public static boolean isEmail(String s) {
		String emailCheck = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern regex = Pattern.compile(emailCheck, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(s);
        return matcher.matches();
	}

	/**
	 * 按照字符串长度(15,18,17+X)判断输入的字符串是否是身份证号码
	 *
	 * @param s 需要判断的字符串
	 * @return boolean 如果格式正确则返回true，格式错误则返回false
	 * @author Tequila
	 * @date 2022/06/28 14:30
	 */
	public static boolean isIdNo(String s){
		String idNumber = "^\\d{15}(\\d{2}[0-9xX])?$";
		Pattern regex = Pattern.compile(idNumber, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(s);
        return matcher.matches();
	}
}
