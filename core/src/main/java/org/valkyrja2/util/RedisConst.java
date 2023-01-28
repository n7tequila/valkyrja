package org.valkyrja2.util;

/**
 * redis 常量
 *
 * @author Tequila
 * @create 2022/11/16 14:52
 **/
public class RedisConst {
	
	public static final String REDIS_TEMPLATE = "redisTemplate";
	
	public static final String STRING_REDIS_TEMPLATE = "stringRedisTemplate";
	
	public static final String BYTES_REDIS_TEMPLATE = "bytesRedisTemplate";

	private RedisConst() {
		throw new IllegalStateException("Const class");
	}
}
