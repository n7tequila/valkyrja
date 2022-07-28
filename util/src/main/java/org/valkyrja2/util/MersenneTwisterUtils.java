/*
 * PROJECT valkyrja2
 * util/MersenneTwisterUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.math3.random.MersenneTwister;

/**
 * 梅森旋转随机数算法工具包
 *
 * @author Tequila
 * @create 2022/05/21 10:55
 **/
public class MersenneTwisterUtils {

    /** 梅森旋转随机数生成对象，默认使用当前时间作为seed */
    private static final MersenneTwister mt = new MersenneTwister(System.currentTimeMillis());


    /**
     * 生成一个[0, end]范围的随机数
     *
     * @param end 结束范围
     * @return int 生成的随机数
     * @author Tequila
     * @date 2022/05/21 10:58
     */
    public static int random(int end) {
        return random(0, end);
    }

    /**
     * 生成一个[0, end]范围的长整型随机数
     *
     * @param end 结束范围
     * @return long 生成的随机数
     * @author Tequila
     * @date 2022/05/21 10:58
     */
    public static long random(long end) {
        return random(0L, end);
    }

    /**
     * 生成一个[start, end]范围的随机数
     *
     * @param start 开始
     * @param end   结束
     * @return int 生成的随机数
     * @author Tequila
     * @date 2022/05/21 10:58
     */
    public static int random(int start, int end) {
        return ((int) (mt.nextDouble() * end) + start);
    }

    /**
     * 生成一个[start, end]范围的长整形随机数
     *
     * @param start 开始范围
     * @param end   结束范围
     * @return long 生成的随机数
     * @author Tequila
     * @date 2022/05/21 10:58
     */
    public static long random(long start, long end) {
        return ((long) (mt.nextDouble() * end) + start);
    }

    private MersenneTwisterUtils() {
        throw new IllegalStateException("Utility class");
    }
}
