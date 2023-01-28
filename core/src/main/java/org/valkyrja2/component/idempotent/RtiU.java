package org.valkyrja2.component.idempotent;

import org.valkyrja2.component.idempotent.exception.DuplicateRequestException;
import org.valkyrja2.component.idempotent.exception.IdempotentException;

import java.util.concurrent.TimeUnit;

/**
 * Request Token Idempotent Utils
 *
 * @author Tequila
 * @create 2022/08/05 12:04
 **/
public final class RtiU {

    /**
     * 幂等
     *
     * @param token  令牌
     * @param wait   等待
     * @param expire 到期
     * @param unit   单位
     * @return {@link Idempotent }
     * @author Tequila
     * @date 2022/08/05 12:07
     */
    public static Idempotent idempotent(String token, long wait, long expire, TimeUnit unit) throws IdempotentException {
        return (new RedissonIdempotent(token)).acquire(wait, expire, unit);
    }

    /**
     * 幂等
     *
     * @param token 令牌
     * @return {@link Idempotent }
     * @throws DuplicateRequestException 复制请求异常
     * @author Tequila
     * @date 2022/08/05 12:09
     */
    public static Idempotent idempotent(String token) throws IdempotentException {
        return (new RedissonIdempotent(token)).acquire();
    }

    /**
     * 幂等
     *
     * @param token  令牌
     * @param expire 到期
     * @return {@link Idempotent }
     * @throws DuplicateRequestException 复制请求异常
     * @author Tequila
     * @date 2022/08/05 12:09
     */
    public static Idempotent idempotent(String token, long expire) throws IdempotentException {
        return (new RedissonIdempotent(token)).acquire(expire);
    }

    private RtiU() {
        throw new IllegalStateException("Factory class");
    }
}
