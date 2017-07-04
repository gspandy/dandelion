package com.ewing.boot;

import com.ewing.boot.entity.MyUser;
import com.ewing.dandelion.SqlBuilder;
import com.ewing.dandelion.generation.SqlGenerator;

import java.util.Arrays;

/**
 * Sql构建工具测试。
 */
public class SqlBuilderTest {

    public static void main(String[] args) {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.getSelectWhereTrue(MyUser.class);
        SqlBuilder sqlBuilder = new SqlBuilder(sql + "\n");

        sqlBuilder.appendSql(" AND appendSql = ?").appendParams("appendParams")
                .appendSqlParams(" AND appendSqlParams in (?,?,?)", 3, 4, 5)
                .appendHasValue(" AND appendHasValue = ?", "Value")
                .extendSqlParams(" AND extendSqlParams IN ", 1, null, 3)
                .extendHasValues(" AND extendHasValues IN ", "A", "C", null)
                .appendStartWith(" AND appendStartWith like ?", "StartWith")
                .appendEndWith(" AND appendEndWith like ?", "EndWith")
                .appendContains(" AND appendContains like ?", "Contains");

        System.out.println(sqlBuilder.toString());
        System.out.println(Arrays.toString(sqlBuilder.getParams()));
    }

}
