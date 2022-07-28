/*
 * PROJECT valkyrja2
 * core/CachingResponse.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

public interface CachingResponse {

	/**
	 * 从可缓存的HttpResponse对象获取body的方法
	 *
	 * @return {@link byte[] }
	 * @author Tequila
	 * @date 2022/07/13 22:59
	 */
	byte[] getBody();
}
