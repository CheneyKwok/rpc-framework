package github.cheneykwok.utils.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public final class ThreadPoolFactoryUtil {

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {

        CustomThreadPoolConfig poolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(poolConfig, threadNamePrefix, false);
    }

    private static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig poolConfig, String threadNamePrefix, boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(poolConfig, threadNamePrefix, daemon));
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(poolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig poolConfig, String threadNamePrefix, boolean daemon) {
        ThreadFactory factory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(poolConfig.getCorePoolSize(), poolConfig.getMaximumPoolSize(), poolConfig.getKeepAliveTime(),
                poolConfig.getUnit(), poolConfig.getWorkQueue(), factory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix, boolean daemon) {
        return null;
    }
}
