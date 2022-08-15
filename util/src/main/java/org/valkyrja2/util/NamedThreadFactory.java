package org.valkyrja2.util;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名的线程工厂方法
 *
 * @author Tequila
 * @create 2022/08/15 23:00
 **/
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private static final String NAME_PATTERN = "%s-%d-thread";
    private final String threadNamePrefix;

    /**
     * {@link NamedThreadFactory}构造方法
     *
     * @param threadNamePrefix 线程名前缀
     */
    public NamedThreadFactory(String threadNamePrefix) {
        final SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                .getThreadGroup();
        this.threadNamePrefix = String.format(Locale.ROOT, NAME_PATTERN,
                checkPrefix(threadNamePrefix), threadPoolNumber.getAndIncrement());
    }

    private static String checkPrefix(String prefix) {
        return prefix == null || prefix.length() == 0 ? "Valkyrja" : prefix;
    }

    /**
     * 创建一个新的{@link Thread 线程}
     *
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new Thread(group, r, String.format(Locale.ROOT, "%s-%d",
                this.threadNamePrefix, threadNumber.getAndIncrement()), 0);
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
