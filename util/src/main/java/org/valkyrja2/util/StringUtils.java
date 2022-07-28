/*
 * PROJECT valkyrja2
 * util/StringUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * String工具类
 *
 * @author Tequila
 * @create 2022/05/20 22:24
 **/
public class StringUtils {

    /** 随机字符集（字符大小写） */
    public static final String RANDOM_CHAR_SYMBOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** 随机字符集（数字） */
    public static final String RANDOM_NUMBER_SYMBOL = "1234567890";

    /**
     * 随机字符集（符号）<br>
     * 不包含',",\,/
     */
    public static final String RANDOM_SIGN_SYMBOL = "~!@#$%^&*()_+{}|:<>?`-=[];,.";

    /** 随机字符集（字符 + 数字） */
    public static final String RANDOM_SYMBOL = RANDOM_CHAR_SYMBOL + RANDOM_NUMBER_SYMBOL;

    /** 随机字符集（字符 + 数字 + 符号） */
    public static final String RANDOM_FULL_SYMBOL = RANDOM_SYMBOL + RANDOM_SIGN_SYMBOL;


    /** 空白字符串 */
    private static final String BLANK_STR = "";

    /** 默认脱敏遮罩字符 */
    public static final String DEFAULT_MASK = "*";

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 按照给定的次数重复排列某一个或几个字符
     *
     * @param s      需要重复排列的字符串
     * @param repeat 需要重复的次数
     * @return {@link String 排列后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:15
     */
    public static String repeat(String s, int repeat) {
        if (isBlank(s) || 0 == repeat) return BLANK_STR;  // 如果数据参数的s是空或者repeat是0次，则直接返回空白字符串

        StringBuilder sb = new StringBuilder();
        sb.append(s);
        for (int i = 0; i < repeat - 1; i++) {
            sb.append(s);
        }

        return sb.toString();
    }


    /**
     * 以suffix作为基础，按照给定的次数重复排列某一个或几个字符，并补足长度
     *
     * @param s      需要重复排列的字符串
     * @param repeat 需要重复的次数
     * @param suffix 尾部基础字符串
     * @return {@link String 排列后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:15
     */
    public static String repeat(String s, int repeat, String suffix) {
        return repeat(s, repeat - suffix.length()) + suffix;
    }

    /**
     * 判断字符串是否为空<br>
     * 如果给定的字符串是空白、空格、tab、回车，则都返回 true
     *
     * @param s 需要判断的字符串
     * @return true 表示字符串为空，否则返回 false
     * @author Tequila
     * @date 2022/05/21 10:16
     */
    public static boolean isBlank(String s) {
        if (s != null && !s.isEmpty()) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c != '\t' && c != '\n' && c != ' ' && c != '\r') {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param s 需要判断的字符串
     * @return true 表示字符串为空，否则返回 false
     * @author Tequila
     * @date 2022/05/21 10:17
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * 判断字符串前段与后端是否有空格，有空格则去除
     *
     * @param s 需要判断的字符串
     * @return {@link String 如果字符串为空返回 null，否则返回处理后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:18
     */
    public static String trim2null(String s) {
        return s.isEmpty() ? null : s.trim();
    }

    /**
     * 为数字填充长度，以满足创建某些特殊字符的需要<br>
     * 例如：fillInt(1, "0", 5);<br>
     * 返回值为：00001
     *
     * @param number 需要处理的数字
     * @param fill   需要填充的内容
     * @param length 总的生成的字符串长度
     * @return {@link String 填充后的结果}
     * @author Tequila
     * @date 2022/05/21 10:19
     */
    public static String fillInt(int number, String fill, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = length; i > String.valueOf(number).length(); i--) {
            sb.append(fill);
        }
        sb.append(number);
        return sb.toString();
    }

    /**
     * 根据输入的固定宽度裁剪字符串，并增加“...”表示延生长度<br/>
     * 如果源字符串长度小于裁剪长度，则不做操作
     *
     * @param str      需要进行简略操作的字符串
     * @param maxWidth 裁剪宽度
     * @return {@link String 简略后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:20
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, maxWidth, "...");
    }

    /**
     * 根据输入的固定宽度裁剪字符串，并增加suffix表示延生长度<br/>
     * 如果源字符串长度小于裁剪长度，则不做操作
     *
     * @param str      字符串
     * @param maxWidth 最大宽度
     * @param suffix   后缀
     * @return {@link String 简略后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:32
     */
    public static String abbreviate(String str, int maxWidth, String suffix) {
        if (str.length() > maxWidth) {
            return str.substring(0, maxWidth) + suffix;
        } else {
            return str;
        }
    }

    /**
     * 从InputStream中提取String内容
     *
     * @param in 需要提取的InputStream对象
     * @return {@link String 提取获得的String字符串}
     * @throws IOException IO操作错误抛出错误
     * @author Tequila
     * @date 2022/05/21 10:21
     */
    public static String inputStream2String(InputStream in) throws IOException {
        int ptr = 0;
        in = new BufferedInputStream(in);
        StringBuilder buffer = new StringBuilder();
        while ((ptr = in.read()) != -1) {
            buffer.append((char) ptr);
        }
        return buffer.toString();
    }

    /**
     * 判断字符串是不是全部小写
     *
     * @param s 需要判断的字符串
     * @return true 表示全部是小写，否则返回false
     * @author Tequila
     * @date 2022/05/21 10:22
     */
    public static boolean isLowerCase(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是不是全大写
     *
     * @param s 需要判断的字符串
     * @return true 表示全部是大写，否则返回false
     * @author Tequila
     * @date 2022/05/21 10:23
     */
    public static boolean isUpperCase(String s) {
        return !isLowerCase(s);
    }

    /**
     * 将byte数组转化成String<br>
     * 如果遇到转换错误，则直接用没有charset的方式进行转换
     *
     * @param bs             byte数组
     * @param defaultCharset 默认的字符集
     * @return {@link String 换行后的字符串结果}
     * @author Tequila
     * @date 2022/05/21 10:24
     */
    public static String byte2str(byte[] bs, String defaultCharset) {
        if (bs == null || 0 == bs.length) return BLANK_STR;

        try {
            return new String(bs, defaultCharset);
        } catch (UnsupportedEncodingException e) {
            return new String(bs);
        }
    }

    /**
     * 判断两个字符串是否一致，并返回相应的对象
     *
     * @param s1 字符串1
     * @param s2 字符串2
     * @param trueValue  真，返回值
     * @param falseValue 假，返回值
     * @return {@link Object 比较后的返回值}
     * @author Tequila
     * @date 2022/05/21 10:25
     */
    public static Object strCompare(String s1, String s2, Object trueValue, Object falseValue) {
        return s1.equals(s2) ? trueValue : falseValue;
    }

    /**
     * 连接多个字符串
     *
     * @param strings 需要拼接的字符串数组
     * @return {@link String 拼接字符串}
     * @author Tequila
     * @date 2022/05/21 10:26
     */
    public static String concat(String...strings) {
        if (strings == null || 0 == strings.length) return BLANK_STR;

        StringBuilder result = new StringBuilder();
        for (String s: strings) {
            if (s != null) result.append(s);
        }

        return result.toString();
    }

    /**
     * 连接多个字符串
     *
     * @param strings 需要拼接的字符串数组
     * @param split   拼接的分隔符
     * @return {@link String 拼接字符串}
     * @author Tequila
     * @date 2022/05/21 10:28
     */
    public static String concat(String[] strings, String split) {
        if (strings == null || 0 == strings.length) return BLANK_STR;

        StringBuilder result = new StringBuilder();
        for (String s: strings) {
            if (s != null) result.append(s).append(split);
        }

        String s = result.toString();
        return s.length() > 0 ? s.substring(0, s.length() - split.length()) : s;
    }

    /**
     * 将字符串转化为ascii的char值，再拼接成字符串
     *
     * @param s 需要转换的字符串
     * @return {@link String 转换后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:29
     */
    public static String str2number(String s) {
        if (StringUtils.isBlank(s)) return BLANK_STR;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                sb.append((int) s.charAt(i));
            } else {
                sb.append(s.charAt(i));
            }
        }

        return sb.toString();
    }

    /**
     * 证件号脱敏
     *
     * @param idno 证件号（15、18位）
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/20 22:26
     */
    public static String idnoMasking(String idno) {
        if (idno.length() == 18) {
            return commonStrMasking(idno, 5, 3);
        } else if (idno.length() == 15) {
            return commonStrMasking(idno, 5, 2);
        } else {
            return commonStrMasking(idno, 3, 3);
        }
    }

    /**
     * 银行卡脱敏
     *
     * @param bankCard 银行卡号
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/20 22:26
     */
    public static String bankCardMasking(String bankCard) {
        return commonStrMasking(bankCard, 4, 4);
    }

    /**
     * 手机号脱敏
     *
     * @param mobileNo 脱敏后的字符串
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:41
     */
    public static String mobileNoMasking(String mobileNo) {
        return commonStrMasking(mobileNo, 3, 4);
    }

    /**
     * 通用数据脱敏
     *
     * @param s 需要脱敏的字符串
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/20 22:26
     */
    public static String commonStrMasking(String s) {
        return commonStrMasking(s, DEFAULT_MASK);
    }

    /**
     * 通用数据脱敏
     *
     * @param s    需要脱敏的字符串
     * @param mask 遮罩字符串
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/21 11:55
     */
    public static String commonStrMasking(String s, String mask) {
        if (s.length() <= 4) {  // 小于4
            return s.charAt(0) + repeat(mask, s.length() - 1);
        } else {  // 大于4，取30%的字符串长度作为可显示部分
            int fix = (int) Math.round(s.length() * 0.3);
            return commonStrMasking(s, fix, fix, mask);
        }
    }

    /**
     * 通用字符串脱敏
     *
     * @param s      字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:37
     */
    public static String commonStrMasking(String s, int prefix, int suffix) {
        return commonStrMasking(s, prefix, suffix, DEFAULT_MASK);
    }

    /**
     * 通用字符串脱敏
     *
     * @param s      字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @param mask 重复字符串
     * @return {@link String 脱敏后的字符串}
     * @author Tequila
     * @date 2022/05/21 10:42
     */
    public static String commonStrMasking(String s, int prefix, int suffix, String mask) {
        if (s.length() > (prefix + suffix)) {
            return s.substring(0, prefix) + StringUtils.repeat(mask, s.length() - prefix - suffix) + s.substring(prefix + s.length() - prefix - suffix);
        } else  {
            return commonStrMasking(s);
        }
    }

    /**
     * 首字符大写
     *
     * @param s 需要处理的字符串
     * @return {@link String 首字符大写字符串}
     * @author Tequila
     * @date 2022/05/21 11:03
     */
    public static String uppercaseFirstChar(String s) {
        char[] cs = s.toCharArray();
        if (!Character.isUpperCase(cs[0])) {
            cs[0] -= 32;
            return String.valueOf(cs);
        } else {
            return s;
        }
    }

    /**
     * null转换成空字符<br>
     * 如果null则返回空字符串，否则返回原字符串
     *
     * @param s 需要处理的字符串
     * @return {@link String 转换后的字符串}
     * @author Tequila
     * @date 2022/05/21 11:04
     */
    public static String null2blank(String s) {
        if (s == null) {
            return BLANK_STR;
        } else {
            return s;
        }
    }

    /**
     * url encode，如果遇到错误则返回原url。
     *
     * @param url 需要处理的url
     * @return {@link String 转换后的url。}
     * @author Tequila
     * @date 2022/05/21 11:06
     */
    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * 生成一个定长的随机字符串
     *
     * @param length 长度
     * @param symbol 象征
     * @return {@link String }
     * @author Tequila
     * @date 2022/05/21 11:09
     */
    public static String randomStr(int length, String symbol) {
        if (length == 0 || StringUtils.isBlank(symbol)) return BLANK_STR;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = MersenneTwisterUtils.random(symbol.length());
            sb.append(symbol.charAt(pos));
        }

        return sb.toString();
    }

    /**
     * 生成一个定长的随机字符串
     *
     * @param length 随机数长度
     * @return {@link String 随机数字符串}
     * @author Tequila
     * @date 2022/05/21 11:15
     */
    public static String randomStr(int length) {
        return randomStr(length, RANDOM_SYMBOL);
    }

    /**
     * 生成一个定长的随机数字字符串
     *
     * @param length 随机数长度
     * @return {@link String 随机数字符串}
     * @author Tequila
     * @date 2022/05/21 11:15
     */
    public static String randomNumber(int length) {
        return randomStr(length, RANDOM_NUMBER_SYMBOL);
    }


    /**
     * 中文format对齐
     *
     * @param chs chs 中文字符串
     * @return {@link String 对齐后的字符串}
     * @author Tequila
     * @date 2022/03/23 16:44
     */
    public static String formatAlignChs(String chs, int length) {
        int ascCount = 0;
        int chsCount = 0;
        for (int i = 0; i < chs.length(); i++) {
            char c = chs.charAt(i);
            if (isChinese(c)) {
                chsCount++;
            } else {
                ascCount++;
            }
        }
        int offset = chsCount > 0 ? chsCount - 1 : 0;
        int format = length - chs.length() + ascCount;
        return "%-" + format + "s";
    }

    /**
     * 是否中文
     *
     * @param c 字符
     * @return true 表示是中文，否则返回 false
     * @author Tequila
     * @date 2022/05/21 11:16
     */
    public static boolean isChinese(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        return Character.UnicodeScript.HAN == sc;
    }
}
