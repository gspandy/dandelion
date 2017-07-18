package tsai.ewing.normal;

import tsai.ewing.dandelion.generation.GlobalIdWorker;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
        Long time = System.currentTimeMillis() >>> 6;
        BigInteger id = new BigInteger(Long.toBinaryString(time) + Long.toBinaryString(~0L), 2);
        System.out.println("\n当前时间的值是：" + id + "\n转换成36进制： " + id.toString(36));
        // 使用日期与长度测试
        String date = "3060年";
        time = new SimpleDateFormat("yyyy年").parse(date).getTime() >>> 6;
        id = new BigInteger(Long.toBinaryString(time) + Long.toBinaryString(~0L), 2);
        System.out.println("\n使用到" + date + "的值是：" + id + "\n转换成36进制： " + id.toString(36));

        // 高并发性能测试
        int threads = 1000;
        int perThread = 1000;
        CountDownLatch latch = new CountDownLatch(threads);
        Object[] results = new Object[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int n = 0; n < perThread; n++)
                    results[index.getAndIncrement()] = GlobalIdWorker.nextBigInteger();
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.print("\n" + threads + "个线程线程各生成" + perThread + "个用时："
                + (System.currentTimeMillis() - time) + " 毫秒\n" + "共：" + index.get() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(threads * perThread);
        ids.addAll(Arrays.asList(results));
        System.out.println(" 其中唯一值：" + ids.size() + " 个");
        System.out.println(GlobalIdWorker.nextString());
    }
}
