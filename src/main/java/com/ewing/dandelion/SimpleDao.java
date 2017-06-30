package com.ewing.dandelion;

import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;
import java.util.Map;

/**
 * 公共JdbcOperations操作方法接口。
 */
public interface SimpleDao {

    /**
     * 获取操作数据库的JdbcOperations。
     *
     * @return JdbcOperations。
     */
    JdbcOperations getJdbcOperations();

    /**
     * 设置操作数据库的JdbcOperations。
     *
     * @param jdbcOperations JdbcOperations。
     */
    void setJdbcOperations(JdbcOperations jdbcOperations);

    /**
     * 获取命名的操作数据库的JdbcOperations。
     *
     * @return 命名的操作数据库的JdbcOperations。
     */
    NamedParameterJdbcOperations getNamedParamOperations();

    /**
     * 设置命名的操作数据库的JdbcOperations。
     *
     * @param namedParamOperations 命名的操作数据库的JdbcOperations。
     */
    void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations);

    /**
     * 追加Sql子句并添加参数到参数列表。
     */
    void appendSqlParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams);

    /**
     * 当存在参数时追加Sql语句并添加参数。
     */
    boolean appendHasParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams);

    /**
     * 查询一个整数并封装成长整数。
     *
     * @param sql 查询SQL。
     * @return 查询返回长整数。
     */
    long queryLong(String sql, Object... params);

    /**
     * 查询一条记录并封装成指定类型的对象。
     *
     * @param clazz 指定对象类型。
     * @param sql   查询语句。
     * @return 指定类型的对象。
     */
    <T> T queryObject(Class<T> clazz, String sql, Object... params);

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     *
     * @param clazz 指定对象类型。
     * @param sql   查询语句。
     * @return 指定类型的对象。
     */
    <T> List<T> queryObjectList(Class<T> clazz, String sql, Object... params);

    /**
     * 查询一条记录并封装成Map。
     *
     * @param sql 查询语句。
     * @return 存储结果的Map。
     */
    Map queryMap(String sql, Object... params);

    /**
     * 查询多条记录并封装成Map集合。
     *
     * @param sql 查询语句。
     * @return 存储结果的Map集合。
     */
    List<Map<String, Object>> queryMapList(String sql, Object... params);

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     *
     * @param clazz 指定对象类型。
     * @param sql   查询语句。
     * @return 指定类型的对象。
     */
    <T> PageData<T> queryObjectPage(PageParam pageParam, Class<T> clazz, String sql, Object... params);

    /**
     * 分页查询多条记录并封装成Map集合。
     *
     * @param sql 查询语句。
     * @return 存储结果的Map集合。
     */
    PageData<Map<String, Object>> queryMapPage(PageParam pageParam, String sql, Object... params);

}
