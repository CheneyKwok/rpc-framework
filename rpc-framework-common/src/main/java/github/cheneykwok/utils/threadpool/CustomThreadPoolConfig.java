package github.cheneykwok.utils.threadpool;


import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Data
public class CustomThreadPoolConfig {

    /**
     * 默认配置参数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;

    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;

    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;

    /**
     * 可配置参数
     */
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;

    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;

    private int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    private TimeUnit unit = DEFAULT_TIME_UNIT;
    private int BLOCKING_QUEUE_CAPACITY = DEFAULT_BLOCKING_QUEUE_CAPACITY;

    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
}
