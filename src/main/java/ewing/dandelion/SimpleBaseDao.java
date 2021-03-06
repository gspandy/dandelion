package ewing.dandelion;

import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公共JdbcOperations操作方法实现。
 */
public class SimpleBaseDao implements SimpleDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBaseDao.class);

    protected JdbcOperations jdbcOperations;
    protected NamedParameterJdbcOperations namedParamOperations;

    /**
     * 快速初始化的构造方法。
     */
    public SimpleBaseDao() {
    }

    /**
     * 获取操作数据库的JdbcOperations。
     */
    @Override
    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    /**
     * 设置操作数据库的JdbcOperations。
     */
    @Override
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * 获取操作数据库的命名JdbcOperations。
     */
    @Override
    public NamedParameterJdbcOperations getNamedParamOperations() {
        return namedParamOperations;
    }

    /**
     * 设置操作数据库的命名JdbcOperations。
     */
    @Override
    public void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations) {
        this.namedParamOperations = namedParamOperations;
    }

    /**
     * 查询一个整数并封装成长整数。
     */
    @Override
    public long queryLong(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("Query sql is empty.");
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class, params);
    }

    /**
     * 查询一条记录并封装成指定类型的实体对象。
     */
    @Override
    public <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        if (entityClass == null || sql == null)
            throw new DaoException("Entity class or sql is empty.");
        LOGGER.debug(sql);
        try {
            return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(entityClass), params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成指定类型的实体对象列表。
     */
    @Override
    public <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        if (entityClass == null || sql == null)
            throw new DaoException("Entity class or sql is empty.");
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(entityClass), params);
    }

    /**
     * 查询一条记录并封装成Map对象。
     */
    @Override
    public Map queryMap(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("Query sql is empty.");
        LOGGER.debug(sql);
        try {
            return jdbcOperations.queryForMap(sql, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成Map对象列表。
     */
    @Override
    public List<Map<String, Object>> queryMapList(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("Query sql is empty.");
        LOGGER.debug(sql);
        return jdbcOperations.queryForList(sql, params);
    }

    /**
     * 分页查询多条记录并封装成指定类型的实体对象分页数据。
     */
    @Override
    public <T> PageData<T> queryEntityPage(PageParam pageParam, Class<T> entityClass, String sql, Object... params) {
        if (pageParam == null || entityClass == null || sql == null)
            throw new DaoException("Page parameter or class or sql is empty.");
        PageData<T> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + sql + " ) _TOTAL_";
            pageData.setTotal(queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                return pageData.setContent(new ArrayList<>(0));
            }
        }
        String pageSql = sql + " LIMIT " + pageParam.getLimit() + " OFFSET " + pageParam.getOffset();
        LOGGER.debug(pageSql);
        List<T> content = jdbcOperations.query(pageSql, new BeanPropertyRowMapper<>(entityClass), params);
        if (!pageParam.isCount())
            pageData.setTotal(content.size());
        return pageData.setContent(content);
    }

    /**
     * 分页查询多条记录并封装成Map对象分页数据。
     */
    @Override
    public PageData<Map<String, Object>> queryMapPage(PageParam pageParam, String sql, Object... params) {
        if (pageParam == null || sql == null)
            throw new DaoException("Page parameter or sql is empty.");
        PageData<Map<String, Object>> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + sql + " ) _TOTAL_";
            pageData.setTotal(queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                return pageData.setContent(new ArrayList<>(0));
            }
        }
        String pageSql = sql + " LIMIT " + pageParam.getLimit() + " OFFSET " + pageParam.getOffset();
        LOGGER.debug(pageSql);
        List<Map<String, Object>> content = jdbcOperations.queryForList(pageSql, params);
        if (!pageParam.isCount())
            pageData.setTotal(content.size());
        return pageData.setContent(content);
    }

}
