package org.valkyrja2.component.idempotent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.valkyrja2.component.idempotent.exception.IdempotentException;
import org.valkyrja2.component.redis.RedisFactory;
import org.valkyrja2.util.ThreadUtils;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:dev/test-context.xml", "classpath:redis-context.xml"})
@WebAppConfiguration
class RtiUTest {

    private static final Logger log = LoggerFactory.getLogger(RtiUTest.class);


    @Test
    void testAcquire() throws InterruptedException, IdempotentException {
        try (Idempotent idempotent = RtiU.idempotent("token", 100L)) {
            Thread.sleep(500);
        }
//        RLock lock = RedisFactory.getRedissonClient().getLock("test");
//        boolean result = lock.tryLock(0, 30, TimeUnit.SECONDS);
//        assertTrue(result);
    }

}