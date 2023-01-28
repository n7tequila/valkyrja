package org.valkyrja2.component.idempotent;

/**
 * 幂等配置对象
 *
 * @author Tequila
 * @create 2022/08/05 10:35
 **/
public final class IdempotentConfig {

    /** 等待 */
    private long wait;

    /** 到期 */
    private long expire;

    /** 模式 */
    private String mode;

    public IdempotentConfig() {
        wait = IdempotentConst.DEFAULT_WAIT;
        expire = IdempotentConst.DEFAULT_EXPIRE;
        mode = IdempotentConst.DEFAULT_MODE;
    }

    public IdempotentConfig(long wait, long expire, String mode) {
        this.wait = wait;
        this.expire = expire;
        this.mode = mode;
    }

    public long getWait() {
        return wait;
    }

    public void setWait(long wait) {
        this.wait = wait;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
