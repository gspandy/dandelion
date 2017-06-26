package com.ewing.boot;

import com.ewing.boot.genericdao.entity.MyUser;
import com.ewing.dandelion.generation.NameHandler;
import com.ewing.dandelion.generation.SqlGenerator;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * SQL生成器测试。
 *
 * @author Ewing
 * @date 2017/5/23
 */
public class SqlGeneratorTest {

    public static void main(String[] args) throws Exception {
        // 功能测试
        functionTest();

        MyUser config = new MyUser();
        config.setBoolValue(true);
        config.setBigDecimal(new BigDecimal(1));
        config.setBytesValue(new byte[0]);

        SqlGenerator sqlGenerator = new SqlGenerator();

        // 高并发及性能测试
        int threads = 10000;
        int perThread = 100;
        CountDownLatch latch = new CountDownLatch(threads);
        long time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(new Runnable() {
                public void run() {
                    for (int n = 0; n < perThread; n++) {
                        MyUser myUser = new MyUser();
                        myUser.setName("ABC123");
                        sqlGenerator.getInsertPositive(config);
                        sqlGenerator.generateIdentity(myUser);
                        sqlGenerator.getSelectPositiveWhereIdEquals(config);
                        sqlGenerator.getUpdatePositiveWhereIdEquals(config);
                        sqlGenerator.getDeleteNamedIdEquals(MyUser.class);
                    }
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.println(threads + "个线程各执行" + perThread + "次（共" + threads * perThread
                + "次）CRUD语句及ID生成用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        // 单线程性能测试
        int times = 1000000;
        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getInsertPositive(config);
        System.out.println("单线程生成插入语句 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getDeleteNamedIdEquals(MyUser.class);
        System.out.println("单线程生成删除语句 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        System.out.println("单线程生成更新语句 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getSelectPositiveWhereIdEquals(config);
        System.out.println("单线程生成查询语句 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

    }

    /**
     * 功能测试。
     */
    public static void functionTest() throws Exception {
        MyUser config = new MyUser();
        config.setBoolValue(true);
        config.setBigDecimal(new BigDecimal(1));
        config.setBytesValue(new byte[0]);

        // 使用下划线风格的Sql命名（@SqlName注解优先）
        NameHandler nameHandler = new NameHandler(true);
        SqlGenerator sqlGenerator = new SqlGenerator(nameHandler);

        // 按方法名字典顺序调用SqlGenerator中的方法
        Method[] methods = SqlGenerator.class.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method method : methods) {
            StringBuilder name = new StringBuilder(method.getName()).append("(");
            List<Object> params = new ArrayList<>();
            Class[] types = method.getParameterTypes();
            // 准备方法的参数
            for (Class type : types) {
                if (type.equals(Class.class)) {
                    params.add(config.getClass());
                    name.append(config.getClass().getSimpleName())
                            .append(".class").append(",");
                } else if (type.equals(String.class)) {
                    params.add("A");
                    name.append("A").append(",");
                } else if (type.equals(Object.class)) {
                    params.add(config);
                    name.append("config").append(",");
                } else if (type.equals(boolean.class)) {
                    params.add(true);
                    name.append(true).append(",");
                } else if (type.equals(int.class)) {
                    params.add(5);
                    name.append(5).append(",");
                }
            }
            name.deleteCharAt(name.length() - 1).append(")：");
            // 调用方法
            Object value = method.invoke(sqlGenerator, params.toArray());
            System.out.println(name.append(value == null ? "no return value" : value));
        }
    }

}
