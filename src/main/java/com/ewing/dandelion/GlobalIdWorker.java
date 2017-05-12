package com.ewing.dandelion;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 可独立运行的全局ID生成器，保持趋势递增，线程安全，尾数均匀分布。
 * 41位(自动扩展位数)毫秒+48Mac地址+13位累加(每毫秒之后对100取余)。
 * 理想情况平均每秒可生成8190950个，实测生成百万个用时不到1秒(视配置而定)。
 * 使用32位10进制可以使用到3300年之后，使用21位36进制可使用到8500年以后，可以扩展长度。
 *
 * @author Ewing
 */
public class GlobalIdWorker {
    // Mac地址掩码（48个1）
    private final static long macAddressMask = ~(-1L << 48L);
    // Mac标志位 保证长度一定是48+1位 再用substring去掉标志位
    private final static long macAddressFlag = 1L << 48L;

    // 序号掩码（13个1）也是最大值
    private final static long sequenceMask = ~(-1L << 13L);
    // 序号标志位 保证长度一定是13+1位 再用substring去掉标志位
    private final static long sequenceFlag = 1L << 13L;

    private static long lastTimestamp = System.currentTimeMillis();

    private static long sequence = 0L;

    // 尾数离散度 可以分散ID的尾数分布
    private static long discreteRate = 100L;

    private static String macAddressBit;

    /**
     * 初始化worker
     */
    static {
        try {
            long macAddress = macAddressLong();
            macAddressBit = Long.toBinaryString((macAddress & macAddressMask) | macAddressFlag).substring(1);
        } catch (IOException e) {
            throw new RuntimeException("Init mac address fail.", e);
        }
    }

    /**
     * 获取机器的Mac地址（48位）
     */
    private static long macAddressLong() throws IOException {
        byte[] macs = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        int shift = 0;
        long macLong = 0;
        for (int i = 0; i < macs.length; i++) {
            macLong = (macLong << shift) | (macs[i] & 0xFF);
            shift += 8;
        }
        return macLong;
    }

    /**
     * 生成唯一ID
     */
    public static synchronized BigInteger nextBigInteger() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            tilNextMillis(lastTimestamp);
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，序号递增
            sequence = sequence + 1;
            if (sequence > sequenceMask) {
                // 当前毫秒内计数满了，则等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = sequence % discreteRate;
        }

        lastTimestamp = timestamp;

        // ID偏移组合生成最终的ID，并返回ID
        String idBit = Long.toBinaryString(timestamp) + macAddressBit +
                Long.toBinaryString(sequence | sequenceFlag).substring(1);

        return new BigInteger(idBit, 2);
    }

    /**
     * 循环到下一毫秒
     */
    private static long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取时间
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取BigDecimal类型的ID
     */
    public static BigDecimal nextBigDecimal() {
        return new BigDecimal(nextBigInteger());
    }

} 