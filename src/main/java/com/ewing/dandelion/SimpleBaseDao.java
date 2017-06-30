package com.ewing.dandelion;

import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
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
     * 追加Sql子句并添加参数到参数列表。
     */
    @Override
    public void appendSqlParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams) {
        sqlBuilder.append(sqlPart);
        for (Object param : newParams)
            allParams.add(param);
    }

    /**
     * 当存在参数时追加Sql语句并添加参数。
     */
    @Override
    public boolean appendHasParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams) {
        for (Object param : newParams) {
            if (param != null && (!(param instanceof String) || ((String) param).trim().length() > 0)) {
                sqlBuilder.append(sqlPart);
                for (Object newParam : newParams) {
                    allParams.add(newParam);
                }
                return true;
            }
        }
        return false;
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
     * 查询一条记录并封装成指定类型的对象。
     */
    @Override
    public <T> T queryObject(Class<T> clazz, String sql, Object... params) {
        if (clazz == null || sql == null)
            throw new DaoException("Class or sql is empty.");
        LOGGER.debug(sql);
        try {
            return jdbcOperations.queryForObject(sql, BeanPropertyRowMapper.newInstance(clazz), params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public <T> List<T> queryObjectList(Class<T> clazz, String sql, Object... params) {
        if (clazz == null || sql == null)
            throw new DaoException("Class or sql is empty.");
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(clazz), params);
    }

    /**
     * 查询一条记录并封装成Map。
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
     * 查询多条记录并封装成Map集合。
     */
    @Override
    public List<Map<String, Object>> queryMapList(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("Query sql is empty.");
        LOGGER.debug(sql);
        return jdbcOperations.queryForList(sql, params);
    }

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public <T> PageData<T> queryObjectPage(PageParam pageParam, Class<T> clazz, String sql, Object... params) {
        if (pageParam == null || clazz == null || sql == null)
            throw new DaoException("Page parameter or class or sql is empty.");
        PageData<T> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + sql + " ) _Total_";
            pageData.setTotal(queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                return pageData.setContent(new ArrayList<>(0));
            }
        }
        String pageSql = sql + " LIMIT " + pageParam.getLimit() + " OFFSET " + pageParam.getOffset();
        LOGGER.debug(pageSql);
        List<T> content = jdbcOperations.query(pageSql, BeanPropertyRowMapper.newInstance(clazz), params);
        if (!pageParam.isCount())
            pageData.setTotal(content.size());
        return pageData.setContent(content);
    }

    /**
     * 分页查询多条记录并封装成Map集合。
     */
    @Override
    public PageData<Map<String, Object>> queryMapPage(PageParam pageParam, String sql, Object... params) {
        if (pageParam == null || sql == null)
            throw new DaoException("Page parameter or sql is empty.");
        PageData<Map<String, Object>> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + sql + " ) _Total_";
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
