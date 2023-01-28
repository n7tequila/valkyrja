package org.valkyrja2.component.idempotent;

import org.redisson.api.RLock;
import org.valkyrja2.component.idempotent.exception.DuplicateRequestException;
import org.valkyrja2.component.idempotent.exception.IdempotentException;
import org.valkyrja2.component.redis.RedisFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Tequila
 * @create 2022/08/05 10:07
 **/
public class RedissonIdempotent extends Idempotent {

    private RLock lock;

    public RedissonIdempotent(String tokenName) {
        super(tokenName);
    }

    public RedissonIdempotent(String tokenName, IdempotentConfig config) {
        super(tokenName, config);
    }

    @Override
    protected void init(String tokenName) {
        lock = getLock(tokenName, config.getMode());
    }

    @Override
    public Idempotent acquire(long wait, long expire, TimeUnit unit) throws IdempotentException {
        try {
            if (lock.tryLock(wait, expire,  unit)) {
                return this;
            } else {
                throw new DuplicateRequestException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IdempotentException("RedissonIdempotent acquire has been interrupted.", e);
        }
    }

    /**
     * 得到锁
     *
     * @param lockName 锁名字
     * @param mode     模式
     * @return {@link RLock }
     * @author Tequila
     * @date 2022/08/05 11:41
     */
    private RLock getLock(String lockName, String mode) {
        switch (mode) {
            case IdempotentConst.MODE_UNFAIR: return RedisFactory.getRedissonClient().getLock(lockName);
            case IdempotentConst.MODE_FAIR: return RedisFactory.getRedissonClient().getFairLock(lockName);
            case IdempotentConst.MODE_SPIN: return RedisFactory.getRedissonClient().getSpinLock(lockName);
            default: throw new IllegalArgumentException("mode must be unfair, fair or spin.");
        }
    }

    @Override
    public void release() {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlockAsync();
        }
    }
}
