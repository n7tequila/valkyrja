package org.valkyrja2.component.async;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.valkyrja2.component.redis.RedisFactory;
import org.valkyrja2.util.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的异步存储工具
 *
 * @author Tequila
 * @create 2022/08/02 10:54
 **/
public class AsyncStorageManager {

    private static final Logger log = LoggerFactory.getLogger(AsyncStorageManager.class);
    private static volatile AsyncStorageManager _instance;
    private AsyncStorageManager() { init(); }
    public static AsyncStorageManager getInstance() {
        if (_instance == null) {
            synchronized (AsyncStorageManager.class) {
                if (_instance == null) {
                    _instance = new AsyncStorageManager();
                }
            }
        }
        return _instance;
    }

    /** 消费等待时间 */
    private static final long CONSUME_WAIT_TIME = 5L;

    /** 停止等待时间 */
    private static final long STOP_WAIT_TIME = 30L;

    /** 默认线程数量 */
    private static final int DEFAULT_THREAD_COUNT = 8;

    /** 异步存储队列 */
    private static final String ASYNC_QUEUE_PREFIX = "$ASYNC_STORAGE:";

    /** 异步消息确认队列 */
    private static final String ASYNC_QUEUE_ACK_PREFIX = "$ASYNC_STORAGE_ACK:";

    /** 异步消息错误队列 */
    private static final String ASYNC_QUEUE_ERROR_PREFIX = "$ASYNC_STORAGE_ERROR:";

    /** 线程名字 */
    public static final String THREAD_NAME = "AsyncStorageManager";

    /** sentinel IN/OUT 标记 */
    public static final String PROC_STORAGE_IN = "AsyncStorageManager.IN.";
    public static final String PROC_STORAGE_OUT = "AsyncStorageManager.OUT.";

    /** 完整队列名称 */
    private String fullQueueName;

    /** 使用端口作为队列名 */
    private boolean usePort = false;

    /** 使用队列名称，如果不适用，则直接使用服务器名称 */
    private boolean useQueueName = false;

    /** 是否使用汇总流量 */
    private boolean flowGroupBy = true;

    /** 队列名称 */
    private String queueName;

    /** 线程数 */
    private int threadCount = DEFAULT_THREAD_COUNT;

    ExecutorService subscribeThreadPool;

    /** subscribe 是否正在运行，初始数量为threadCount */
    private CountDownLatch running;

    /** RedisTemplate */
    private StringRedisTemplate redisTemplate;

    /** 处理程序映射 */
    private final Map<String, AsyncStorageHandler<?>> handlerMap = new HashMap<>();

    /**
     * 初始化所有变量，如果变量存在，则跳过初始化
     *
     * @author Tequila
     * @date 2022/08/11 21:40
     */
    private void init() {
        // nothing to do
    }

    /**
     * 发布一个消息队列
     *
     * @param obj 对象
     * @return boolean
     * @author Tequila
     * @date 2022/08/02 13:49
     */
    public boolean publish(Object obj) {
        String type = obj.getClass().getName();
        // 如果type没有定义，则抛出错误
        if (!handlerMap.containsKey(type)) {
            throw new IllegalStateException("Can not find AsyncStorageHandler for class " + type);
        }
        AsyncStorageHandler<?> handler = handlerMap.get(type);

        try (Entry entry = SphU.entry(formatFlowName(PROC_STORAGE_IN), EntryType.IN)) {
            log.info("========== 发布异步保存任务 - 开始 - {}", obj.getClass().getName());
            StorageInfo storageInfo = new StorageInfo(type, handler.toJson(obj));

            // 左进右出
            Long res = getRedisTemplate().opsForList().leftPush(fullQueueName(), storageInfo.toJson());
            return (res != null && res > 0);
        } catch (BlockException e) {
            return false;
        }
    }

    /**
     * 订阅消息队列
     *
     * @author Tequila
     * @date 2022/08/02 13:38
     */
    public void subscribe() {
        // 每次启动时，都把最后一次执行到一半的队列放回待处理队列
        unAckAll();
        if (subscribeThreadPool == null) {
            subscribeThreadPool = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory(THREAD_NAME));
            for (int i = 0; i < threadCount; i++) {
                subscribeThreadPool.execute(new SubscribeTask());
            }
            this.running = new CountDownLatch(threadCount);
        }
    }

    /**
     * 轮训所有的未执行任务，放回处理队列
     *
     * @author Tequila
     * @date 2022/08/02 15:54
     */
    public void unAckAll() {
        String unAckJson = null;
        do {
            unAckJson = getRedisTemplate().opsForList().rightPopAndLeftPush(fullAckQueueName(), fullQueueName());
        } while (unAckJson != null);
    }

    /**
     * 订阅是否正在运行
     *
     * @return boolean
     * @author Tequila
     * @date 2022/08/02 15:33
     */
    public boolean isSubscribeRunning() {
        return (this.running.getCount() >= 1L);
    }

    /**
     * 停止订阅
     *
     * @author Tequila
     * @date 2022/08/02 15:55
     */
    public void stopSubscribe() {
        if (this.subscribeThreadPool != null && isSubscribeRunning()) {
            log.info("========== 关闭异步存储队列 - 开始");

            this.subscribeThreadPool.shutdownNow();
            try {
                if (this.subscribeThreadPool.awaitTermination(STOP_WAIT_TIME, TimeUnit.SECONDS)) {
                    log.info("========== 关闭异步存储队列 - 成功");
                } else {
                    log.info("========== 关闭异步存储队列 - 超时");
                }
            } catch (InterruptedException e) {
                log.info("========== 关闭异步存储队列 - 被中断", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 获取状态
     *
     * @return {@link List }<{@link StoragePoolStatus }>
     * @author Tequila
     * @date 2022/08/04 17:48
     */
    public List<StoragePoolStatus> getStatus() {
        List<StoragePoolStatus> statusList = new ArrayList<>();

        Set<String> keys = getRedisTemplate().keys(ASYNC_QUEUE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                String serverName = key.substring(ASYNC_QUEUE_PREFIX.length(), key.length());
                Long size = getRedisTemplate().opsForList().size(key);
                statusList.add(new StoragePoolStatus(serverName, size));
            }
        }

        return statusList;
    }

    /**
     * 获取完整队列名称
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 17:13
     */
    private String fullQueueName() {
        return formatQueueName(ASYNC_QUEUE_PREFIX);
    }

    /**
     * 完整ack队列名称
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 17:37
     */
    private String fullAckQueueName() {
        return formatQueueName(ASYNC_QUEUE_ACK_PREFIX);
    }

    /**
     * 完整错误队列名称
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 17:37
     */
    public String fullErrQueueName() {
        return formatQueueName(ASYNC_QUEUE_ERROR_PREFIX);
    }

    /**
     * 得到完整队列名称
     *
     * @param prefix 前缀
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 17:52
     */
    private String formatQueueName(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        if (useQueueName) {
            sb.append(":").append(getQueueName());
        }
        sb.append(getServerName());
        return sb.toString();
    }

    /**
     * 格式流量监控名称
     *
     * @param prefix 前缀
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/16 10:59
     */
    private String formatFlowName(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(getQueueName());
        if (!flowGroupBy) {
            sb.append(".").append(getServerName());
        }
        return sb.toString();
    }

    /**
     * 获取RedisTemplate
     *
     * @return {@link StringRedisTemplate }
     * @author Tequila
     * @date 2022/08/12 00:02
     */
    private StringRedisTemplate getRedisTemplate() {
        StringRedisTemplate locRedisTemplate = this.redisTemplate;
        if (locRedisTemplate == null) {
            synchronized (this) {
                locRedisTemplate = this.redisTemplate;
                if (locRedisTemplate == null) {
                    this.redisTemplate = locRedisTemplate = RedisFactory.getStringRedisTemplate();
                }
            }
        }

        return locRedisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获得服务器名称
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 17:11
     */
    public String getServerName() {
        if (usePort) {
            return String.format("%s:%s", NetworkUtils.getHostName(), NetworkUtils.getHostPort());
        } else {
            return NetworkUtils.getHostName();
        }
    }

    /**
     * 获得队列名称，如果队列名称为空，则
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/08/15 16:48
     */
    public String getQueueName() {
        if (useQueueName) {
            String locQueueName = this.queueName;
            if (StringUtils.isBlank(locQueueName)) {
                synchronized (this) {
                    locQueueName = this.queueName;
                    if (StringUtils.isBlank(locQueueName)) {
                        this.queueName = locQueueName = StringUtils.randomStr(12);
                    }
                }
            }
            return locQueueName;
        } else {
            return "";
        }
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * 设置处理程序
     *
     * @author Tequila
     * @date 2022/08/12 00:09
     */
    public void setHandlers(List<AsyncStorageHandler<?>> handlers) {
        Objects.requireNonNull(handlers, "handlers must not be null.");

        handlers.forEach(handle -> handlerMap.put(handle.getName(), handle));
    }

    /**
     * 添加处理程序
     *
     * @param handler 处理程序
     * @author Tequila
     * @date 2022/08/12 00:11
     */
    public void addHandler(AsyncStorageHandler<?> handler) {
        Objects.requireNonNull(handler, "handler must not be null.");

        handlerMap.put(handler.getName(), handler);
    }

    Map<String, AsyncStorageHandler<?>> getHandlerMap() {
        return handlerMap;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isUseQueueName() {
        return useQueueName;
    }

    public void setUseQueueName(boolean useQueueName) {
        this.useQueueName = useQueueName;
    }

    public boolean isUsePort() {
        return usePort;
    }

    public boolean isFlowGroupBy() {
        return flowGroupBy;
    }

    public void setFlowGroupBy(boolean flowGroupBy) {
        this.flowGroupBy = flowGroupBy;
    }

    public void setUsePort(boolean usePort) {
        this.usePort = usePort;
    }

    /**
     * 订阅任务
     *
     * @author Tequila
     * @create 2022/08/02 15:55
     **/
    private static class SubscribeTask implements Runnable {

        @Override
        public void run() {
            log.info("========== 执行异步保存任务 - 开始 - {}", Thread.currentThread().getName());
            while (!Thread.currentThread().isInterrupted()) {
                StorageInfo storageInfo = consume();
                if (storageInfo != null) {
                    if (SphO.entry(getInstance().formatFlowName(PROC_STORAGE_OUT), EntryType.OUT)) {
                        try {
                            log.info("========== 执行异步保存任务 - 保存 - {}", storageInfo.getName());
                            String typeName = storageInfo.getName();
                            if (AsyncStorageManager.getInstance().getHandlerMap().containsKey(typeName)) {
                                AsyncStorageHandler<?> handler = AsyncStorageManager.getInstance().getHandlerMap().get(typeName);
                                Class<?> type = ClassUtils.getInterfaceGenericType(handler.getClass(), 0);
                                Object obj = handler.parseJson(storageInfo.getData());
                                if (obj != null && ClassUtils.isAssignable(obj.getClass(), type)) {
                                    handler.handleStorageSave(obj);

                                    log.info("========== 执行异步保存任务 - 保存 - {}", storageInfo.getName());
                                }
                            } else {
                                log.info("========== 执行异步保存任务 - 保存 - 跳过 - {}", storageInfo.getName());
                            }

                            ack();
                        } catch (Exception e) {
                            log.error("========== 执行异步保存任务 - 报错 - {} - {}", storageInfo.getName(), storageInfo.getData(), e);
                            pushError(storageInfo);
                        } finally {
                            SphO.exit();
                        }
                    } else {
                        unAck(storageInfo);
                    }
                    Thread.yield();
                }
            }
            // 关闭消费队列
            AsyncStorageManager.getInstance().running.countDown();
            log.info("========== 执行异步保存任务 - 结束 - {}", Thread.currentThread().getName());
        }

        /**
         * 消费
         *
         * @return {@link StorageInfo }
         * @author Tequila
         * @date 2022/08/12 00:56
         */
        private StorageInfo consume() {
            String json = getInstance().getRedisTemplate().opsForList().rightPopAndLeftPush(
                    getInstance().fullQueueName(),
                    getInstance().fullAckQueueName(),
                    CONSUME_WAIT_TIME,
                    TimeUnit.SECONDS);
            try {
                if (json != null) {
                    return Jackson2Utils.json2obj(json, StorageInfo.class);
                } else {
                    return null;
                }
            } catch (IOException e) {
                log.error("========== 执行异步保存任务 - 获取数据 - 失败 - {}", json, e);
                return null;
            }
        }

        /**
         * 确认消息处理
         *
         * @author Tequila
         * @date 2022/08/02 14:20
         */
        public void ack() {
            getInstance().getRedisTemplate().opsForList().leftPop(getInstance().fullAckQueueName());
        }

        /**
         * 消息处理未成功，放回队列中
         *
         * @author Tequila
         * @date 2022/08/02 14:26
         */
        public void unAck(StorageInfo storageInfo) {
            getInstance().getRedisTemplate().opsForList().rightPush(getInstance().fullQueueName(), storageInfo.toJson());
        }

        /**
         * 消息处失败，放到错误队列中
         *
         * @param storageInfo 存储信息
         * @author Tequila
         * @date 2022/08/12 01:22
         */
        public void pushError(StorageInfo storageInfo) {
            getInstance().getRedisTemplate().opsForList().leftPush(getInstance().fullQueueName(), storageInfo.toJson());
        }
    }

    /**
     * 存储信息
     *
     * @author Tequila
     * @create 2022/08/05 19:31
     **/
    private static class StorageInfo implements JsonObject {

        private String name;

        private String data;

        public StorageInfo() {
            super();
        }

        public StorageInfo(String name, String data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
