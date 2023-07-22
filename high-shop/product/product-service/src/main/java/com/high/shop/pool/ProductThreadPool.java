package com.high.shop.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class ProductThreadPool {

    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            // 注意核心线程数(空闲时保留线程数)不能大于最大线程数
            // 即corePoolSize(第一个参数)的值不能大于maximumPoolSize(第二个参数)的值
            4,
            Runtime.getRuntime().availableProcessors(),
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

}
