package com.ewing.dandelion.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器，保持趋势递增，尾数均匀，每秒可获取262144000个全局唯一值。
 * 实测生成千万个用时约16秒，即每秒60万个，相对于2亿6千万来说是非常安全的。
 * 位值组成：毫秒去掉低6位(精度为64毫秒)+24位机器标识+16位进程标识+24位累加数。
 * 使用31位10进制整数或20位36进制字符串可再用1000多年，到时扩展字段长度即可。
 *
 * @author Ewing
 */
public class GlobalIdWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalIdWorker.class);
    // 将时间截掉后6位（相当于除以64）约精确到1/16秒
    private final static int timeTruncate = 6;
    // 机器标识及进程标识
    private static final String runMacProcBit;
    // 计数器 可以溢出可循环使用 实际取后24位
    private static final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());
    // 序号掩码（24个1）也是最大值16777215
    private final static int counterMask = ~(-1 << 24);
    // 序号标志位 保证长度一定是24+1位 再用substring去掉标志位
    private final static int counterFlag = 1 << 24;

    /**
     * 私有化构造方法。
     */
    private GlobalIdWorker() {
    }

    /**
     * 初始化机器标识及进程标识。
     */
    static {
        // 保证一定是24位机器ID + 16位进程ID
        int machineId = createMachineIdentifier() & 0xffffff | (1 << 24);
        int processId = createProcessIdentifier() & 0xffff | (1 << 16);
        String machineIdBit = Integer.toBinaryString(machineId).substring(1);
        String processIdBit = Integer.toBinaryString(processId).substring(1);
        runMacProcBit = machineIdBit + processIdBit;
    }

    /**
     * 生成全局唯一ID。
     */
    public static BigInteger nextBigInteger() {
        long timestamp = System.currentTimeMillis() >>> timeTruncate;

        int count = counter.getAndIncrement() & counterMask;

        // ID偏移组合生成最终的ID，并返回ID
        String idBit = Long.toBinaryString(timestamp) + runMacProcBit +
                Integer.toBinaryString(count | counterFlag).substring(1);

        return new BigInteger(idBit, 2);
    }

    /**
     * 获取36进制20位长度的String类型的ID。
     */
    public static String nextString() {
        return nextBigInteger().toString(36);
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

    /**
     * 获取机器标识的HashCode。
     */
    private static int createMachineIdentifier() {
        int machineHash;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);
                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException bue) {
                        // Mac地址少于6字节 继续
                    }
                }
            }
            machineHash = sb.toString().hashCode();
        } catch (Throwable t) {
            machineHash = new SecureRandom().nextInt();
            LOGGER.warn("Use random number instead mac address!", t);
        }
        return machineHash;
    }

    /**
     * 获取进程标识，转换双字节型。
     */
    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
            } else {
                processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable t) {
            processId = (short) new SecureRandom().nextInt();
            LOGGER.warn("Use random number instead process id!", t);
        }
        return processId;
    }

} 