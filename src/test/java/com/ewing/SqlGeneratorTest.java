package com.ewing;

import com.ewing.dandelion.SqlGenerator;
import com.ewing.user.entity.MyUser;

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

        // 功能测试
        System.out.println(SqlGenerator.getColumnsByConfig(config, true));
        System.out.println(SqlGenerator.getResultColumns(myUser.getClass()));
        System.out.println(SqlGenerator.getResultColumnsPositive(config));
        System.out.println(SqlGenerator.getResultColumnsNegative(config));
        System.out.println(SqlGenerator.getInsertValues(myUser));
        System.out.println(SqlGenerator.getInsertValuesByConfig(myUser, config, true));
        System.out.println(SqlGenerator.getInsertPositiveValues(myUser, config));
        System.out.println(SqlGenerator.getInsertNegativeValues(myUser, config));
        System.out.println(SqlGenerator.getSelectBodyByConfig(config, true));
        System.out.println(SqlGenerator.getSelectWhereIdEquals(myUser.getClass()));
        System.out.println(SqlGenerator.getSelectWhereTrue(myUser.getClass()));
        System.out.println(SqlGenerator.getSelectPositiveWhereTrue(config));
        System.out.println(SqlGenerator.getSelectPositiveWhereIdEquals(config));
        System.out.println(SqlGenerator.getSelectNegativeWhereTrue(config));
        System.out.println(SqlGenerator.getSelectNegativeWhereIdEquals(config));
        System.out.println(SqlGenerator.getUpdateBodyByConfig(config, true));
        System.out.println(SqlGenerator.getUpdateWhereIdEquals(myUser.getClass()));
        System.out.println(SqlGenerator.getUpdatePositiveWhereIdEquals(config));
        System.out.println(SqlGenerator.getUpdateNegativeWhereIdEquals(config));
        System.out.println(SqlGenerator.getDeleteWhereIdEquals(myUser.getClass(), true));
        System.out.println(SqlGenerator.getDeleteWhereTrue(myUser.getClass()));

        // 性能测试
        int times = 1000000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            SqlGenerator.getInsertPositiveValues(myUser, config);
        System.out.println("通过配置插入 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            SqlGenerator.getDeleteWhereIdEquals(myUser.getClass(), true);
        System.out.println("根据ID删除 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            SqlGenerator.getUpdatePositiveWhereIdEquals(config);
        System.out.println("根据ID更新 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");

        time = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
            SqlGenerator.getSelectPositiveWhereIdEquals(config);
        System.out.println("根据ID查询 " + times + " 次用时：" + (System.currentTimeMillis() - time) + " 毫秒");
    }

}
