package com.high.shop.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ManagerThreadPool {

    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            4,
            Runtime.getRuntime().availableProcessors(),
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(30),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

}
