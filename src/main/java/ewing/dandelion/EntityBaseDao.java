package ewing.dandelion;

import ewing.dandelion.generation.EntityUtils;
import ewing.dandelion.generation.SqlGenerator;
import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
     * 私有方法，根据Sql添加实体对象。
     */
    private <E> E addEntity(E entity, String sql) {
        LOGGER.debug(sql);
        sqlGenerator.generateIdentity(entity);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 1)
            throw new DaoException("Add entity failed.");
        return entity;
    }

    /**
     * 添加实体对象的全部属性到数据库。
     */
    @Override
    public <E> E add(E entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getInsertValues(entity.getClass());
        return addEntity(entity, sql);
    }

    /**
     * 添加与配置对象积极属性对应的实体对象的属性到数据库。
     */
    @Override
    public <E> E addPositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getInsertPositive(config);
        return addEntity(entity, sql);
    }

    /**
     * 添加与配置对象消极属性对应的实体对象的属性到数据库。
     */
    @Override
    public <E> E addNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getInsertNegative(config);
        return addEntity(entity, sql);
    }

    /**
     * 批量添加实体对象的全部属性到数据库。
     */
    @Override
    public <E> E[] addBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Entities is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            E entity = entities[i];
            if (entity == null)
                throw new DaoException("Entity is empty.");
            sqlGenerator.generateIdentity(entity);
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getInsertValues(entities[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据Sql更新实体对象。
     */
    private <E> E updateEntity(E entity, String sql) {
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 1)
            throw new DaoException("Update entity failed.");
        return entity;
    }

    /**
     * 更新实体对象的全部属性到数据库。
     */
    @Override
    public <E> E update(E entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getUpdateWhereIdEquals(entity.getClass());
        return updateEntity(entity, sql);
    }

    /**
     * 更新与配置对象积极属性对应的实体对象的属性到数据库。
     */
    @Override
    public <E> E updatePositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 更新与配置对象消极属性对应的实体对象的属性到数据库。
     */
    @Override
    public <E> E updateNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getUpdateNegativeWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 批量更新实体对象的全部属性到数据库。
     */
    @Override
    public <E> E[] updateBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Entities is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            E entity = entities[i];
            if (entity == null)
                throw new DaoException("Entity is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getUpdateWhereIdEquals(entities[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据ID和Sql获取实体对象。
     */
    private <E> E getEntity(Class<E> entityClass, Object identity, String sql) {
        LOGGER.debug(sql);
        try {
            if (EntityUtils.isEntityOrSuper(identity, entityClass)) {
                Object[] params = EntityUtils.getEntityIds(getSqlGenerator().getEntityInfo(entityClass), identity);
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(entityClass), params);
            } else {
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(entityClass), identity);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID或包含ID的实体或父类对象获取实体对象的全部属性。
     */
    @Override
    public <E> E get(Class<E> entityClass, Object identity) {
        if (entityClass == null || identity == null)
            throw new DaoException("Entity or identity is empty.");
        String sql = sqlGenerator.getSelectWhereIdEquals(entityClass);
        return getEntity(entityClass, identity, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象积极属性对应的实体对象的属性。
     */
    @Override
    public <E> E getPositive(E config, Object identity) {
        if (config == null || identity == null)
            throw new DaoException("Config or identity is empty.");
        String sql = sqlGenerator.getSelectPositiveWhereIdEquals(config);
        return getEntity((Class<E>) config.getClass(), identity, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象消极属性对应的实体对象的属性。
     */
    @Override
    public <E> E getNegative(E config, Object identity) {
        if (config == null || identity == null)
            throw new DaoException("Config or identity is empty.");
        String sql = sqlGenerator.getSelectNegativeWhereIdEquals(config);
        return getEntity((Class<E>) config.getClass(), identity, sql);
    }

    /**
     * 根据多个ID或包含ID的实体或父类对象批量获取实体对象的全部属性。
     */
    @Override
    public <E> List<E> getBatch(Class<E> entityClass, Object... identities) {
        if (entityClass == null || identities == null || identities.length == 0)
            throw new DaoException("Entity class or identities is empty.");
        String sql = sqlGenerator.getSelectWhereBatchIds(entityClass, identities.length);
        LOGGER.debug(sql);
        if (EntityUtils.isEntityOrSuper(identities[0], entityClass)) {
            Object[] params = EntityUtils.getEntitiesIds(getSqlGenerator().getEntityInfo(entityClass), identities);
            return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(entityClass), params);
        } else {
            return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(entityClass), identities);
        }
    }

    /**
     * 查询实体对象总数。
     */
    @Override
    public long countAll(Class entityClass) {
        if (entityClass == null)
            throw new DaoException("Entity class is empty.");
        String sql = sqlGenerator.getCountWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class);
    }

    /**
     * 查询全部实体对象。
     */
    @Override
    public <E> List<E> getAll(Class<E> entityClass) {
        if (entityClass == null)
            throw new DaoException("Entity class is empty.");
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(entityClass));
    }

    /**
     * 分页查询全部实体对象。
     */
    @Override
    public <E> PageData<E> getByPage(Class<E> entityClass, PageParam pageParam) {
        if (entityClass == null || pageParam == null)
            throw new DaoException("Entity class or page param is empty.");
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        return queryEntityPage(pageParam, entityClass, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象删除实体对象。
     */
    @Override
    public void delete(Class entityClass, Object identity) {
        if (entityClass == null || identity == null)
            throw new DaoException("Entity class or identity is empty.");
        String sql = sqlGenerator.getDeleteIdEquals(entityClass);
        LOGGER.debug(sql);
        if (EntityUtils.isEntityOrSuper(identity, entityClass)) {
            Object[] params = EntityUtils.getEntityIds(getSqlGenerator().getEntityInfo(entityClass), identity);
            if (jdbcOperations.update(sql, params) < 0)
                throw new DaoException("Delete entity failed.");
        } else {
            if (jdbcOperations.update(sql, identity) < 0)
                throw new DaoException("Delete entity failed.");
        }
    }

    /**
     * 从数据库删除实体对象。
     */
    @Override
    public void deleteEntity(Object entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getDeleteNamedIdEquals(entity.getClass());
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 0)
            throw new DaoException("Delete entity failed.");
    }

    /**
     * 批量从数据库删除实体对象。
     */
    @Override
    public void deleteBatch(Object... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Entities is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            Object entity = entities[i];
            if (entity == null)
                throw new DaoException("Entity is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getDeleteNamedIdEquals(entities[0].getClass());
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
    }

    /**
     * 删除全部实体对象。
     */
    @Override
    public void deleteAll(Class entityClass) {
        if (entityClass == null)
            throw new DaoException("Entity class is empty.");
        String sql = sqlGenerator.getDeleteWhereTrue(entityClass);
        LOGGER.debug(sql);
        jdbcOperations.update(sql);
    }

}
