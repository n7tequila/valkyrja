/*
 * PROJECT valkyrja2
 * util/IDNoUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证号工具类
 *
 * @author Tequila
 * @create 2022/06/28 16:52
 **/
public class IDNoUtils {

    private static final Logger log = LoggerFactory.getLogger(IDNoUtils.class);

    public static final String IDNO_MUST_NOT_BE_NULL = "idno must not be null";

    private IDNoUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 是否有效身份证号
     *
     * @param idno 身份证号，支持18位、15位和港澳台的10位
     * @return boolean
     * @author Tequila
     * @date 2022/06/28 16:55
     */
    public static boolean isValid(String idno) {
        Objects.requireNonNull(idno, IDNO_MUST_NOT_BE_NULL);

        idno = idno.trim();
        int length = idno.length();
        switch (length) {
            // 18位身份证
            case 18:
                return isValid18(idno);
            // 15位身份证
            case 15:
                return isValid15(idno);
            // 10位身份证，港澳台地区
            case 10: {
                String[] val = isValid10(idno);
                return null != val && val[2].equals("true");
            }
            default:
                return false;
        }
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idno 15位身份编码
     * @return 18位身份编码
     */
    public static String convertIdNo(String idno) {
        Objects.requireNonNull(idno, IDNO_MUST_NOT_BE_NULL);

        StringBuilder idNo18;
        if (idno.length() != CHINA_ID_MIN_LENGTH) {
            return null;
        }
        if (isMatch(NUMBERS, idno)) {
            // 获取出生年月日
            String birthday = idno.substring(6, 12);
            Date birthDate = strToDate(birthday, YY_MM_DD);
            // 获取出生年
            int sYear = year(birthDate);
            // 理论上2000年之后不存在15位身份证，可以不要此判断
            if (sYear > 2000) {
                sYear -= 100;
            }
            idNo18 = new StringBuilder().append(idno, 0, 6).append(sYear).append(idno.substring(8));
            // 获取校验位
            char sVal = getCheckCode18(idNo18.toString());
            idNo18.append(sVal);
        } else {
            return null;
        }
        return idNo18.toString();
    }

    /**
     * 从身份证号码中获取生日日期，只支持15或18位身份证号码
     *
     * @param idno 身份证号码
     * @return {@link Date }
     * @author Tequila
     * @date 2022/06/28 16:57
     */
    public static Date getBirthDate(String idno) {
        Objects.requireNonNull(idno, IDNO_MUST_NOT_BE_NULL);

        final String birthByIdCard = getBirth(idno);
        return null == birthByIdCard ? null : strToDate(birthByIdCard, YYYY_MM_DD);
    }

    /**
     * 根据身份编号获取年龄，只支持15或18位身份证号码
     *
     * @param idno 身份证号
     * @return 年龄
     */
    public static int getAgeByCard(String idno) {
        String birth = getBirth(idno);
        return getAge(strToDate(birth, YYYY_MM_DD), new Date());
    }

    /**
     * 根据出生日期计算在某个日期的年龄
     *
     * @param birthDay      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int getAge(Date birthDay, Date dateToCompare) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToCompare);

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("Birthday is after date " + dateToCompare + " !");
        }

        //先获取指定日期的年月日
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        //获取出生日期的年月
        cal.setTime(birthDay);
        int age = year - cal.get(Calendar.YEAR);
        final int monthBirth = cal.get(Calendar.MONTH);

        if (month == monthBirth) {
            //获取出生日期的日
            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            // 如果生日在当月，但是未达到生日当天的日期，年龄减一(判断是否是最后一天是为了去除润月的影响)
            if ((!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth) {
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }


    /**
     * 根据身份证号码获取生日年，只支持15或18位身份证号码
     *
     * @param idno 身份编号
     * @return 生日(yyyy)
     */
    public static Short getYearByIdCard(String idno) {
        Objects.requireNonNull(idno, IDNO_MUST_NOT_BE_NULL);

        final int len = idno.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idno = convertIdNo(idno);
        }
        return Short.valueOf(idno.substring(6, 10));
    }

    /**
     * 根据身份证号码获取生日月，只支持15或18位身份证号码
     *
     * @param idno 身份编号
     * @return 生日(MM)
     */
    public static Short getMonthByIdCard(String idno) {
        final int len = idno.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idno = convertIdNo(idno);
        }
        return Short.valueOf(idno.substring(10, 12));
    }

    /**
     * 根据身份证号码获取生日天，只支持15或18位身份证号码
     *
     * @param idno 身份编号
     * @return 生日(dd)
     */
    public static Short getDayByIdCard(String idno) {
        final int len = idno.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idno = convertIdNo(idno);
        }
        return Short.valueOf(idno.substring(12, 14));
    }

    /**
     * 根据身份证号码获取性别，只支持15或18位身份证号码
     *
     * @param idno 身份编号
     * @return 性别(1 : 男 ， 0 : 女)
     */
    public static int getGender(String idno) {
        if (idno == null || idno.trim().length() == 0) {
            throw new IllegalArgumentException("ID Card is must not null");
        }
        final int len = idno.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            throw new IllegalArgumentException("ID Card length must be 15 or 18");
        }

        if (len == CHINA_ID_MIN_LENGTH) {
            idno = convertIdNo(idno);
        }
        char sCardChar = idno.charAt(16);
        return (sCardChar % 2 != 0) ? 1 : 0;
    }

    /**
     * 根据身份证号码获取户籍省份，只支持15或18位身份证号码
     *
     * @param idno 身份编码
     * @return 省级编码。
     */
    public static String getProvince(String idno) {
        int len = idno.length();
        if (len == CHINA_ID_MIN_LENGTH || len == CHINA_ID_MAX_LENGTH) {
            String sProvinNum = idno.substring(0, 2);
            return CITY_CODES.get(sProvinNum);
        }
        return null;
    }

    /**
     * 判断18位身份证的合法性
     *
     * 公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成，排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * 第1、2位数字表示：所在省份的代码；第3、4位数字表示：所在城市的代码；第5、6位数字表示：所在区县的代码，第7~14位数字表示：出生年、月、日
     * 第15、16位数字表示：所在地的派出所的代码；第17位数字表示性别：奇数表示男性，偶数表示女性
     * 第18位数字是校检码，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示
     * 第十八位数字(校验码)的计算方法为：
     *
     * @param idno 待验证的身份证
     * @return 是否有效的18位身份证
     */
    private static boolean isValid18(String idno) {
        if (CHINA_ID_MAX_LENGTH != idno.length()) {
            return false;
        }

        //校验生日
        if (!isBirthday(idno.substring(6, 14))) {
            return false;
        }

        // 前17位
        String code17 = idno.substring(0, 17);
        // 第18位
        char code18 = Character.toLowerCase(idno.charAt(17));
        if (isMatch(NUMBERS, code17)) {
            // 获取校验位
            char val = getCheckCode18(code17);
            return val == code18;
        }
        return false;
    }

    /**
     * 验证15位身份编码是否合法
     *
     * @param idno 身份编码
     * @return 是否合法
     */
    private static boolean isValid15(String idno) {
        if (CHINA_ID_MIN_LENGTH != idno.length()) {
            return false;
        }
        if (isMatch(NUMBERS, idno)) {
            // 省份
            String proCode = idno.substring(0, 2);
            if (null == CITY_CODES.get(proCode)) {
                return false;
            }

            // 校验生日（两位年份，补充为19XX）
            return isBirthday("19" + idno.substring(6, 12));
        } else {
            return false;
        }
    }

    /**
     * 验证10位身份编码是否合法
     *
     * @param idno 身份编码
     * @return 身份证信息数组
     * [0] - 台湾、澳门、香港 [1] - 性别(男M,女F,未知N) [2] - 是否合法(合法true,不合法false) 若不是身份证件号码则返回null
     */
    private static String[] isValid10(String idno) {
        if (idno == null || idno.trim().length() == 0) {
            return null;
        }
        String[] info = new String[3];
        String card = idno.replaceAll("[()]", "");
        if (card.length() != 8 && card.length() != 9 && idno.length() != 10) {
            return null;
        }
        // 台湾
        if (idno.matches("^[a-zA-Z][0-9]{9}$")) {
            info[0] = "台湾";
            String char2 = idno.substring(1, 2);
            if (char2.equals("1")) {
                info[1] = "M";
            } else if (char2.equals("2")) {
                info[1] = "F";
            } else {
                info[1] = "N";
                info[2] = "false";
                return info;
            }
            info[2] = isValidTWCard(idno) ? "true" : "false";
        } else if (idno.matches("^[157][0-9]{6}\\(?[0-9A-Z]\\)?$")) {
            // 澳门
            info[0] = "澳门";
            info[1] = "N";
        } else if (idno.matches("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$")) {
            // 香港
            info[0] = "香港";
            info[1] = "N";
            info[2] = isValidHKCard(idno) ? "true" : "false";
        } else {
            return null;
        }
        return info;
    }

    /**
     * 验证台湾身份证号码
     *
     * @param idno 身份证号码
     * @return 验证码是否符合
     */
    private static boolean isValidTWCard(String idno) {
        if (idno == null || idno.trim().length() == 0) {
            return false;
        }
        String start = idno.substring(0, 1);
        Integer iStart = TW_PREFIX_CODE.get(start);
        if (null == iStart) {
            return false;
        }
        String mid = idno.substring(1, 9);
        String end = idno.substring(9, 10);
        int sum = iStart / 10 + (iStart % 10) * 9;
        final char[] chars = mid.toCharArray();
        int iflag = 8;
        for (char c : chars) {
            sum += Integer.parseInt(String.valueOf(c)) * iflag;
            iflag--;
        }
        return (sum % 10 == 0 ? 0 : (10 - sum % 10)) == Integer.valueOf(end);
    }

    /**
     * 验证香港身份证号码
     * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35 最后一位校验码为0-9的数字加上字符"A"，"A"代表10
     * 将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效
     * @param idno 身份证号码
     * @return 验证码是否符合
     */
    private static boolean isValidHKCard(String idno) {
        String card = idno.replaceAll("[()]", "");
        int sum;
        if (card.length() == 9) {
            sum = (Character.toUpperCase(card.charAt(0)) - 55) * 9 + (Character.toUpperCase(card.charAt(1)) - 55) * 8;
            card = card.substring(1, 9);
        } else {
            sum = 522 + (Character.toUpperCase(card.charAt(0)) - 55) * 8;
        }
        String start = idno.substring(0, 1);
        Integer iStart = HK_PREFIX_CODE.get(start);
        if (null == iStart) {
            return false;
        }
        String mid = card.substring(1, 7);
        String end = card.substring(7, 8);
        char[] chars = mid.toCharArray();
        int iflag = 7;
        for (char c : chars) {
            sum = sum + Integer.parseInt(String.valueOf(c)) * iflag;
            iflag--;
        }
        if ("A".equalsIgnoreCase(end)) {
            sum += 10;
        } else {
            sum += Integer.parseInt(end);
        }
        return sum % 11 == 0;
    }

    /**
     * 根据身份编号获取生日，只支持15或18位身份证号码
     *
     * @param idno 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirth(String idno) {
        final int len = idno.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            idno = convertIdNo(idno);
        }
        return idno.substring(6, 14);
    }

    /**
     * 获得18位身份证校验码
     * 计算方式：
     * 将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 将这17位数字和系数相乘的结果相加
     * 用加出来和除以11，看余数是多少
     * 余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2
     * 通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2
     * @param code17 18位身份证号中的前17位
     * @return 第18位
     */
    private static char getCheckCode18(String code17) {
        int sum = getPowerSum(code17.toCharArray());
        return getCheckCode18(sum);
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum 加权和
     * @return 校验位
     */
    private static char getCheckCode18(int iSum) {
        switch (iSum % 11) {
            case 10:
                return '2';
            case 9:
                return '3';
            case 8:
                return '4';
            case 7:
                return '5';
            case 6:
                return '6';
            case 5:
                return '7';
            case 4:
                return '8';
            case 3:
                return '9';
            case 2:
                return 'x';
            case 1:
                return '0';
            case 0:
                return '1';
            default:
                return SPACE;
        }
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr 身份证号码的数组
     * @return 身份证编码
     */
    private static int getPowerSum(char[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                iSum += Integer.parseInt(String.valueOf(iArr[i])) * power[i];
            }
        }
        return iSum;
    }

    /**
     * 根据日期获取年
     *
     * @param date 日期
     * @return 年的部分
     */
    public static int year(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.YEAR);
    }

    /**
     * 验证是否为生日<br>
     * 只支持以下几种格式：
     * <ul>
     * <li>yyyyMMdd</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>yyyy年MM月dd日</li>
     * </ul>
     *
     * @param value 值
     * @return 是否为生日
     */
    public static boolean isBirthday(CharSequence value) {
        if (isMatch(BIRTHDAY, value)) {
            Matcher matcher = BIRTHDAY.matcher(value);
            if (matcher.find()) {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(3));
                int day = Integer.parseInt(matcher.group(5));
                return isBirthday(year, month, day);
            }
        }
        return false;
    }

    /**
     * 验证是否为生日
     *
     * @param year  年，从1900年开始计算
     * @param month 月，从1开始计数
     * @param day   日，从1开始计数
     * @return 是否为生日
     */
    public static boolean isBirthday(int year, int month, int day) {
        // 验证年
        int thisYear = year(new Date());
        if (year < 1900 || year > thisYear) {
            return false;
        }

        // 验证月
        if (month < 1 || month > 12) {
            return false;
        }

        // 验证日
        if (day < 1 || day > 31) {
            return false;
        }
        // 检查几个特殊月的最大天数
        if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
            return false;
        }
        if (month == 2) {
            // 在2月，非闰年最大28，闰年最大29
            return day < 29 || (day == 29 && isLeapYear(year));
        }
        return true;
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    private static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    /**
     * 将字符串转换成指定格式的日期
     *
     * @param str        日期字符串.
     * @param dateFormat 日期格式. 如果为空，默认为:yyyy-MM-dd HH:mm:ss.
     * @return
     */
    private static Date strToDate(final String str, String dateFormat) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        try {
            if (dateFormat == null || dateFormat.length() == 0) {
                dateFormat = DATE_FORMAT;
            }
            DateFormat fmt = new SimpleDateFormat(dateFormat);
            return fmt.parse(str.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    private static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }


    /**
     * 中国公民身份证号码最小长度。
     */
    private static final int CHINA_ID_MIN_LENGTH = 15;
    /**
     * 中国公民身份证号码最大长度。
     */
    private static final int CHINA_ID_MAX_LENGTH = 18;
    /**
     * 每位加权因子
     */
    private static final int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /** 省市代码表 */
    private static final Map<String, String> CITY_CODES = new HashMap<>();
    /** 台湾身份首字母对应数字 */
    private static final Map<String, Integer> TW_PREFIX_CODE = new HashMap<>();
    /** 香港身份首字母对应数字 */
    private static final Map<String, Integer> HK_PREFIX_CODE = new HashMap<>();

    /** 默认日期模板 */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD = "yyyyMMdd";
    private static final String YY_MM_DD = "yyMMdd";
    public static final char SPACE = ' ';
    /**
     * 数字
     */
    public static final Pattern NUMBERS = Pattern.compile("\\d+");
    /**
     * 生日
     */
    public static final Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$");

    static {
        CITY_CODES.put("11", "北京");
        CITY_CODES.put("12", "天津");
        CITY_CODES.put("13", "河北");
        CITY_CODES.put("14", "山西");
        CITY_CODES.put("15", "内蒙古");
        CITY_CODES.put("21", "辽宁");
        CITY_CODES.put("22", "吉林");
        CITY_CODES.put("23", "黑龙江");
        CITY_CODES.put("31", "上海");
        CITY_CODES.put("32", "江苏");
        CITY_CODES.put("33", "浙江");
        CITY_CODES.put("34", "安徽");
        CITY_CODES.put("35", "福建");
        CITY_CODES.put("36", "江西");
        CITY_CODES.put("37", "山东");
        CITY_CODES.put("41", "河南");
        CITY_CODES.put("42", "湖北");
        CITY_CODES.put("43", "湖南");
        CITY_CODES.put("44", "广东");
        CITY_CODES.put("45", "广西");
        CITY_CODES.put("46", "海南");
        CITY_CODES.put("50", "重庆");
        CITY_CODES.put("51", "四川");
        CITY_CODES.put("52", "贵州");
        CITY_CODES.put("53", "云南");
        CITY_CODES.put("54", "西藏");
        CITY_CODES.put("61", "陕西");
        CITY_CODES.put("62", "甘肃");
        CITY_CODES.put("63", "青海");
        CITY_CODES.put("64", "宁夏");
        CITY_CODES.put("65", "新疆");
        CITY_CODES.put("71", "台湾");
        CITY_CODES.put("81", "香港");
        CITY_CODES.put("82", "澳门");
        CITY_CODES.put("91", "国外");

        TW_PREFIX_CODE.put("A", 10);
        TW_PREFIX_CODE.put("B", 11);
        TW_PREFIX_CODE.put("C", 12);
        TW_PREFIX_CODE.put("D", 13);
        TW_PREFIX_CODE.put("E", 14);
        TW_PREFIX_CODE.put("F", 15);
        TW_PREFIX_CODE.put("G", 16);
        TW_PREFIX_CODE.put("H", 17);
        TW_PREFIX_CODE.put("J", 18);
        TW_PREFIX_CODE.put("K", 19);
        TW_PREFIX_CODE.put("L", 20);
        TW_PREFIX_CODE.put("M", 21);
        TW_PREFIX_CODE.put("N", 22);
        TW_PREFIX_CODE.put("P", 23);
        TW_PREFIX_CODE.put("Q", 24);
        TW_PREFIX_CODE.put("R", 25);
        TW_PREFIX_CODE.put("S", 26);
        TW_PREFIX_CODE.put("T", 27);
        TW_PREFIX_CODE.put("U", 28);
        TW_PREFIX_CODE.put("V", 29);
        TW_PREFIX_CODE.put("X", 30);
        TW_PREFIX_CODE.put("Y", 31);
        TW_PREFIX_CODE.put("W", 32);
        TW_PREFIX_CODE.put("Z", 33);
        TW_PREFIX_CODE.put("I", 34);
        TW_PREFIX_CODE.put("O", 35);

        // 来自http://shenfenzheng.bajiu.cn/?rid=40
        // 持证人拥有香港居留权
        HK_PREFIX_CODE.put("A", 1);
        // 持证人所报称的出生日期或地点自首次登记以后，曾作出更改
        HK_PREFIX_CODE.put("B", 2);
        // 持证人登记领证时在香港的居留受到入境事务处处长的限制
        HK_PREFIX_CODE.put("C", 3);
        // 持证人所报的姓名自首次登记以后，曾作出更改
        HK_PREFIX_CODE.put("N", 14);
        // 持证人报称在香港、澳门及中国以外其他地区或国家出生
        HK_PREFIX_CODE.put("O", 15);
        // 持证人拥有香港入境权
        HK_PREFIX_CODE.put("R", 18);
        // 持证人登记领证时在香港的居留不受入境事务处处长的限制
        HK_PREFIX_CODE.put("U", 21);
        // 持证人报称在澳门地区出生
        HK_PREFIX_CODE.put("W", 23);
        // 持证人报称在中国大陆出生
        HK_PREFIX_CODE.put("X", 24);
        // 持证人报称在香港出生
        HK_PREFIX_CODE.put("Z", 26);
    }
}
