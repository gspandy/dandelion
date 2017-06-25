package com.ewing.boot;

import com.ewing.boot.generic.entity.MyUser;
import com.ewing.dandelion.generation.SqlGenerator;

import java.math.BigDecimal;

/**
 * SQL生成器测试。
 *
 * @author Ewing
 * @date 2017/5/23
 */
public class SqlGeneratorTest {

    public static void main(String[] args) {
        MyUser config = new MyUser();
        config.setBoolValue(true);
        config.setBigDecimal(new BigDecimal("123.45"));
        MyUser myUser = new MyUser();

        SqlGenerator sqlGenerator = new SqlGenerator();

        // 功能测试
        System.out.println(sqlGenerator.getColumnsByConfig(config, true));
        System.out.println(sqlGenerator.getResultColumns(MyUser.class));
        System.out.println(sqlGenerator.getPositiveColumns(config));
        System.out.println(sqlGenerator.getNegativeColumns(config));
        System.out.println(sqlGenerator.getInsertValues(MyUser.class));
        System.out.println(sqlGenerator.getInsertByConfig(config, true));
        System.out.println(sqlGenerator.getInsertPositive(config));
        System.out.println(sqlGenerator.getInsertNegative(config));
        System.out.println(sqlGenerator.getCountWhereTrue(MyUser.class));
        System.out.println(sqlGenerator.getSelectByConfig(config, true));
        System.out.println(sqlGenerator.getSelectWhereIdEquals(MyUser.class));
        System.out.println(sqlGenerator.getSelectWhereBatchIds(MyUser.class, 5));
        System.out.println(sqlGenerator.getSelectWhereTrue(MyUser.class));
        System.out.println(sqlGenerator.getSelectPositiveWhereTrue(config));
        System.out.println(sqlGenerator.getSelectPositiveWhereIdEquals(config));
        System.out.println(sqlGenerator.getSelectNegativeWhereTrue(config));
        System.out.println(sqlGenerator.getSelectNegativeWhereIdEquals(config));
        System.out.println(sqlGenerator.getUpdateByConfig(config, true));
        System.out.println(sqlGenerator.getUpdateWhereIdEquals(MyUser.class));
        System.out.println(sqlGenerator.getUpdatePositiveWhereIdEquals(config));
        System.out.println(sqlGenerator.getUpdateNegativeWhereIdEquals(config));
        System.out.println(sqlGenerator.getDeleteIdEquals(MyUser.class));
        System.out.println(sqlGenerator.getDeleteNamedIdEquals(MyUser.class));
        System.out.println(sqlGenerator.getDeleteWhereTrue(MyUser.class));

        // 性能测试
        int times = 1000000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getInsertPositive(config);
        System.out.println("通过配置插入 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getDeleteIdEquals(MyUser.class);
        System.out.println("根据ID删除 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        System.out.println("根据ID更新 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            sqlGenerator.getSelectPositiveWhereIdEquals(config);
        System.out.println("根据ID查询 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");
    }

}
