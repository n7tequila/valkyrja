package org.valkyrja2.component.idempotent;

import java.util.concurrent.TimeUnit;

/**
 * 幂等常量
 *
 * @author Tequila
 * @create 2022/08/05 10:27
 **/
public final class IdempotentConst {

    /** 默认获取锁等待时间 */
    public static final long DEFAULT_WAIT = 0L;

    /** 默认锁到期时间 */
    public static final long DEFAULT_EXPIRE = 3000L;

    /** 默认时间单位，秒 */
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    public static final String MODE_UNFAIR = "unfair";

    public static final String MODE_FAIR = "fair";

    public static final String MODE_SPIN = "spin";

    public static final String DEFAULT_MODE = MODE_UNFAIR;


    private IdempotentConst() {
        throw new IllegalStateException("Const class");
    }
}
