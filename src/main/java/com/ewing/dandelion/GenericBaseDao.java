package com.ewing.dandelion;

import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
public abstract class GenericBaseDao<T> implements GenericDao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericBaseDao.class);

    private Class<T> entityClass;
    private JdbcOperations jdbcOperations;
    private NamedParameterJdbcOperations namedParamOperations;

    /**
     * 快速初始化的构造方法。
     */
    public GenericBaseDao() {
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superclass;
        entityClass = (Class<T>) type.getActualTypeArguments()[0];
    }

    /**
     * 获取泛型的实际类型。
     */
    public Class<T> getEntityClass() {
        return entityClass;
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
    @Autowired
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
    @Autowired
    public void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations) {
        this.namedParamOperations = namedParamOperations;
    }

    /**
     * 追加Sql子句并添加参数到参数列表。
     */
    protected void appendToSql(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams) {
        sqlBuilder.append(sqlPart);
        for (Object param : newParams)
            allParams.add(param);
    }

    /**
     * 当存在参数时追加Sql语句并添加参数。
     */
    protected boolean appendHasParam(StringBuilder sqlBuilder, String sqlPart, List<Object> allParams, Object... newParams) {
        boolean hasOne = false;
        for (Object param : newParams)
            if (param != null && (!(param instanceof String) || ((String) param).trim().length() > 0)) {
                hasOne = true;
                break;
            }
        if (hasOne) { // 存在非null的参数
            sqlBuilder.append(sqlPart);
            for (Object param : newParams)
                allParams.add(param);
        }
        return hasOne;
    }

    /**
     * 把对象实例的所有属性插入到数据库。
     */
    @Override
    public boolean add(T object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getInsertValues(object);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public boolean addPositive(T object, T config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = SqlGenerator.getInsertPositiveValues(object, config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public boolean addNegative(T object, T config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = SqlGenerator.getInsertNegativeValues(object, config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把对象实例的所有属性更新到数据库。
     */
    @Override
    public boolean update(T object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getUpdateWhereIdEquals(entityClass);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public boolean updatePositive(T object, T config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = SqlGenerator.getUpdatePositiveWhereIdEquals(config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public boolean updateNegative(T object, T config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = SqlGenerator.getUpdateNegativeWhereIdEquals(config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 根据ID获取指定类型的对象的所有属性。
     */
    @Override
    public T getObject(Object... id) {
        if (id == null || id.length == 0)
            throw new DaoException("对象ID为空！");
        String sql = SqlGenerator.getSelectWhereIdEquals(entityClass);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance(entityClass), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     */
    @Override
    public T getPositive(T config, Object... id) {
        if (config == null || id == null || id.length == 0)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = SqlGenerator.getSelectPositiveWhereIdEquals(config);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance(entityClass), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     */
    @Override
    public T getNegative(T config, Object... id) {
        if (config == null || id == null || id.length == 0)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = SqlGenerator.getSelectNegativeWhereIdEquals(config);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance(entityClass), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public boolean delete(T object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getDeleteWhereIdEquals(object.getClass(), true);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 根据对象的ID属性删除指定类型的对象。
     */
    @Override
    public boolean deleteById(Object... id) {
        if (id == null || id.length == 0)
            throw new DaoException("对象ID为空！");
        String sql = SqlGenerator.getDeleteWhereIdEquals(entityClass, false);
        LOGGER.info(sql);
        return this.getJdbcOperations().update(sql, id) > 0;
    }

    /**
     * 删除全部对象。
     */
    @Override
    public boolean deleteAll() {
        String sql = SqlGenerator.getDeleteWhereTrue(getEntityClass());
        LOGGER.info(sql);
        return this.getJdbcOperations().update(sql) >= 0;
    }

    /**
     * 查询总数。
     */
    @Override
    public long countAll() {
        String sql = SqlGenerator.getCountWhereTrue(entityClass);
        LOGGER.info(sql);
        return this.getJdbcOperations().queryForObject(sql, Long.class);
    }

    /**
     * 查询所有记录。
     */
    @Override
    public List<T> queryAll() {
        String querySql = SqlGenerator.getSelectWhereTrue(entityClass);
        LOGGER.info(querySql);
        return this.getJdbcOperations().query(querySql, BeanPropertyRowMapper.newInstance(entityClass));
    }

    /**
     * 查询一个整数并封装成长整数。
     */
    @Override
    public long queryLong(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(sql);
        return this.getJdbcOperations().queryForObject(sql, Long.class, params);
    }

    /**
     * 查询一条记录并封装成指定类型的对象。
     */
    @Override
    public T queryObject(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        try {
            return this.getJdbcOperations().queryForObject(querySql, BeanPropertyRowMapper.newInstance(entityClass), params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public List<T> queryObjectList(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        return this.getJdbcOperations().query(querySql, BeanPropertyRowMapper.newInstance(entityClass), params);
    }

    /**
     * 查询一条记录并封装成Map。
     */
    @Override
    public Map queryMap(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        try {
            return this.getJdbcOperations().queryForMap(querySql, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成Map集合。
     */
    @Override
    public List<Map<String, Object>> queryMapList(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        return this.getJdbcOperations().queryForList(querySql, params);
    }

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public PageData<T> queryPageData(PageParam pageParam, String querySql, Object... params) {
        if (querySql == null || pageParam == null)
            throw new DaoException("对象类型或查询语句或分页参数为空！");
        PageData<T> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + querySql + " ) _Total_";
            pageData.setTotal(this.queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                pageData.setContent(new ArrayList<>(0));
                return pageData;
            }
        }
        String pageSql = querySql + " LIMIT " + pageParam.getOffset() + "," + pageParam.getLimit();
        LOGGER.info(pageSql);
        pageData.setContent(this.getJdbcOperations().query(pageSql,
                BeanPropertyRowMapper.newInstance(getEntityClass()), params));
        return pageData;
    }

    /**
     * 分页查询多条记录并封装成Map集合。
     */
    @Override
    public PageData<Map<String, Object>> queryPageMap(PageParam pageParam, String querySql, Object... params) {
        if (querySql == null || pageParam == null)
            throw new DaoException("查询语句或分页参数为空！");
        PageData<Map<String, Object>> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + querySql + " ) _Total_";
            pageData.setTotal(this.queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                pageData.setContent(new ArrayList<>(0));
                return pageData;
            }
        }
        String pageSql = querySql + " LIMIT " + pageParam.getOffset() + "," + pageParam.getLimit();
        LOGGER.info(pageSql);
        pageData.setContent(this.getJdbcOperations().queryForList(pageSql, params));
        return pageData;
    }

}
