package com.ly.export.utils;

import java.util.concurrent.*;

/**
 * 线程池工具类
 */
public class ThreadPoolUtil {

    /**
     * 线程池的阻塞队列
     */
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100000);
    private static volatile ThreadPoolExecutor threadPool;

    static {
        createThreadPool();
    }

    private ThreadPoolUtil() {
    }

    private static ThreadPoolExecutor createThreadPool() {
        if (threadPool == null) {
            synchronized (ThreadPoolUtil.class) {
                if (threadPool != null) {
                    return threadPool;
                }

                // 最大线程数要小于数据库连接数 否则慢sql会耗尽数据库连接池
                threadPool = new ThreadPoolExecutor(20, 40, 0L, TimeUnit.MILLISECONDS, queue);
                return threadPool;
            }
        }
        return threadPool;
    }

    /**
     * 将任务放入线程池中
     */
    public static <T> Future<T> executeTask(Callable<T> callable){
        return threadPool.submit(callable);
    }

}