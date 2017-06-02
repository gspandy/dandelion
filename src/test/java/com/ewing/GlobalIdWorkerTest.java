package com.ewing;

import com.ewing.dandelion.GlobalIdWorker;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 全局ID测试类。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class GlobalIdWorkerTest {
    /**
     * 测试方法。
     */
    public static void main(String[] args) throws Exception {
        // 当前时间测试
        Long time = System.currentTimeMillis();
        String idBit = Long.toBinaryString(time) + Long.toBinaryString(~(-1L << (24L + 16L + 16L)));
        System.out.println("使用当前时间的值是：" + new BigInteger(idBit, 2)
                + "\n转换成36进制： " + new BigInteger(idBit, 2).toString(36));
        // 使用日期与长度测试
        String date = "6300年";
        time = new SimpleDateFormat("yyyy年").parse(date).getTime();
        idBit = Long.toBinaryString(time) + Long.toBinaryString(~(-1L << (24L + 16L + 16L)));
        System.out.println("\n使用到" + date + "的值是：" + new BigInteger(idBit, 2)
                + "\n转换成36进制： " + new BigInteger(idBit, 2).toString(36));

        // ID无重复测试 1000个线程 每个线程生成1000个
        Object value = new Object();
        CountDownLatch latch = new CountDownLatch(1000);
        Map<Object, Object> ids = new ConcurrentHashMap<>(1000000);
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < 1000; n++)
                        ids.put(GlobalIdWorker.nextBigInteger(), value);
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.println("\n1000个线程线程各生成1000个共：" + ids.size() + " 个无重复\n用时："
                + (System.currentTimeMillis() - time) + " 毫秒（要再减去Map操作耗时）");
    }
}
