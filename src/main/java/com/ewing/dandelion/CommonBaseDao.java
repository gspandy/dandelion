package com.ewing.dandelion;

import com.ewing.dandelion.generation.PropertyUtils;
import com.ewing.dandelion.generation.SqlGenerator;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
@Repository
public class CommonBaseDao implements CommonDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonBaseDao.class);

    private JdbcOperations jdbcOperations;
    private NamedParameterJdbcOperations namedParamOperations;
    private SqlGenerator sqlGenerator;

    /**
     * 快速初始化的构造方法。
     */
    public CommonBaseDao() {
    }

    /**
     * 获取Sql生成器。
     */
    @Override
    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    /**
     * 设置Sql生成器。
     */
    @Override
    @Autowired
    public void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
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
     * 私有方法，根据Sql添加对象。
     */
    private <E> E addObject(E object, String sql) {
        LOGGER.debug(sql);
        sqlGenerator.generateIdentity(object);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(object)) < 1)
            throw new DaoException("保存对象失败！");
        return object;
    }

    /**
     * 把对象实例的所有属性插入到数据库。
     */
    @Override
    public <E> E add(E object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = sqlGenerator.getInsertValues(object.getClass());
        return addObject(object, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public <E> E addPositive(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = sqlGenerator.getInsertPositive(config);
        return addObject(object, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public <E> E addNegative(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = sqlGenerator.getInsertNegative(config);
        return addObject(object, sql);
    }

    /**
     * 批量把对象实例的所有属性插入到数据库。
     */
    @Override
    public <E> List<E> addBatch(E... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("实例对象列表为空！");
        List<E> entities = new ArrayList<>(objects.length);
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            E object = objects[i];
            if (object == null)
                throw new DaoException("包含为空的实例对象！");
            sqlGenerator.generateIdentity(object);
            sources[i] = new BeanPropertySqlParameterSource(object);
            entities.add(object);
        }
        String sql = sqlGenerator.getInsertValues(objects[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据Sql更新对象。
     */
    private <E> E updateObject(E object, String sql) {
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(object)) < 1)
            throw new DaoException("更新对象失败！");
        return object;
    }

    /**
     * 把对象实例的所有属性更新到数据库。
     */
    @Override
    public <E> E update(E object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = sqlGenerator.getUpdateWhereIdEquals(object.getClass());
        return updateObject(object, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public <E> E updatePositive(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        return updateObject(object, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public <E> E updateNegative(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("实例对象或配置对象为空！");
        String sql = sqlGenerator.getUpdateNegativeWhereIdEquals(config);
        return updateObject(object, sql);
    }

    /**
     * 批量更新对象实例的所有属性。
     */
    @Override
    public <E> List<E> updateBatch(E... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("实例对象列表为空！");
        List<E> entities = new ArrayList<>(objects.length);
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            E object = objects[i];
            if (object == null)
                throw new DaoException("包含为空的实例对象！");
            sources[i] = new BeanPropertySqlParameterSource(object);
            entities.add(object);
        }
        String sql = sqlGenerator.getUpdateWhereIdEquals(objects[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据ID和Sql获取对象。
     */
    private <E> E getObject(Class<E> clazz, Object id, String sql) {
        LOGGER.debug(sql);
        try {
            if (clazz.equals(id.getClass())) {
                List<PropertyDescriptor> properties = getSqlGenerator().getEntityInfo(clazz).getIdentityProperties();
                List<Object> params = PropertyUtils.getValues(properties, id);
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), params.toArray());
            } else {
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), id);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取指定类型的对象的所有属性。
     */
    @Override
    public <E> E get(Class<E> clazz, Object id) {
        if (clazz == null || id == null)
            throw new DaoException("对象类型或ID为空！");
        String sql = sqlGenerator.getSelectWhereIdEquals(clazz);
        return getObject(clazz, id, sql);
    }

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     */
    @Override
    public <E> E getPositive(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = sqlGenerator.getSelectPositiveWhereIdEquals(config);
        return getObject((Class<E>) config.getClass(), id, sql);
    }

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     */
    @Override
    public <E> E getNegative(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = sqlGenerator.getSelectNegativeWhereIdEquals(config);
        return getObject((Class<E>) config.getClass(), id, sql);
    }

    /**
     * 根据ID数组批量获取指定类型的对象的所有属性。
     */
    @Override
    public <E> List<E> getBatch(Class<E> clazz, Object... ids) {
        if (clazz == null || ids == null || ids.length == 0)
            throw new DaoException("对象类型或ID数组为空！");
        String sql = sqlGenerator.getSelectWhereBatchIds(clazz, ids.length);
        LOGGER.debug(sql);
        // 如果则参数为该实体的实例 使用反射取ID值
        if (clazz.equals(ids[0].getClass())) {
            List<PropertyDescriptor> properties = getSqlGenerator().getEntityInfo(clazz).getIdentityProperties();
            List<Object> params = new ArrayList<>();
            for (Object id : ids)
                params.addAll(PropertyUtils.getValues(properties, id));
            return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(clazz), params.toArray());
        } else {
            return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(clazz), ids);
        }
    }

    /**
     * 查询指定类型的对象总数。
     */
    @Override
    public long countAll(Class clazz) {
        String sql = sqlGenerator.getCountWhereTrue(clazz);
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class);
    }

    /**
     * 查询指定类型的所有记录。
     */
    @Override
    public <E> List<E> getAll(Class<E> clazz) {
        if (clazz == null)
            throw new DaoException("对象类型为空！");
        String querySql = sqlGenerator.getSelectWhereTrue(clazz);
        LOGGER.debug(querySql);
        return jdbcOperations.query(querySql, BeanPropertyRowMapper.newInstance(clazz));
    }

    /**
     * 分页查询所有记录。
     */
    @Override
    public <E> PageData<E> getByPage(Class<E> clazz, PageParam pageParam) {
        if (clazz == null)
            throw new DaoException("对象类型为空！");
        String querySql = sqlGenerator.getSelectWhereTrue(clazz);
        return queryObjectPage(pageParam, clazz, querySql);
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public void delete(Object object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = sqlGenerator.getDeleteNamedIdEquals(object.getClass());
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(object)) < 0)
            throw new DaoException("删除对象失败！");
    }

    /**
     * 批量把对象实例从数据库删除。
     */
    @Override
    public void deleteBatch(Object... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("实例对象列表为空！");
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == null)
                throw new DaoException("包含为空的实例对象！");
            sources[i] = new BeanPropertySqlParameterSource(object);
        }
        String sql = sqlGenerator.getDeleteNamedIdEquals(objects[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
    }

    /**
     * 根据对象的ID属性删除指定类型的对象。
     */
    @Override
    public void deleteById(Class clazz, Object id) {
        if (clazz == null || id == null)
            throw new DaoException("对象类型或ID为空！");
        String sql = sqlGenerator.getDeleteIdEquals(clazz);
        LOGGER.debug(sql);
        if (clazz.equals(id.getClass())) {
            List<PropertyDescriptor> properties = getSqlGenerator().getEntityInfo(clazz).getIdentityProperties();
            List<Object> params = PropertyUtils.getValues(properties, id);
            if (jdbcOperations.update(sql, params.toArray()) < 0)
                throw new DaoException("删除对象失败！");
        } else {
            if (jdbcOperations.update(sql, id) < 0)
                throw new DaoException("删除对象失败！");
        }
    }

    /**
     * 删除全部对象。
     */
    @Override
    public void deleteAll(Class clazz) {
        String sql = sqlGenerator.getDeleteWhereTrue(clazz);
        LOGGER.debug(sql);
        jdbcOperations.update(sql);
    }

    /**
     * 查询一个整数并封装成长整数。
     */
    @Override
    public long queryLong(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class, params);
    }

    /**
     * 查询一条记录并封装成指定类型的对象。
     */
    @Override
    public <T> T queryObject(Class<T> clazz, String querySql, Object... params) {
        if (clazz == null || querySql == null)
            throw new DaoException("对象类型或查询语句为空！");
        LOGGER.debug(querySql);
        try {
            return jdbcOperations.queryForObject(querySql, BeanPropertyRowMapper.newInstance(clazz), params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public <T> List<T> queryObjectList(Class<T> clazz, String querySql, Object... params) {
        if (clazz == null || querySql == null)
            throw new DaoException("对象类型或查询语句为空！");
        LOGGER.debug(querySql);
        return jdbcOperations.query(querySql, BeanPropertyRowMapper.newInstance(clazz), params);
    }

    /**
     * 查询一条记录并封装成Map。
     */
    @Override
    public Map queryMap(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.debug(querySql);
        try {
            return jdbcOperations.queryForMap(querySql, params);
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
        LOGGER.debug(querySql);
        return jdbcOperations.queryForList(querySql, params);
    }

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public <T> PageData<T> queryObjectPage(PageParam pageParam, Class<T> clazz, String querySql, Object... params) {
        if (clazz == null || querySql == null || pageParam == null)
            throw new DaoException("对象类型或查询语句或分页参数为空！");
        PageData<T> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + querySql + " ) _Total_";
            pageData.setTotal(queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                pageData.setContent(new ArrayList<>(0));
                return pageData;
            }
        }
        String pageSql = querySql + " LIMIT " + pageParam.getLimit() + " OFFSET " + pageParam.getOffset();
        LOGGER.debug(pageSql);
        pageData.setContent(jdbcOperations.query(pageSql, BeanPropertyRowMapper.newInstance(clazz), params));
        return pageData;
    }

    /**
     * 分页查询多条记录并封装成Map集合。
     */
    @Override
    public PageData<Map<String, Object>> queryMapPage(PageParam pageParam, String querySql, Object... params) {
        if (querySql == null || pageParam == null)
            throw new DaoException("查询语句或分页参数为空！");
        PageData<Map<String, Object>> pageData = new PageData<>();
        if (pageParam.isCount()) {
            String countSql = "SELECT COUNT(*) FROM ( " + querySql + " ) _Total_";
            pageData.setTotal(queryLong(countSql, params));
            if (pageData.getTotal() == 0) {
                pageData.setContent(new ArrayList<>(0));
                return pageData;
            }
        }
        String pageSql = querySql + " LIMIT " + pageParam.getLimit() + " OFFSET " + pageParam.getOffset();
        LOGGER.debug(pageSql);
        pageData.setContent(jdbcOperations.queryForList(pageSql, params));
        return pageData;
    }

}
