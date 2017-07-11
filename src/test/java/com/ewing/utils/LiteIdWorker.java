package com.ewing.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

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
     * 使用JDK生成UUID并转换成25位36进制字符串。
     */
    public static String uuidString() {
        UUID id = UUID.randomUUID();
        // 取高低位转换成36进制 低位部分须补足16位
        String mb = Long.toHexString(id.getMostSignificantBits());
        StringBuilder lb = new StringBuilder(Long.toHexString(id.getLeastSignificantBits()));
        while (lb.length() < 16) lb.insert(0, '0');
        StringBuilder idb = new StringBuilder(new BigInteger(mb + lb.toString(), 16).toString(36));
        while (idb.length() < 25) idb.insert(0, '0');
        return idb.toString();
    }

    /**
     * 使用JDK生成UUID并转换成32位16进制字符串。
     */
    public static String uuidHex() {
        UUID id = UUID.randomUUID();
        // 取高低位转换成16进制 比直接toString效率好很多
        StringBuilder mb = new StringBuilder(Long.toHexString(id.getMostSignificantBits()));
        while (mb.length() < 16) mb.insert(0, '0');
        StringBuilder lb = new StringBuilder(Long.toHexString(id.getLeastSignificantBits()));
        while (lb.length() < 16) lb.insert(0, '0');
        return mb.append(lb).toString();
    }

}