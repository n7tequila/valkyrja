package org.valkyrja2.component.idempotent;

import org.valkyrja2.component.idempotent.exception.DuplicateRequestException;
import org.valkyrja2.component.idempotent.exception.IdempotentException;
import org.valkyrja2.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 抽象幂等对象
 *
 * @author Tequila
 * @create 2022/08/05 10:52
 **/
public abstract class Idempotent implements AutoCloseable {

    private static final String FMT_TOKEN_NAME = "$rti:%s";

    private String tokenName;

    protected IdempotentConfig config;

    protected Idempotent(String tokenName) {
        this.config = IdempotentConfigLoader.getConfig();
        this.tokenName = tokenName;
        init(formatTokenName(tokenName));
    }

    protected Idempotent(String tokenName, IdempotentConfig config) {
        this.config = config;
        this.tokenName = tokenName;
        init(formatTokenName(tokenName));
    }

    /**
     * 初始化幂等对象
     *
     * @param tokenName 令牌名称
     * @author Tequila
     * @date 2022/08/05 11:39
     */
    protected abstract void init(String tokenName);

    /**
     * 请求幂等令牌
     *
     * @return boolean
     * @author Tequila
     * @date 2022/08/05 10:21
     */
    public Idempotent acquire() throws IdempotentException {
        return acquire(getConfig().getWait(), getConfig().getExpire(), IdempotentConst.DEFAULT_UNIT);
    }

    /**
     * 请求幂等令牌
     *
     * @param expire 自动释放时间
     * @return {@link Idempotent }
     * @throws DuplicateRequestException 复制请求异常
     * @author Tequila
     * @date 2022/08/05 12:10
     */
    public Idempotent acquire(long expire) throws IdempotentException {
        return acquire(getConfig().getWait(), expire, IdempotentConst.DEFAULT_UNIT);
    }

    /**
     * 请求幂等令牌
     *
     * @param expire 到期
     * @param unit   单位
     * @return {@link Idempotent }
     * @throws DuplicateRequestException 复制请求异常
     * @author Tequila
     * @date 2022/08/05 12:11
     */
    public Idempotent acquire(long expire, TimeUnit unit) throws IdempotentException {
        return acquire(getConfig().getWait(), expire, unit);
    }

    /**
     * 请求幂等令牌
     *
     * @param wait   等待
     * @param expire 到期
     * @param unit   单位
     * @return {@link Idempotent }
     * @throws DuplicateRequestException 复制请求异常
     * @author Tequila
     * @date 2022/08/05 12:11
     */
   public abstract Idempotent acquire(long wait, long expire, TimeUnit unit) throws IdempotentException;

    /**
     * 释放
     *
     * @author Tequila
     * @date 2022/08/05 12:20
     */
    public abstract void release();


    @Override
    public void close() {
        release();
    }

    /**
     * 格式标记名
     *
     * @param s 字符串
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/05 12:34
     */
    protected String formatTokenName(String s) {
        return String.format(FMT_TOKEN_NAME, s);
    }

    public IdempotentConfig getConfig() {
        return config;
    }

    public void setConfig(IdempotentConfig config) {
        this.config = config;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
