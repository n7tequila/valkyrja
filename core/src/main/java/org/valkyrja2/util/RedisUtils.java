package org.valkyrja2.util;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.valkyrja2.mvc.spring.SpringUtils;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class RedisUtils {

	/**
	 * 默认随机失效比例
	 */
	private static final BigDecimal DEFAULT_RANDOM_EXPIRE_PERCENT = BigDecimal.valueOf(0.1);

	private static ThreadLocal<RedisTemplate<String, String>> redisTemplates = new ThreadLocal<>();

	/**
	 * 获取一个缓存在threadLocal中的RedisTemplate对象
	 *
	 * @return {@link RedisTemplate<String, String> }
	 * @author Tequila
	 * @date 2022/03/02 14:54
	 */
	public static synchronized RedisTemplate<String, String> getThreadRedisTemplate() {
		RedisTemplate<String, String> local = redisTemplates.get();
		if (local == null) {
			local = SpringUtils.getBean(RedisConst.STRING_REDIS_TEMPLATE);
			redisTemplates.set(local);
		}
		
		return local;
	}

	/**
	 * 获取RedisTemplate
	 *
	 * @return {@link RedisTemplate<String, String> }
	 * @author Tequila
	 * @date 2022/04/08 21:36
	 */
	public static RedisTemplate<String, String> getRedisTemplate() {
		return SpringUtils.getBean(RedisConst.REDIS_TEMPLATE);
	}

	/**
	 * 得到StringRedisTemplate
	 *
	 * @return {@link StringRedisTemplate }
	 * @author Tequila
	 * @date 2022/04/08 21:37
	 */
	public static StringRedisTemplate getStringRedisTemplate() {
		return SpringUtils.getBean(RedisConst.STRING_REDIS_TEMPLATE);
	}

	/**
	 * 释放一个缓存在threadLocal中的RedisTemplate对象
	 *
	 * @author Tequila
	 * @date 2022/03/02 14:53
	 */
	public static void releaseThreadRedisTemplate() {
		redisTemplates.remove();
	}

	/**
	 * 设置nx
	 *
	 * @param redisTemplate RedisTemplate
	 * @param redisKey      redis key
	 * @param value         值
	 * @param expire        到期
	 * @return {@link Boolean }
	 * @author Tequila
	 * @date 2022/03/02 14:51
	 */
	@SuppressWarnings("unchecked")
	public static Boolean setNX(RedisTemplate<String, String> redisTemplate, String redisKey, String value, long expire) {
		if (redisTemplate == null) redisTemplate = getThreadRedisTemplate();
		
		return (Boolean) redisTemplate.execute((RedisCallback) connection -> {
			String val = value;
			if (val == null)  val = String.valueOf(System.currentTimeMillis());
			boolean acquire = connection.setNX(redisKey.getBytes(), val.getBytes());
			if (acquire == true && expire > 0) {
				connection.expire(redisKey.getBytes(), expire);
			}
			
			return acquire;
		});
	}

	/**
	 * 设置nx
	 *
	 * @param redisTemplate RedisTemplate
	 * @param redisKey      redis key
	 * @param value         值
	 * @return {@link Boolean }
	 * @author Tequila
	 * @date 2022/03/02 14:51
	 */
	public static Boolean setNX(RedisTemplate<String, String> redisTemplate, String redisKey, String value) {
		return setNX(redisTemplate, redisKey, value, 0);
	}

	/**
	 * 设置nx
	 *
	 * @param redisKey 复述,关键
	 * @param value    值
	 * @param expire   到期
	 * @return {@link Boolean }
	 * @author Tequila
	 * @date 2022/03/02 14:49
	 */
	public static Boolean setNX(String redisKey, String value, long expire) {
		return setNX(getThreadRedisTemplate(), redisKey, value, expire);
	}

	/**
	 * 设置nx
	 *
	 * @param redisKey redis key
	 * @param value    价值
	 * @return {@link Boolean }
	 * @author Tequila
	 * @date 2022/03/02 14:51
	 */
	public static Boolean setNX(String redisKey, String value) {
		return setNX(getThreadRedisTemplate(), redisKey, value, 0);
	}

	/**
	 * 随机超时
	 *
	 * @param baseTimeout 基础超时（秒）
	 * @return long
	 * @author Tequila
	 * @date 2022/03/02 14:58
	 */
	public static long randomTimeout(long baseTimeout) {
		long incTimeout = DEFAULT_RANDOM_EXPIRE_PERCENT.multiply(BigDecimal.valueOf(baseTimeout)).longValue();
		return randomTimeout(baseTimeout, incTimeout);
	}

	/**
	 * 生成随机超时数值
	 *
	 * @param baseTimeout 基础超时
	 * @param timeUnit    时间单位
	 * @return long 
	 * @author Tequila
	 * @date 2022/03/02 14:40
	 */
	public static long randomTimeout(long baseTimeout, TimeUnit timeUnit) {
		baseTimeout = TimeUnit.SECONDS.convert(baseTimeout, timeUnit);
		long incTimeout = DEFAULT_RANDOM_EXPIRE_PERCENT.multiply(BigDecimal.valueOf(baseTimeout)).longValue();

		return randomTimeout(baseTimeout, incTimeout);
	}

	/**
	 * 生成随机超时数值（秒）
	 *
	 * @param baseTimeout 基础超时
	 * @param randomScope 随机范围
	 * @return long
	 * @author Tequila
	 * @date 2022/03/02 14:36
	 */
	public static long randomTimeout(long baseTimeout, long randomScope) {
		long actualAddTimeout = MersenneTwisterUtils.random(randomScope);
		return baseTimeout + actualAddTimeout;
	}
	
	private RedisUtils() {
	    throw new IllegalStateException("Utility class");
	}

}
