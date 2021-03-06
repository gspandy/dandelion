package ewing.dandelion;

import ewing.dandelion.generation.EntityUtils;
import ewing.dandelion.generation.SqlGenerator;
import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
public abstract class GenericBaseDao<E> extends SimpleBaseDao implements GenericDao<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericBaseDao.class);

    protected final Class<E> entityClass;

    protected SqlGenerator sqlGenerator;

    /**
     * 快速初始化的构造方法。
     */
    public GenericBaseDao() {
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superclass;
        entityClass = (Class<E>) type.getActualTypeArguments()[0];
    }

    /**
     * 获取泛型的实际类型。
     */
    public Class<E> getEntityClass() {
        return entityClass;
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
     * 设置操作数据库的JdbcOperations。
     */
    @Override
    @Autowired
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        super.setJdbcOperations(jdbcOperations);
    }

    /**
     * 设置操作数据库的命名JdbcOperations。
     */
    @Override
    @Autowired
    public void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations) {
        super.setNamedParamOperations(namedParamOperations);
    }

    /**
     * 私有方法，根据Sql添加实体对象。
     */
    private E addEntity(E entity, String sql) {
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
    public E add(E entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getInsertValues(entityClass);
        return addEntity(entity, sql);
    }

    /**
     * 添加与配置对象积极属性对应的实体对象的属性到数据库。
     */
    @Override
    public E addPositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getInsertPositive(config);
        return addEntity(entity, sql);
    }

    /**
     * 添加与配置对象消极属性对应的实体对象的属性到数据库。
     */
    @Override
    public E addNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getInsertNegative(config);
        return addEntity(entity, sql);
    }

    /**
     * 批量添加实体对象的全部属性到数据库。
     */
    @Override
    public E[] addBatch(E... entities) {
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
        String sql = sqlGenerator.getInsertValues(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据Sql更新实体对象。
     */
    private E updateEntity(E entity, String sql) {
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 1)
            throw new DaoException("Update entity failed.");
        return entity;
    }

    /**
     * 更新实体对象的全部属性到数据库。
     */
    @Override
    public E update(E entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getUpdateWhereIdEquals(entityClass);
        return updateEntity(entity, sql);
    }

    /**
     * 更新与配置对象积极属性对应的实体对象的属性到数据库。
     */
    @Override
    public E updatePositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 更新与配置对象消极属性对应的实体对象的属性到数据库。
     */
    @Override
    public E updateNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Entity or config is empty.");
        String sql = sqlGenerator.getUpdateNegativeWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 批量更新实体对象的全部属性到数据库。
     */
    @Override
    public E[] updateBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Entities is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            E entity = entities[i];
            if (entity == null)
                throw new DaoException("Entity is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getUpdateWhereIdEquals(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据ID和Sql获取实体对象。
     */
    private E getEntity(Object identity, String sql) {
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
    public E get(Object identity) {
        if (identity == null)
            throw new DaoException("Identity is empty.");
        String sql = sqlGenerator.getSelectWhereIdEquals(entityClass);
        return getEntity(identity, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象积极属性对应的实体对象的属性。
     */
    @Override
    public E getPositive(E config, Object identity) {
        if (config == null || identity == null)
            throw new DaoException("Config or identity is empty.");
        String sql = sqlGenerator.getSelectPositiveWhereIdEquals(config);
        return getEntity(identity, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象消极属性对应的实体对象的属性。
     */
    @Override
    public E getNegative(E config, Object identity) {
        if (config == null || identity == null)
            throw new DaoException("Config or identity is empty.");
        String sql = sqlGenerator.getSelectNegativeWhereIdEquals(config);
        return getEntity(identity, sql);
    }

    /**
     * 根据多个ID或包含ID的实体或父类对象批量获取实体对象的全部属性。
     */
    @Override
    public List<E> getBatch(Object... identities) {
        if (identities == null || identities.length == 0)
            throw new DaoException("Identities is empty.");
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
    public long countAll() {
        String sql = sqlGenerator.getCountWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class);
    }

    /**
     * 查询全部实体对象。
     */
    @Override
    public List<E> getAll() {
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(entityClass));
    }

    /**
     * 分页查询全部实体对象。
     */
    @Override
    public PageData<E> getByPage(PageParam pageParam) {
        if (pageParam == null)
            throw new DaoException("Page param is empty.");
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        return queryEntityPage(pageParam, entityClass, sql);
    }

    /**
     * 根据ID或包含ID的实体或父类对象删除实体对象。
     */
    @Override
    public void delete(Object identity) {
        if (identity == null)
            throw new DaoException("Identity is empty.");
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
    public void deleteEntity(E entity) {
        if (entity == null)
            throw new DaoException("Entity is empty.");
        String sql = sqlGenerator.getDeleteNamedIdEquals(entityClass);
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 0)
            throw new DaoException("Delete entity failed.");
    }

    /**
     * 批量从数据库删除实体对象。
     */
    @Override
    public void deleteBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Entities is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            Object entity = entities[i];
            if (entity == null)
                throw new DaoException("Entity is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getDeleteNamedIdEquals(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
    }

    /**
     * 删除全部实体对象。
     */
    @Override
    public void deleteAll() {
        String sql = sqlGenerator.getDeleteWhereTrue(entityClass);
        LOGGER.debug(sql);
        jdbcOperations.update(sql);
    }

}
