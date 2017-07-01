package com.ewing.dandelion;

import com.ewing.dandelion.generation.PropertyUtils;
import com.ewing.dandelion.generation.SqlGenerator;
import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
public class EntityBaseDao extends SimpleBaseDao implements EntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityBaseDao.class);

    protected SqlGenerator sqlGenerator;

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
    public void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }

    /**
     * 私有方法，根据Sql添加对象。
     */
    private <E> E addObject(E object, String sql) {
        LOGGER.debug(sql);
        sqlGenerator.generateIdentity(object);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(object)) < 1)
            throw new DaoException("Add object failed.");
        return object;
    }

    /**
     * 把对象实例的所有属性插入到数据库。
     */
    @Override
    public <E> E add(E object) {
        if (object == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getInsertValues(object.getClass());
        return addObject(object, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public <E> E addPositive(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getInsertPositive(config);
        return addObject(object, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public <E> E addNegative(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getInsertNegative(config);
        return addObject(object, sql);
    }

    /**
     * 批量把对象实例的所有属性插入到数据库。
     */
    @Override
    public <E> List<E> addBatch(E... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("Object instances is empty.");
        List<E> entities = new ArrayList<>(objects.length);
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            E object = objects[i];
            if (object == null)
                throw new DaoException("Object instance is empty.");
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
            throw new DaoException("Update object failed.");
        return object;
    }

    /**
     * 把对象实例的所有属性更新到数据库。
     */
    @Override
    public <E> E update(E object) {
        if (object == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getUpdateWhereIdEquals(object.getClass());
        return updateObject(object, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public <E> E updatePositive(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        return updateObject(object, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public <E> E updateNegative(E object, E config) {
        if (object == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getUpdateNegativeWhereIdEquals(config);
        return updateObject(object, sql);
    }

    /**
     * 批量更新对象实例的所有属性。
     */
    @Override
    public <E> List<E> updateBatch(E... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("Object instances is empty.");
        List<E> entities = new ArrayList<>(objects.length);
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            E object = objects[i];
            if (object == null)
                throw new DaoException("Object instance is empty.");
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
                Object[] params = PropertyUtils.getEntityIds(getSqlGenerator().getEntityInfo(clazz), id);
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), params);
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
            throw new DaoException("Object instance or identity is empty.");
        String sql = sqlGenerator.getSelectWhereIdEquals(clazz);
        return getObject(clazz, id, sql);
    }

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     */
    @Override
    public <E> E getPositive(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("Config object or identity is empty.");
        String sql = sqlGenerator.getSelectPositiveWhereIdEquals(config);
        return getObject((Class<E>) config.getClass(), id, sql);
    }

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     */
    @Override
    public <E> E getNegative(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("Config object or identity is empty.");
        String sql = sqlGenerator.getSelectNegativeWhereIdEquals(config);
        return getObject((Class<E>) config.getClass(), id, sql);
    }

    /**
     * 根据ID数组批量获取指定类型的对象的所有属性。
     */
    @Override
    public <E> List<E> getBatch(Class<E> clazz, Object... ids) {
        if (clazz == null || ids == null || ids.length == 0)
            throw new DaoException("Class or identities is empty.");
        String sql = sqlGenerator.getSelectWhereBatchIds(clazz, ids.length);
        LOGGER.debug(sql);
        // 如果则参数为该实体的实例 使用反射取ID值
        if (clazz.equals(ids[0].getClass())) {
            Object[] params = PropertyUtils.getEntitiesIds(getSqlGenerator().getEntityInfo(clazz), ids);
            return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(clazz), params);
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
            throw new DaoException("Class is empty.");
        String sql = sqlGenerator.getSelectWhereTrue(clazz);
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(clazz));
    }

    /**
     * 分页查询所有记录。
     */
    @Override
    public <E> PageData<E> getByPage(Class<E> clazz, PageParam pageParam) {
        if (clazz == null)
            throw new DaoException("Class is empty.");
        String sql = sqlGenerator.getSelectWhereTrue(clazz);
        return queryObjectPage(pageParam, clazz, sql);
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public void delete(Object object) {
        if (object == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getDeleteNamedIdEquals(object.getClass());
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(object)) < 0)
            throw new DaoException("Delete object failed.");
    }

    /**
     * 批量把对象实例从数据库删除。
     */
    @Override
    public void deleteBatch(Object... objects) {
        if (objects == null || objects.length == 0)
            throw new DaoException("Object instances is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == null)
                throw new DaoException("Object instance is empty.");
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
            throw new DaoException("Object instance or identity is empty.");
        String sql = sqlGenerator.getDeleteIdEquals(clazz);
        LOGGER.debug(sql);
        if (clazz.equals(id.getClass())) {
            Object[] params = PropertyUtils.getEntityIds(getSqlGenerator().getEntityInfo(clazz), id);
            if (jdbcOperations.update(sql, params) < 0)
                throw new DaoException("Delete object failed.");
        } else {
            if (jdbcOperations.update(sql, id) < 0)
                throw new DaoException("Delete object failed.");
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

}
