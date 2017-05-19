package com.ewing;

import com.ewing.dandelion.GlobalIdWorker;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 测试方法。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class GlobalIdWorkerTest {
    /**
     * 测试方法。
     */
    public static void main(String[] args) throws Exception {
        // 使用到3300年的长度测试
        Long time = new SimpleDateFormat("yyyyMMdd").parse("33001230").getTime();
        String idBit = Long.toBinaryString(time) + Long.toBinaryString(~(-1L << (48L + 13L)));
        System.out.println("使用到3300年时的值是：" + new BigInteger(idBit, 2)
                + "\n转换成36进制： " + new BigInteger(idBit, 2).toString(36));

        // ID无重复测试 10000个线程 每个线程生成100个
        CountDownLatch latch = new CountDownLatch(10000);
        Map<BigInteger, String> ids = new ConcurrentHashMap<>(1000000);
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < 100; n++)
                        ids.put(GlobalIdWorker.nextBigInteger(), "");
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.println("\n10000个线程线程各生成100个共：" + ids.size() + " 个无重复\n用时："
                + (System.currentTimeMillis() - time) + " 毫秒(含Map.put消耗的时间)");

        // 生成的ID尾数统计
        int x = 0;
        char[] chars = new char[1000000];
        for (BigInteger id : ids.keySet()) {
            String intString = id.toString();
            chars[x++] = intString.charAt(intString.length() - 1);
        }
        Arrays.sort(chars);
        System.out.println("\n生成的ID尾数统计：");
        int n = 1;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                n++;
                if (i == chars.length - 1)
                    System.out.println(chars[i] + "：" + n);
            } else {
                System.out.println(chars[i - 1] + "：" + n);
                n = 1;
            }
        }
    }
}
