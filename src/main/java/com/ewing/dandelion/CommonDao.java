package com.ewing.dandelion;

import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;
import java.util.Map;

/**
 * 基本数据访问接口。
 *
 * @author Ewing
 * @since 2017-03-01
 **/
public interface CommonDao {

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
    void appendToSql(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams);

    /**
     * 当存在参数时追加Sql语句并添加参数。
     */
    boolean appendHasParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams);

    /**
     * 把对象实例的所有属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @return 是否插入成功。
     */
    boolean add(Object object);

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 是否插入成功。
     */
    boolean addPositive(Object object, Object config);

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 是否插入成功。
     */
    boolean addNegative(Object object, Object config);

    /**
     * 批量把对象实例的所有属性插入到数据库。
     *
     * @param objects 对象数组。
     * @return 结果是否成功。
     */
    boolean[] addBatch(Object... objects);

    /**
     * 把对象实例的所有属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @return 是否更新成功。
     */
    boolean update(Object object);

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 是否更新成功。
     */
    boolean updatePositive(Object object, Object config);

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 是否更新成功。
     */
    boolean updateNegative(Object object, Object config);

    /**
     * 根据ID获取指定类型的对象的所有属性。
     *
     * @param clazz 对象类型。
     * @param id    对象ID。
     * @return 指定类型的对象。
     */
    <T> T getObject(Class<T> clazz, Object... id);

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     *
     * @param config 指定对象配置，必须包含ID值。
     * @param id     对象ID。
     * @return 指定类型的对象。
     */
    <T> T getPositive(T config, Object... id);

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     *
     * @param config 指定对象配置，必须包含ID值。
     * @param id     对象ID。
     * @return 指定类型的对象。
     */
    <T> T getNegative(T config, Object... id);

    /**
     * 查询总数。
     *
     * @param clazz 指定对象类型。
     * @return 总记录数。
     */
    long countAll(Class<?> clazz);

    /**
     * 获取所有记录。
     *
     * @param clazz 指定对象类型。
     * @return 所有记录数据。
     */
    <T> List<T> getAll(Class<T> clazz);

    /**
     * 根据对象的ID属性删除对象。
     *
     * @param object 要删除的数据对象。
     * @return 是否删除成功。
     */
    boolean delete(Object object);

    /**
     * 根据对象的ID属性删除指定类型的对象。
     *
     * @param clazz 指定对象类型。
     * @param id    要删除的对象ID。
     * @return 是否删除成功。
     */
    boolean deleteById(Class<?> clazz, Object... id);

    /**
     * 删除所有对象。
     *
     * @param clazz 对象类型。
     * @return 是否删除成功。
     */
    boolean deleteAll(Class<?> clazz);

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
     * @param clazz    指定对象类型。
     * @param querySql 查询语句。
     * @return 指定类型的对象。
     */
    <T> T queryObject(Class<T> clazz, String querySql, Object... params);

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     *
     * @param clazz    指定对象类型。
     * @param querySql 查询语句。
     * @return 指定类型的对象。
     */
    <T> List<T> queryObjectList(Class<T> clazz, String querySql, Object... params);

    /**
     * 查询一条记录并封装成Map。
     *
     * @param querySql 查询语句。
     * @return 存储结果的Map。
     */
    Map queryMap(String querySql, Object... params);

    /**
     * 查询多条记录并封装成Map集合。
     *
     * @param querySql 查询语句。
     * @return 存储结果的Map集合。
     */
    List<Map<String, Object>> queryMapList(String querySql, Object... params);

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     *
     * @param clazz    指定对象类型。
     * @param querySql 查询语句。
     * @return 指定类型的对象。
     */
    <T> PageData<T> queryPageData(PageParam pageParam, Class<T> clazz, String querySql, Object... params);

    /**
     * 分页查询多条记录并封装成Map集合。
     *
     * @param querySql 查询语句。
     * @return 存储结果的Map集合。
     */
    PageData<Map<String, Object>> queryPageMap(PageParam pageParam, String querySql, Object... params);

}
