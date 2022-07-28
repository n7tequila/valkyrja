/*
 * PROJECT valkyrja2
 * util/PinyinUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 汉字转换为拼音工具类
 *
 * @author Tequila
 * @create 2022/06/29 01:43
 **/
public class PinyinUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PinyinUtils.class);
	
	private PinyinUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * (一个)中文 正则表达式 
	 */
	private static final String CHINESE_REGEX = "[\u4e00-\u9fa5]";

	/**
	 * 将指定字符串中的中文转为汉语拼音，其余非中文的部分不变 注:拼音小写
	 *
	 * @param originChinese 要转成拼音的中文
	 * @param abbreviation  是否简写 true, 【张三】转换为【zs】 false, 【张三】转换为【zhangsan】
	 * @param upperCase     拼音是否用大写输出
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/29 01:45
	 */
	public static String chinese2pinyin(String originChinese, boolean abbreviation, boolean upperCase) {
		// 将字符串 转换为 字符数组
		char[] chineseCharArray = originChinese.trim().toCharArray();
		// 设置转换格式
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		// 输出拼音全部小写(默认即为小写)
		defaultFormat.setCaseType(upperCase ? HanyuPinyinCaseType.UPPERCASE : HanyuPinyinCaseType.LOWERCASE);
		// 不带声调
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		/*
		 * 含ü的字有 【女】【吕】【略】【虐】等
		 *
		 * WITH_V 设置 【ü】 转换为 【v】 , 如:【女】 转换后为 【nv】 WITH_U_AND_COLON 设置 【ü】 转换为 【u:】,
		 * 如:【女】 转换后为 【nu:】 WITH_U_UNICODE 设置 【ü】 转换为 【ü】,即:原输出, 如:【女】 转换后为 【nü】
		 */
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		StringBuilder hanYuPinYinResult = new StringBuilder(6);
		boolean isChinese;
		String tempStr;
		for (char cStr : chineseCharArray) {
			isChinese = String.valueOf(cStr).matches(CHINESE_REGEX);
			// 如果字符是中文,则将中文转为汉语拼音
			if (isChinese) {
				try {
					tempStr = PinyinHelper.toHanyuPinyinStringArray(cStr, defaultFormat)[0];
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					log.warn(String.format("Can not convert %s to PINYIN", originChinese), e);
					return "";
				}
				tempStr = abbreviation ? tempStr.substring(0, 1) : tempStr;
				hanYuPinYinResult.append(tempStr);
			} else {
				// 如果字符不是中文,则不转换
				hanYuPinYinResult.append(cStr);
			}
		}
		return hanYuPinYinResult.toString();
	}
	
	public static String chinese2pinyin(String originChinese) {
		return chinese2pinyin(originChinese, true, false);
	}
}