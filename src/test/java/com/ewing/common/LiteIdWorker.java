package com.ewing.common;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器，初始化instance参数不能重复，最多可使用到2248年。
 * 每2048毫秒内生成不超过16777216个（约每秒8百万个）就不会重复。
 */
public class LiteIdWorker {
    // 时间预算到2248年长度是43位，而我只给它留了32位，去掉后11位
    private static final int TIME_RIGHT_SHIFT = 11;
    private static final int TIME_LEFT_SHIFT = 32;
    private static final int INSTANCE_SHIFT = 24;
    private static final int SEQ_MASK = ~(-1 << 24);
    private static final int INSTANCE_MAX = ~(-1 << 8);
    private static final AtomicInteger COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private final long instance;

    public LiteIdWorker(int instance) {
        if (instance > INSTANCE_MAX || instance < 0)
            throw new IllegalArgumentException("实例必须在0到" + INSTANCE_MAX + "中！");
        this.instance = instance << INSTANCE_SHIFT;
    }

    public long nextLong() {
        long currentTime = System.currentTimeMillis();

        currentTime = currentTime >>> TIME_RIGHT_SHIFT;

        int count = COUNTER.getAndIncrement() & SEQ_MASK;

        return (currentTime << TIME_LEFT_SHIFT) | instance | count;
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
        int perThread = 10000;
        LiteIdWorker liteIdWorker = new LiteIdWorker(100);
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