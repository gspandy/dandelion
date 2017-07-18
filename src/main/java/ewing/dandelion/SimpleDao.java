package ewing.dandelion;

import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;
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
     */
    JdbcOperations getJdbcOperations();

    /**
     * 设置操作数据库的JdbcOperations。
     */
    void setJdbcOperations(JdbcOperations jdbcOperations);

    /**
     * 获取命名的操作数据库的JdbcOperations。
     */
    NamedParameterJdbcOperations getNamedParamOperations();

    /**
     * 设置命名的操作数据库的JdbcOperations。
     */
    void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations);

    /**
     * 查询一个整数并封装成长整数。
     */
    long queryLong(String sql, Object... params);

    /**
     * 查询一条记录并封装成指定类型的实体对象。
     */
    <T> T queryEntity(Class<T> entityClass, String sql, Object... params);

    /**
     * 查询多条记录并封装成指定类型的实体对象列表。
     */
    <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params);

    /**
     * 查询一条记录并封装成Map对象。
     */
    Map queryMap(String sql, Object... params);

    /**
     * 查询多条记录并封装成Map对象列表。
     */
    List<Map<String, Object>> queryMapList(String sql, Object... params);

    /**
     * 分页查询多条记录并封装成指定类型的实体对象分页数据。
     */
    <T> PageData<T> queryEntityPage(PageParam pageParam, Class<T> entityClass, String sql, Object... params);

    /**
     * 分页查询多条记录并封装成Map对象分页数据。
     */
    PageData<Map<String, Object>> queryMapPage(PageParam pageParam, String sql, Object... params);

}
