/*
 * PROJECT valkyrja2
 * util/DateUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 日期工具类
 *
 * @author Tequila
 * @create 2022/06/27 23:21
 **/
public class DateUtils {

    /* ========== 以下是所有快速时间格式的常量定义 ==========*/

    public static final String DATE_FORMAT_SIMPLE = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_SHORT_SIMPLE = "yyyyMMddHHmm";

    public static final String DATE_FORMAT_EN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SHORT_EN = "yyyy-MM-dd";
    public static final String DATE_FORMAT_EX_EN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DATE_FORMAT_CN = "yyyy年M月d日 HH时mm分ss秒";
    public static final String DATE_FORMAT_SHORT_CN = "yyyy年M月d日";
    public static final String DATE_FORMAT_EX_CN = "yyyy年M月d日 HH时mm分ss秒SSS毫秒";

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** 默认Local */
    private static Locale defaultLocal = Locale.ENGLISH;

    /**
     * 解析字符串，生成<code>java.util.Date</code><br>
     * 标准日期时间格式为"yyyy-MM-dd HH:mm:ss"<br>
     *
     * @param value 需要解析的字符串
     * @return 从字符串进行解析得到的Date。如果发生错误，则返回null
     * @throws ParseException 给出的解析字符串格式错误
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2date(String value) throws ParseException {
        return str2date(DATE_FORMAT_EN, value);
    }

    /**
     * 解析字符串，生成<code>java.util.Date</code><br>
     * 使用标准的短日期格式"yyyy-MM-dd"
     *
     * @param value 需要解析的字符串
     * @return {@link Date }
     * @throws ParseException 解析异常
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2shortDate(String value) throws ParseException {
        return str2date(DATE_FORMAT_SHORT_EN, value);
    }

    /**
     * 解析字符串，生成<code>java.util.Date</code><br>
     * 标准日期时间格式为"yyyy-MM-dd HH:mm:ss"<br>
     *
     * @param value 需要解析的字符串
     * @param defaultValue 如果格式错误，则返回默认值
     * @return 从字符串进行解析得到的Date。如果发生错误，则返回null
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2date(String value, Date defaultValue) {
        try {
            return str2date(value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    /**
     * 用给定的日期和时间格式解析字符串，生成<code>java.util.Date</code>
     *
     * @param pattern 符合Java标准日期、时间转换格式的的字符串
     * @param value   需要解析的字符串
     * @return 从字符串进行解析得到的Date。如果发生错误，则返回null
     * @throws ParseException 给出的解析字符串格式错误
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2date(String pattern, String value) throws ParseException {
        return str2date(pattern, value, false);
    }

    /**
     * 用给定的日期和时间格式解析字符串，生成<code>java.util.Date</code>。并且可控制转换宽容度。
     *
     * @param pattern 符合Java标准日期、时间转换格式的的字符串
     * @param value   需要解析的字符串
     * @param lenient 转换宽容度，false为严格转换。例如：2013-01-32认为是错误的日期格式。
     * @return 从字符串进行解析得到的Date。如果发生错误，则返回null
     * @throws ParseException 给出的解析字符串格式错误
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2date(String pattern, String value, boolean lenient) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(pattern, defaultLocal);
        df.setLenient(lenient);
        return df.parse(value);
    }

    /**
     * 用给定的日期和时间格式解析字符串，生成<code>java.util.Date</code>
     *
     * @param pattern 符合Java标准日期、时间转换格式的的字符串
     * @param value   需要解析的字符串
     * @param defaultValue 如果格式错误，则返回默认值
     * @return 从字符串进行解析得到的Date。如果发生错误，则返回null
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date str2date(String pattern, String value, Date defaultValue) {
        try {
            return str2date(pattern, value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    /**
     * 将一个Date格式化日期字符串（不包含毫秒）<br>
     * 默认使用"yyyy-MM-dd"格式转换
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatShortDate(Date date) {
        return formatDate(DATE_FORMAT_SHORT_EN, date);
    }

    /**
     * 将一个Date格式化中文日期字符串（不包含毫秒）<br>
     * 默认使用"yyyy年M月d日"格式转换
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatShortDateCN(Date date) {
        return formatDate(DATE_FORMAT_SHORT_CN, date);
    }

    /**
     * 将一个Date格式化日期/时间字符串（不包含毫秒）
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatDate(Date date) {
        return formatDate(DATE_FORMAT_EN, date);
    }

    /**
     * 将一个Date格式化中文日期/时间字符串（不包含毫秒）
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatDateCN(Date date) {
        return formatDate(DATE_FORMAT_CN, date);
    }

    /**
     * 将一个Date格式化日期/时间字符串（包含毫秒）
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatDateEx(Date date) {
        return formatDate(DATE_FORMAT_EX_EN, date);
    }

    /**
     * 将一个Date格式化中文日期/时间字符串（包含毫秒）
     *
     * @param date 要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatDateExCN(Date date) {
        return formatDate(DATE_FORMAT_EX_CN, date);
    }

    /**
     * 给定转换格式，将一个Date格式化日期/时间字符串（不包含毫秒）
     *
     * @param pattern 符合Java标准日期、时间转换格式的的字符串
     * @param date    要格式化为时间字符串的时间值
     * @return 已格式化的时间字符串
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static String formatDate(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, defaultLocal);
        return sdf.format(date);
    }

    /**
     * 确定给定的年份是否为闰年。
     *
     * @param year 需要判断的年份
     * @return 如果给定的年份为闰年，则返回 true；否则返回 false
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static boolean isLeapYear(int year) {
        GregorianCalendar cal = new GregorianCalendar();
        return cal.isLeapYear(year);
    }

    /**
     * 确定给定的年份是否为闰年。
     *
     * @param year 需要判断的年份字符串。
     * @return 如果给定的年份为闰年，则返回 true；否则返回 false。
     * @throws IllegalArgumentException 如果给出的年份字符串格式错误。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static boolean isLeapYear(String year) throws IllegalArgumentException {
        int intYear = Integer.parseInt(year);
        return isLeapYear(intYear);
    }

    /**
     * 确定给定的日期的年份是否为闰年。
     *
     * @param date 需要判断的日期
     * @return 如果给定的年份为闰年，则返回 true；否则返回 false
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static boolean isLeapYear(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.isLeapYear(cal.get(Calendar.YEAR));
    }

    /**
     * 根据日历规则，将指定的（有符号的）时间量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param field  日历字段
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @throws IllegalArgumentException 如果 field 为
     *                                  ZONE_OFFSET、DST_OFFSET，或未知，或者任何日历字段在
     *                                  non-lenient 模式下具有超出范围的值
     *
     * @see java.util.Calendar
     * @see java.util.GregorianCalendar
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incDate(Date date, int field, int amount) throws IllegalArgumentException {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 根据日历规则，将指定的（有符号的）年份量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incYear(Date date, int amount) {
        return incDate(date, Calendar.YEAR, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）月份量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incMonth(Date date, int amount) {
        return incDate(date, Calendar.MONTH, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）约分量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incDay(Date date, int amount) {
        return incDate(date, Calendar.DATE, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）小时量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incHour(Date date, int amount) {
        return incDate(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）分钟量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incMinute(Date date, int amount) {
        return incDate(date, Calendar.MINUTE, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）秒钟量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incSecond(Date date, int amount) {
        return incDate(date, Calendar.SECOND, amount);
    }

    /**
     * 根据日历规则，将指定的（有符号的）毫秒量添加到给定的日历字段中。
     *
     * @param date   需要操作的时间值
     * @param amount 为字段添加的日期或时间量
     * @return 表示时间值的 Date
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static Date incMilliSecond(Date date, int amount) {
        return incDate(date, Calendar.MILLISECOND, amount);
    }

    /**
     * 计算两个日期时间值之间的秒差。<br>
     * 如果第一个日期大于第二个日期，则返回正值；小于则返回负值；两个日期相等则返回0。
     *
     * @param d1 第一个时间值。
     * @param d2 第二个时间值。
     * @return 两个时间值之间的毫秒差
     * @see #dateBetween(int, Date, Date)
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static long dateBetween(Date d1, Date d2) {
        return dateBetween(Calendar.MILLISECOND, d1, d2);
    }

    /**
     * 根据制定的日历字段，计算两个时间值之间的差。<br>
     * <ul>
     * <li>两个时间值之间的秒差<br>
     * dateBetween(Calendar.MINUTE, '2012-01-01 12:34:02', '2012-01-01
     * 12:33:55');<br>
     * 返回 7</li>
     * <li>两个时间值之间的年差<br>
     * dateBetween(Calendar.YEAR, '2012-01-01', '2013-01-01');<br>
     * 返回 -1</li>
     * <li>两个时间值之间的分钟差<br>
     * dateBetween(Calendar.MINUTE, '2012-01-01 12:33:50', '2012-01-01
     * 12:33:55');<br>
     * 返回 0</li>
     * </ul>
     *
     * @param field 日历字段
     * @param d1    第一个时间值。
     * @param d2    第二个时间值。
     * @return 两个时间值之间的差。
     * @throws IllegalArgumentException 如果 field 不是
     *                                  YEAR、MONTH、DATE、HOUR、MINUTE、SECOND、MILLISECOND中的一个
     * @see java.util.Calendar
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static long dateBetween(int field, Date d1, Date d2) throws IllegalArgumentException {
        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();
        c1.setTime(d1);
        c2.setTime(d2);
        long result = (c1.getTime().getTime() - c2.getTime().getTime());
        /**
         * 判断field是 “年 月 日 时 分 秒 毫秒”中的哪个一个。
         * 如果为天“Calendar.DATE”则先后除以24小时，60分钟，60秒，1000毫秒。
         * 如果为小时“Calendar.HOUR”则先后除以60分钟,60秒,1000毫秒。以下同理
         */
        switch (field) {
            case Calendar.YEAR:
                result = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
                break;

            case Calendar.MONTH:
                result = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
                result = 12 * result;
                result = result + (c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH));
                break;

            case Calendar.DATE:
                result = result / 24;

            case Calendar.HOUR:
                result = result / 60;

            case Calendar.MINUTE:
                result = result / 60;

            case Calendar.SECOND:
                result = result / 1000;

            case Calendar.MILLISECOND:

                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 计算某一个时间值是否在另两个时间值之间。<br>
     * <ul>
     * <li>小于第一个时间值<br>
     * dateIntRange('2012-01-01', '2012-02-01', '2012-03-01');<br>
     * 返回 -1</li>
     * <li>大于第二个时间值<br>
     * dateIntRange('2012-04-01', '2012-02-01', '2012-03-01');<br>
     * 返回 1</li>
     * <li>在两个时间值之间<br>
     * </li> dateIntRange('2012-02-21', '2012-02-01', '2012-03-01');<br>
     * 返回 0
     * </ul>
     *
     * @param date 需要判断的时间值。
     * @param d1   第一个时间值。
     * @param d2   第二个时间值。
     * @return 如果 date 小于第一个时间值则返回 -1，大于第二个时间值返回 1，在两个时间值之间返回 0。
     * @throws IllegalArgumentException 如果第二个时间值小于第一个时间值
     * @see java.util.Calendar
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int dateInRange(Date date, Date d1, Date d2) throws IllegalArgumentException {
        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();
        Calendar booleanCalendar = new GregorianCalendar();
        c1.setTime(d1);
        c2.setTime(d2);
        booleanCalendar.setTime(date);
        int judge = 0;
        /**
         * 比较d1与d2如果d2时间值小于d1的值则抛出IllegalArgumentException。
         * 如果所需要判断的时间值date（“booleanCalendar”）小于d1（“c1”）怎么返回-1 。
         * 如果所需要判断的时间值date（“booleanCalendar”）大于d2（“c2”）怎么返回1 。
         * 如果所需要判断的时间值date（“booleanCalendar”）大于d1（“c1”）并且小于d2（“c2”）怎么返回0 。
         */
        if (c1.compareTo(c2) > 0) {
            throw new IllegalArgumentException();
        } else if (booleanCalendar.compareTo(c1) < 0) {
            judge = -1;
        } else if (booleanCalendar.compareTo(c2) > 0) {
            judge = 1;
        } else if (booleanCalendar.compareTo(c1) > 0 && booleanCalendar.compareTo(c2) < 0) {
            judge = 0;
        }
        return judge;
    }

    /**
     * 获取当前时间相对应的日历字段的值。
     *
     * @param field 日历字段。
     * @return 返回给定日历字段的值。
     * @throws IllegalArgumentException 如果 field 字段取值不在标准日历字段范围内
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurDateField(int field) throws IllegalArgumentException {
        Calendar c = new GregorianCalendar();
        return field == Calendar.MONTH ? c.get(field) + 1 : c.get(field);
    }

    /**
     * 获取当前时间的年份值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurYear() {
        return getCurDateField(Calendar.YEAR);
    }

    /**
     * 获取当前时间的月份值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurMonth() {
        return getCurDateField(Calendar.MONTH);
    }

    /**
     * 获取当前时间的天值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurDay() {
        return getCurDateField(Calendar.DATE);
    }

    /**
     * 获取当前时间的小时值（取值返回0-12）。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurHour() {
        return getCurDateField(Calendar.HOUR);
    }

    /**
     * 获取当前时间的小时值（取值返回0-24）。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurHourOfDay() {
        return getCurDateField(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间的分钟值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurMinute() {
        return getCurDateField(Calendar.MINUTE);
    }

    /**
     * 获取当前时间的秒钟值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurSecond() {
        return getCurDateField(Calendar.SECOND);
    }

    /**
     * 获取当前时间的毫秒值。
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurMillisecond() {
        return getCurDateField(Calendar.MILLISECOND);
    }

    /**
     * 获取当前是一年中的第几天
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurDayOfYear() {
        return getCurDateField(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取当前时间是今年中的第几周
     *
     * @return 返回给定日历字段的值。
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static int getCurWeekOfYear() {
        return getCurDateField(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 将Date对象转换成LocalDateTime
     *
     * @param date 需要转换的日期
     * @return  {@link LocalDateTime } 转换后的LocalDateTime对象
     * @since jdk 8
     * @author Tequila
     * @date 2022/06/27 23:22
     */
    public static LocalDateTime date2localDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将Date对象转换成LocalDate
     *
     * @param date 需要转换的日期
     * @return {@link LocalDate } 转换后的LocalDateTime对象
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static LocalDate date2localDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取当前时间是今年中的第几周。 <br>
     * 按照中华人民共和国习惯，周一为每周第一天计算。
     *
     * @return 返回当年的第几周。
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static int getChnWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取给定时间是给定时间所在年份中的第几周。 <br>
     * 按照中华人民共和国习惯，周一为每周第一天计算。
     *
     * @param date 日期对象
     * @return 返回给定时间所在年份中的第几周。
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static int getChnWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取当前月最大天数
     *
     * @return 返回当前月最大天数。
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static int getMaxDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回当天只有日期的Date对象
     *
     * @return 日期对象
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static Date todayOnlyDate() {
        return onlyDate(new Date());
    }

    /**
     * 返回指定日期的Date对象
     *
     * @param date 需要处理的日期
     * @return 日期对象
     * @author Tequila
     * @date 2022/06/27 23:30
     */
    public static Date onlyDate(Date date) {
        String d = formatDate(DATE_FORMAT_SHORT_EN, date) + " 00:00:00.000";
        try {
            return str2date(d);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 返回今天的开始时间的日期对象<br>
     * 每天最开始的时间是yyyy-MM-dd 00:00:00
     *
     * @return {@link Date } 今天最早的日期对象
     * @author Tequila
     * @date 2022/06/27 23:33
     */
    public static Date todayStart() {
        String d = formatDate(DATE_FORMAT_SHORT_EN, new Date()) + " 00:00:00.000";
        try {
            return str2date(d);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 返回今天的结束时间的日期对象<br>
     * 每天结束的时间是yyyy-MM-dd 23:59:59
     *
     * @return {@link Date } 今天最晚的日期对象
     * @author Tequila
     * @date 2022/06/27 23:35
     */
    public static Date todayEnd() {
        String d = formatDate(DATE_FORMAT_SHORT_EN, new Date()) + " 23:59:59.999";
        try {
            return str2date(d);
        } catch (ParseException e) {
            return null;
        }
    }

    private static final String[] UPPERCASE_DATE = { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九",
            "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九",
            "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九",
            "三十", "三十一"};

    /**
     * 将指定的日期转换成为大写日期
     *
     * @param date 日期
     * @return {@link String } 大写日期的字符串
     * @author Tequila
     * @date 2022/06/27 23:36
     */
    public static String uppercaseDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String sYear = String.valueOf(year);
        String sMonth;
        String sDay;
        for (int i = 0; i < sYear.length(); i++) {
            if (Character.isDigit(sYear.charAt(i))) {
                String s = String.valueOf(sYear.charAt(i));
                sYear = sYear.replaceAll(s, UPPERCASE_DATE[Integer.parseInt(s)]);
            }
        }
        sMonth = UPPERCASE_DATE[month];
        sDay = UPPERCASE_DATE[day];

        return String.format("%s年%s月%s日", sYear, sMonth, sDay);
    }

    /**
     * 将timestamp转换成Date对象
     *
     * @param timestamp 时间戳
     * @return {@link Date } 转换后的Date对象
     * @author Tequila
     * @date 2022/06/27 23:37
     */
    public static Date timestamp2date(String timestamp) {
        if (timestamp.length() == 10) {
            return new Date(Long.parseLong(timestamp) * 1000);
        } else {
            return new Date(Long.parseLong(timestamp));
        }
    }

    /**
     * 将秒转换成hh:mm:ss的字符串描述
     *
     * @param d      double型时间数据，小数点前是秒，小数点后是毫秒
     * @param showMS 显示毫秒
     * @return {@link String } 转换后的时间数据
     * @author Tequila
     * @date 2022/06/28 09:32
     */
    public static String second2timeline(double d, boolean showMS) {
        String s = String.valueOf(d);
        long time;
        long ms;
        /* 判断字符串是否包含有小数，如果有的话拆分整数和小数 */
        if (s.contains(".")) {
            String[] part = s.split("\\.");
            time = Long.parseLong(part[0]);
            ms = Long.parseLong(part[1]);
        } else {
            time = Long.parseLong(s);
            ms  = 0;
        }
        long hh, mm, ss;
        hh = time / 3600;
        time -= hh * 3600;
        mm = time / 60;
        time -= mm * 60;
        ss = time;

        if (showMS) {
            return String.format("%02d:%02d:%02d.%d", hh, mm, ss, ms);
        } else {
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        }
    }

    /**
     * 将秒转换成hh:mm:ss的字符串描述<br>
     * 默认不显示小数点后的毫秒
     *
     * @param d double型时间数据，小数点前是秒，小数点后是毫秒
     * @return {@link String } 转换后的时间数据
     * @author Tequila
     * @date 2022/06/28 08:24
     */
    public static String second2timeline(double d) {
        return second2timeline(d, false);
    }

    /**
     * 收集当地日期
     *
     * @param start 开始
     * @param end   结束
     * @return {@link List }<{@link String }>
     * @author Tequila
     * @date 2022/07/15 22:20
     */
    public static List<String> collectLocalDates(LocalDate start, LocalDate end){
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localDate -> localDate.plusDays(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(LocalDate::toString)
                // 把流收集为List
                .collect(Collectors.toList());
    }

    /**
     * 收集当地日期
     *
     * @param year 年
     * @return {@link List }<{@link String }>
     * @author Tequila
     * @date 2022/07/15 22:21
     */
    public static List<String> collectLocalDates(int year){
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return collectLocalDates(start, end);
    }
}
