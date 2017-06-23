package com.ewing.boot.common;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器，初始化instance[0~2047]参数不能重复，最多可使用到2248年。
 * 每1024毫秒最多可生成不超过1048576个（即每秒1024000个），永不重复。
 */
public class LiteIdWorker {
    // 时间预算到2248年长度是43位，而我只给它留了33位，去掉后10位
    private static final int TIME_TRUNCATE = 10;
    private static final int TIME_SHIFT = 31;
    private static final int INSTANCE_SHIFT = 20;
    private static final int SEQ_MASK = ~(-1 << 20);
    private static final int INSTANCE_MAX = ~(-1 << 11);
    private static final int FIRST = new SecureRandom().nextInt() & SEQ_MASK;
    private int count = FIRST;
    private long timeNow = getTruncateTime();
    private final long instance;

    public LiteIdWorker(int instance) {
        if (instance > INSTANCE_MAX || instance < 0)
            throw new IllegalArgumentException("实例必须在0到" + INSTANCE_MAX + "中！");
        this.instance = instance << INSTANCE_SHIFT;
    }

    public synchronized long nextLong() {
        long currentTime = getTruncateTime();
        // 时间滞后 等待时间同步
        while (currentTime < timeNow) {
            sleepOneMilis();
            currentTime = getTruncateTime();
        }
        count = ++count & SEQ_MASK;
        // 计数器已满 等待下个时间段
        if (count == FIRST)
            while (currentTime == timeNow) {
                sleepOneMilis();
                currentTime = getTruncateTime();
            }
        timeNow = currentTime;
        return (currentTime << TIME_SHIFT) | instance | count;
    }

    /**
     * 获取去掉后几位的时间。
     */
    private static long getTruncateTime() {
        return System.currentTimeMillis() >>> TIME_TRUNCATE;
    }

    /**
     * 线程睡眠一毫秒。
     */
    private static void sleepOneMilis() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // 休眠异常 继续执行
        }
    }

    /**
     * 测试方法，无侵入不导包，可直接删除。
     */
    public static void main(String[] args) throws Exception {
        String date = "2248年";
        long time = new java.text.SimpleDateFormat("yyyy年").parse(date).getTime();
        System.out.println(date + "时间：" + Long.toBinaryString(time) + " 长度：" + Long.toBinaryString(time).length());
        // 高并发性能测试
        int threads = 1000;
        int perThread = 1000;
        LiteIdWorker liteIdWorker = new LiteIdWorker(0);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threads);
        long[] longs = new long[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(new Runnable() {
                public void run() {
                    for (int n = 0; n < perThread; n++)
                        longs[index.getAndIncrement()] = liteIdWorker.nextLong();
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.print("\n" + threads + "个线程线程各生成" + perThread + "个用时："
                + (System.currentTimeMillis() - time) + " 毫秒\n共：" + index.get() + " 个");
        // 验证是否唯一
        java.util.Set<Object> ids = new java.util.HashSet<>(threads * perThread);
        for (long lo : longs) ids.add(lo);
        System.out.println(" 唯一值：" + ids.size() + " 个");
    }

}