/*
 * PROJECT valkyrja2
 * core/BizIdempotentManager.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.idempotent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.valkyrja2.component.idempotent.exception.DuplicateRequestRuntimeException;
import org.valkyrja2.component.idempotent.exception.IdempotentRuntimeException;
import org.valkyrja2.mvc.spring.SpringUtils;
import org.valkyrja2.util.StringUtils;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * 业务逻辑幂等管理工具
 *
 * @author Tequila
 * @create 2022/07/14 21:07
 **/
public class BizIdempotentManager {
	private static final Logger log = LoggerFactory.getLogger(BizIdempotentManager.class);

	/** redis key 前缀 */
	private static final String BIZ_IDEMPOTENT_PREFIX = "$it:biz:";

	/* ========== Singleton define ========== */
    private static volatile BizIdempotentManager _instance;
    private BizIdempotentManager() {
    	init();
    }
    public static BizIdempotentManager getInstance() {
    	if (_instance == null) {
    		synchronized (BizIdempotentManager.class) {
    			if (_instance == null) {
    				_instance = new BizIdempotentManager();
    			}
    		}
    	}
    	return _instance;
    }

	/* ========== END Singleton define ========== */

	/** 默认超时（5分钟） */
	private static final long DEFAULT_EXPIRE = 5L * 60;

    /** 基于当前线程的token，每个线程只能有一个 */
    private ThreadLocal<Stack<String>> threadToken;

	/** RedisTemplate */
	private StringRedisTemplate redisTemplate;

	/** 默认到期 */
	private long defaultExpire;

	/**
	 * 初始化
	 *
	 * @author Tequila
	 * @date 2022/04/08 20:48
	 */
	public void init() {
		log.debug("BizIdempotentManager init.");

        threadToken = new NamedThreadLocal<>(BizIdempotentManager.class.getName());
        this.defaultExpire = DEFAULT_EXPIRE;
    }

	/**
	 * 开始事务处理
	 *
	 * @param token 令牌
	 * @param expire 到期
	 * @param unit   单位
	 * @param errMsg 犯错消息
	 * @author Tequila
	 * @date 2022/04/08 23:41
	 */
	public void beginTrans(String token, long expire, TimeUnit unit, String errMsg) {
		/*
		 * 先判断本地有没有
		 * 再判断redis中有没有
		 */
		if (sessionTokenExists(token) || redisTokenExistsAndSet(token, expire, unit)) {
			throw new DuplicateRequestRuntimeException(errMsg);
		}
		if (log.isDebugEnabled()) log.debug("BizIdempotentManager.startTrans({})", token);
	}

	/**
	 * 开始事务处理
	 *
	 * @param token  令牌
	 * @param expire 到期时间（秒）
	 * @author Tequila
	 * @date 2022/04/09 20:40
	 */
	public void beginTrans(String token, long expire) {
		beginTrans(token, expire, TimeUnit.SECONDS, null);
	}

	public void beginTrans(String token) {
		beginTrans(token, defaultExpire, TimeUnit.SECONDS, null);
	}

	public void beginTrans(String token, String errMsg) {
		beginTrans(token, defaultExpire, TimeUnit.SECONDS, errMsg);
	}

	/**
	 * 是否在事务处理中
	 *
	 * @param token 令牌
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/15 14:54
	 */
	public boolean isInTrans(String token) {
		return sessionTokenExists(token);
	}

	/**
	 * 是否在事务处理中
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/07/15 14:54
	 */
	public boolean isInTrans() {
		return threadToken.get() != null;
	}

	/**
	 * 令牌存在
	 *
	 * 如果<code>threadToken.get()</code>空，或者如果<code>threadToken.get()</code>
	 * 不为空，但是其中的Set不包含token都认为当前token没有使用
	 *
	 * @param token 令牌
	 * @return boolean
	 * @author Tequila
	 * @date 2022/04/09 19:41
	 */
	private boolean sessionTokenExists(String token) {
		return (threadToken.get() != null && threadToken.get().contains(token));
	}

	/**
	 * redis 令牌存在
	 *
	 * @param token 令牌
	 * @return boolean
	 * @author Tequila
	 * @date 2022/04/09 20:34
	 */
	private boolean redisTokenExistsAndSet(String token, long expire, TimeUnit unit) {
		String key = BIZ_IDEMPOTENT_PREFIX + token;
		Boolean result = getRedisTemplate().opsForValue().setIfAbsent(key, "", expire, unit);
		if (Boolean.TRUE.equals(result)) {
			putToken(token);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 设置令牌
	 *
	 * @param token 令牌
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/04/09 20:36
	 */
	private void putToken(String token) {
		Stack<String> tokenStack;
		if (threadToken.get() == null) {
			tokenStack = new Stack<>();
			threadToken.set(tokenStack);
		} else {
			tokenStack = threadToken.get();
		}

		tokenStack.push(token);
	}

	/**
	 * 释放最后一个token
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/04/09 20:56
	 */
	public String release() {
		Stack<String> tokenStack = threadToken.get();
		try {
			if (tokenStack != null && !tokenStack.isEmpty()) {
				String lastToken = tokenStack.pop();
				log.debug("BizIdempotentManager.release(), token {}", lastToken);
				String key = BIZ_IDEMPOTENT_PREFIX + lastToken;
				getRedisTemplate().delete(key);

				return lastToken;
			}

			throw new IdempotentRuntimeException("Idempotent session not start");
		} finally {
			if (tokenStack != null && tokenStack.isEmpty()) threadToken.remove();
		}
	}

	/**
	 * 释放指定的token
	 *
	 * @param token 令牌
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/04/09 21:05
	 */
	public boolean release(String token) {
		Stack<String> tokenStack = threadToken.get();
		try {
			/* 1.先从堆栈中判断是否存在，先清空堆栈中的数据 */
			if (tokenStack != null && !tokenStack.isEmpty() && tokenStack.contains(token)) {
				tokenStack.remove(token);
				log.debug("BizIdempotentManager.release({})", token);
			}

			/* 再删除redis中的数据，如果redis也没有，则跳出 */
			if (delRedisKey(token)) {
				return true;
			}
		} finally {
			if (tokenStack != null && tokenStack.isEmpty()) threadToken.remove();
		}

		return false;
	}

	/**
	 * 释放所有token
	 *
	 * @author Tequila
	 * @date 2022/04/09 21:08
	 */
	public void releaseAll() {
		Stack<String> tokenStack = threadToken.get();
		try {
			if (tokenStack != null && !tokenStack.isEmpty()) {
				while (!tokenStack.isEmpty()) {
					String lastToken = tokenStack.pop();  // 堆栈pop最后进去的一个token
					delRedisKey(lastToken);
				}
			}
		} finally {
			threadToken.remove();
		}
	}


	/**
	 * 合并令牌
	 *
	 * @param args arg游戏
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/04/10 00:10
	 */
	public String concatToken(String...args) {
		return StringUtils.concat(args, ":");
	}

	/**
	 * 删除redis key
	 *
	 * @param token 令牌
	 * @author Tequila
	 * @date 2022/04/09 22:16
	 */
	private boolean delRedisKey(String token) {
		String key = BIZ_IDEMPOTENT_PREFIX + token;
		return Boolean.TRUE.equals(getRedisTemplate().delete(key));
	}
	/**
	 * 获取RedisTemplate
	 *
	 * @return {@link StringRedisTemplate }
	 * @author Tequila
	 * @date 2022/04/09 20:21
	 */
	private StringRedisTemplate getRedisTemplate() {
		if (this.redisTemplate == null) {
			StringRedisTemplate locRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
			if (locRedisTemplate == null) {
				locRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
			}
			this.redisTemplate = locRedisTemplate;
		}
		return this.redisTemplate;
	}

	public void setRedisTemplate(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public long getDefaultExpire() {
		return defaultExpire;
	}

	public void setDefaultExpire(long defaultExpire) {
		this.defaultExpire = defaultExpire;
	}
}
