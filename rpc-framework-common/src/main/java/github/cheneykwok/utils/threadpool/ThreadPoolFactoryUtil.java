package github.cheneykwok.utils.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public final class ThreadPoolFactoryUtil {

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {

        return createCustomThreadPoolIfAbsent(new CustomThreadPoolConfig(), threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig customThreadPoolConfig) {
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
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
        return new ThreadPoolExecutor(poolConfig.getCorePoolSize(), poolConfig.getMaximumPoolSize(), poolConfig.getKeepAliveTime(),
                poolConfig.getUnit(), poolConfig.getWorkQueue(), createThreadFactory(threadNamePrefix, daemon));
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPoolExecutor) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("==================ThreadPool Status===================");
            log.info("ThreadPool Size: [{}]", threadPoolExecutor.getPoolSize());
            log.info("Active Threads: [{}]", threadPoolExecutor.getActiveCount());
            log.info("Number of Tasks: [{}]", threadPoolExecutor.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: [{}]", threadPoolExecutor.getQueue().size());
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void shutDownAllThreadPool() {
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(e -> {
            ExecutorService executorService = e.getValue();
            executorService.shutdown();
            log.info("shutdown thread pool [{}] [{}]", e.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                log.info("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }
}
