package tsai.ewing.dandelion;

import tsai.ewing.dandelion.generation.SqlGenerator;
import tsai.ewing.dandelion.pagination.PageData;
import tsai.ewing.dandelion.pagination.PageParam;

import java.util.List;

/**
 * 基本数据访问接口。
 *
 * @author Ewing
 * @since 2017-03-01
 **/
public interface EntityDao extends SimpleDao {

    /**
     * 获取Sql生成器。
     *
     * @return Sql生成器。
     */
    SqlGenerator getSqlGenerator();

    /**
     * 设置Sql生成器。
     *
     * @param sqlGenerator Sql生成器。
     */
    void setSqlGenerator(SqlGenerator sqlGenerator);

    /**
     * 把对象实例的所有属性插入到数据库。
     *
     * @param entity 要插入到数据库的对象。
     * @return 插入成功的对象。
     */
    <E> E add(E entity);

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     *
     * @param entity 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    <E> E addPositive(E entity, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     *
     * @param entity 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    <E> E addNegative(E entity, E config);

    /**
     * 批量把对象实例的所有属性插入到数据库。
     *
     * @param entities 对象数组。
     * @return 添加成功的对象。
     */
    <E> E[] addBatch(E... entities);

    /**
     * 把对象实例的所有属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @return 更新成功的对象。
     */
    <E> E update(E entity);

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    <E> E updatePositive(E entity, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    <E> E updateNegative(E entity, E config);

    /**
     * 批量更新对象实例的所有属性。
     *
     * @param entities 要更新到数据库的对象。
     * @return 更新成功的对象。
     */
    <E> E[] updateBatch(E... entities);

    /**
     * 根据ID获取指定类型的对象的所有属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param entityClass 指定对象类型。
     * @param id          ID或包含ID值的对象实例。
     * @return 指定类型的对象。
     */
    <E> E get(Class<E> entityClass, Object id);

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param config 指定对象配置。
     * @param id     ID或包含ID值的对象实例。
     * @return 指定类型的对象。
     */
    <E> E getPositive(E config, Object id);

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param config 指定对象配置。
     * @param id     ID或包含ID值的对象实例。
     * @return 指定类型的对象。
     */
    <E> E getNegative(E config, Object id);

    /**
     * 根据ID数组批量获取指定类型的对象的所有属性。
     * 如果实体中有多个ID，则参数为该实体的实例数组。
     *
     * @param entityClass 指定对象类型。
     * @param ids         ID或包含ID值的对象实例数组。
     * @return 指定类型的对象集合。
     */
    <E> List<E> getBatch(Class<E> entityClass, Object... ids);

    /**
     * 查询指定类型的对象总数。
     *
     * @param entityClass 指定对象类型。
     * @return 总记录数。
     */
    long countAll(Class entityClass);

    /**
     * 查询指定类型的所有记录。
     *
     * @param entityClass 指定对象类型。
     * @return 所有记录数据。
     */
    <E> List<E> getAll(Class<E> entityClass);

    /**
     * 分页查询所有记录。
     *
     * @param entityClass 指定对象类型。
     * @param pageParam   分页参数。
     * @return 所有记录数据。
     */
    <E> PageData<E> getByPage(Class<E> entityClass, PageParam pageParam);

    /**
     * 根据对象的ID属性删除对象。
     *
     * @param entity 要删除的数据对象。
     */
    void delete(Object entity);

    /**
     * 批量把对象实例从数据库删除。
     *
     * @param entities 对象数组。
     */
    void deleteBatch(Object... entities);

    /**
     * 根据对象的ID属性删除指定类型的对象。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param entityClass 指定对象类型。
     * @param id          ID或包含ID值的对象实例。
     */
    void deleteById(Class entityClass, Object id);

    /**
     * 删除所有对象。
     *
     * @param entityClass 指定对象类型。
     */
    void deleteAll(Class entityClass);

}
