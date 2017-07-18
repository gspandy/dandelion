package tsai.ewing.dandelion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import tsai.ewing.dandelion.generation.EntityUtils;
import tsai.ewing.dandelion.generation.SqlGenerator;
import tsai.ewing.dandelion.pagination.PageData;
import tsai.ewing.dandelion.pagination.PageParam;

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
     * 私有方法，根据Sql添加对象。
     */
    private E addEntity(E entity, String sql) {
        LOGGER.debug(sql);
        sqlGenerator.generateIdentity(entity);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 1)
            throw new DaoException("Add entity failed.");
        return entity;
    }

    /**
     * 把对象实例的所有属性插入到数据库。
     */
    @Override
    public E add(E entity) {
        if (entity == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getInsertValues(entityClass);
        return addEntity(entity, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public E addPositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getInsertPositive(config);
        return addEntity(entity, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public E addNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getInsertNegative(config);
        return addEntity(entity, sql);
    }

    /**
     * 批量把对象实例的所有属性插入到数据库。
     */
    @Override
    public E[] addBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Object instances is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            E entity = entities[i];
            if (entity == null)
                throw new DaoException("Object instance is empty.");
            sqlGenerator.generateIdentity(entity);
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getInsertValues(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据Sql更新对象。
     */
    private E updateEntity(E entity, String sql) {
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 1)
            throw new DaoException("Update entity failed.");
        return entity;
    }

    /**
     * 把对象实例的所有属性更新到数据库。
     */
    @Override
    public E update(E entity) {
        if (entity == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getUpdateWhereIdEquals(entityClass);
        return updateEntity(entity, sql);
    }

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public E updatePositive(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getUpdatePositiveWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public E updateNegative(E entity, E config) {
        if (entity == null || config == null)
            throw new DaoException("Object instance or config is empty.");
        String sql = sqlGenerator.getUpdateNegativeWhereIdEquals(config);
        return updateEntity(entity, sql);
    }

    /**
     * 批量更新对象实例的所有属性。
     */
    @Override
    public E[] updateBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Object instances is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            E entity = entities[i];
            if (entity == null)
                throw new DaoException("Object instance is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getUpdateWhereIdEquals(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
        return entities;
    }

    /**
     * 私有方法，根据ID和Sql获取对象。
     */
    private E getEntity(Object id, String sql) {
        LOGGER.debug(sql);
        try {
            if (EntityUtils.isEntityOrSuper(id, entityClass)) {
                Object[] params = EntityUtils.getEntityIds(getSqlGenerator().getEntityInfo(entityClass), id);
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(entityClass), params);
            } else {
                return jdbcOperations.queryForObject(sql, new BeanPropertyRowMapper<>(entityClass), id);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取对象的所有属性。
     */
    @Override
    public E get(Object id) {
        if (id == null)
            throw new DaoException("Identity is empty.");
        String sql = sqlGenerator.getSelectWhereIdEquals(entityClass);
        return getEntity(id, sql);
    }

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     */
    @Override
    public E getPositive(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("Config entity or identity is empty.");
        String sql = sqlGenerator.getSelectPositiveWhereIdEquals(config);
        return getEntity(id, sql);
    }

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     */
    @Override
    public E getNegative(E config, Object id) {
        if (config == null || id == null)
            throw new DaoException("Config entity or identity is empty.");
        String sql = sqlGenerator.getSelectNegativeWhereIdEquals(config);
        return getEntity(id, sql);
    }

    /**
     * 根据ID数组批量获取对象的所有属性。
     */
    @Override
    public List<E> getBatch(Object... ids) {
        if (ids == null || ids.length == 0)
            throw new DaoException("Identities is empty.");
        String sql = sqlGenerator.getSelectWhereBatchIds(entityClass, ids.length);
        LOGGER.debug(sql);
        // 如果则参数为该实体的实例 使用反射取ID值
        if (EntityUtils.isEntityOrSuper(ids[0], entityClass)) {
            Object[] params = EntityUtils.getEntitiesIds(getSqlGenerator().getEntityInfo(entityClass), ids);
            return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(entityClass), params);
        } else {
            return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(entityClass), ids);
        }
    }

    /**
     * 查询总记录数。
     */
    @Override
    public long countAll() {
        String sql = sqlGenerator.getCountWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.queryForObject(sql, Long.class);
    }

    /**
     * 获取所有记录。
     */
    @Override
    public List<E> getAll() {
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        LOGGER.debug(sql);
        return jdbcOperations.query(sql, BeanPropertyRowMapper.newInstance(entityClass));
    }

    /**
     * 分页查询所有记录。
     */
    @Override
    public PageData<E> getByPage(PageParam pageParam) {
        String sql = sqlGenerator.getSelectWhereTrue(entityClass);
        return queryEntityPage(pageParam, entityClass, sql);
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public void delete(E entity) {
        if (entity == null)
            throw new DaoException("Object instance is empty.");
        String sql = sqlGenerator.getDeleteNamedIdEquals(entityClass);
        LOGGER.debug(sql);
        if (namedParamOperations.update(sql, new BeanPropertySqlParameterSource(entity)) < 0)
            throw new DaoException("Delete entity failed.");
    }

    /**
     * 批量把对象实例从数据库删除。
     */
    @Override
    public void deleteBatch(E... entities) {
        if (entities == null || entities.length == 0)
            throw new DaoException("Object instances is empty.");
        SqlParameterSource[] sources = new SqlParameterSource[entities.length];
        for (int i = 0; i < entities.length; i++) {
            Object entity = entities[i];
            if (entity == null)
                throw new DaoException("Object instance is empty.");
            sources[i] = new BeanPropertySqlParameterSource(entity);
        }
        String sql = sqlGenerator.getDeleteNamedIdEquals(entityClass);
        LOGGER.debug(sql);
        namedParamOperations.batchUpdate(sql, sources);
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public void deleteById(Object id) {
        if (id == null)
            throw new DaoException("Identity is empty.");
        String sql = sqlGenerator.getDeleteIdEquals(entityClass);
        LOGGER.debug(sql);
        if (EntityUtils.isEntityOrSuper(id, entityClass)) {
            Object[] params = EntityUtils.getEntityIds(getSqlGenerator().getEntityInfo(entityClass), id);
            if (jdbcOperations.update(sql, params) < 0)
                throw new DaoException("Delete entity failed.");
        } else {
            if (jdbcOperations.update(sql, id) < 0)
                throw new DaoException("Delete entity failed.");
        }
    }

    /**
     * 删除全部对象。
     */
    @Override
    public void deleteAll() {
        String sql = sqlGenerator.getDeleteWhereTrue(entityClass);
        LOGGER.debug(sql);
        jdbcOperations.update(sql);
    }

}
