package org.valkyrja2.component.idempotent;

import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrja2.util.ConfigUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Tequila
 * @create 2022/08/05 11:03
 **/
public final class IdempotentConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(IdempotentConfigLoader.class);

    private static final String DEFAULT_SENTINEL_CONFIG_FILE = "classpath:idempotent.properties";
    public static final String PROP_WAIT = "idempotent.wait";
    public static final String PROP_EXPIRE = "idempotent.expire";
    public static final String PROP_MODE = "idempotent.mode";


    private static IdempotentConfig config;

    /* 初始化配置信息 */
    static {
        try {
            load();
        } catch (Exception e) {
            log.warn("[IdempotentConfigLoader] Failed to initialize configuration items", e);
        }
    }

    private static void load() {
        config = new IdempotentConfig();  //  默认配置

        Properties p = ConfigUtils.loadProperties(DEFAULT_SENTINEL_CONFIG_FILE);
        if (p != null && !p.isEmpty()) {
            log.info("[SentinelConfigLoader] Loading Sentinel config from {}", DEFAULT_SENTINEL_CONFIG_FILE);
            config.setWait(Long.parseLong(p.getProperty(PROP_WAIT, String.valueOf(IdempotentConst.DEFAULT_WAIT))));
            config.setExpire(Long.parseLong(p.getProperty(PROP_EXPIRE, String.valueOf(IdempotentConst.DEFAULT_EXPIRE))));
            config.setMode(p.getProperty(PROP_MODE, IdempotentConst.DEFAULT_MODE));
        }
    }

    public static IdempotentConfig getConfig() {
        return config;
    }

    private IdempotentConfigLoader() {
        throw new IllegalStateException("Factory class");
    }
}
