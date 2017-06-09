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
public interface GenericDao<E> {

    /**
     * 设置操作数据库的JdbcOperations。
     *
     * @param jdbcOperations JdbcOperations。
     */
    void setJdbcOperations(JdbcOperations jdbcOperations);

    /**
     * 设置命名的操作数据库的JdbcOperations。
     *
     * @param namedParamOperations 命名的操作数据库的JdbcOperations。
     */
    void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations);

    /**
     * 把对象实例的所有属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @return 插入成功的对象。
     */
    E add(E object);

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    E addPositive(E object, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     *
     * @param object 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    E addNegative(E object, E config);

    /**
     * 批量把对象实例的所有属性插入到数据库。
     *
     * @param objects 对象数组。
     * @return 结果是否成功。
     */
    boolean[] addBatch(E... objects);

    /**
     * 把对象实例的所有属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @return 更新成功的对象。
     */
    E update(E object);

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    E updatePositive(E object, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     *
     * @param object 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    E updateNegative(E object, E config);

    /**
     * 根据ID获取指定类型的对象的所有属性。
     *
     * @param id 对象ID。
     * @return 指定类型的对象。
     */
    E getObject(Object... id);

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     *
     * @param config 指定对象配置，必须包含ID值。
     * @param id     对象ID。
     * @return 指定类型的对象。
     */
    E getPositive(E config, Object... id);

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     *
     * @param config 指定对象配置，必须包含ID值。
     * @param id     对象ID。
     * @return 指定类型的对象。
     */
    E getNegative(E config, Object... id);

    /**
     * 查询总数。
     *
     * @return 总记录数。
     */
    long countAll();

    /**
     * 查询所有记录。
     *
     * @return 所有记录数据。
     */
    List<E> getAll();

    /**
     * 分页查询所有记录。
     *
     * @param pageParam 分页参数。
     * @return 所有记录数据。
     */
    PageData<E> getByPage(PageParam pageParam);

    /**
     * 根据对象的ID属性删除对象。
     *
     * @param object 要删除的数据对象。
     */
    void delete(E object);

    /**
     * 根据对象的ID属性删除指定类型的对象。
     *
     * @param id 要删除的对象ID。
     * @return 是否删除成功。
     */
    void deleteById(Object... id);

    /**
     * 删除所有对象。
     */
    void deleteAll();

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
