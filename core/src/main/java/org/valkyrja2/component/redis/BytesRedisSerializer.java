/*
 * PROJECT valkyrja2
 * core/BytesRedisSerializer.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

public class BytesRedisSerializer implements RedisSerializer<byte[]> {

	@Override
	public byte[] serialize(@Nullable byte[] bytes) {
		return (bytes);
	}

	@Override
	public byte[] deserialize(@Nullable byte[] bytes) {
		return bytes;
	}

}
