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
public interface GenericDao<E> extends SimpleDao {

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
    E add(E entity);

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     *
     * @param entity 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    E addPositive(E entity, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     *
     * @param entity 要插入到数据库的对象。
     * @param config 配置对象。
     * @return 插入成功的对象。
     */
    E addNegative(E entity, E config);

    /**
     * 批量把对象实例的所有属性插入到数据库。
     *
     * @param entities 对象数组。
     * @return 添加成功的对象。
     */
    E[] addBatch(E... entities);

    /**
     * 把对象实例的所有属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @return 更新成功的对象。
     */
    E update(E entity);

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    E updatePositive(E entity, E config);

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     *
     * @param entity 要更新到数据库的对象。
     * @param config 配置对象。
     * @return 更新成功的对象。
     */
    E updateNegative(E entity, E config);

    /**
     * 批量更新对象实例的所有属性。
     *
     * @param entities 要更新到数据库的对象。
     * @return 更新成功的对象。
     */
    E[] updateBatch(E... entities);

    /**
     * 根据ID获取对象的所有属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param id ID或包含ID值的对象实例。
     * @return 对象实例。
     */
    E get(Object id);

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param config 指定对象配置。
     * @param id     ID或包含ID值的对象实例。
     * @return 对象实例。
     */
    E getPositive(E config, Object id);

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param config 指定对象配置。
     * @param id     ID或包含ID值的对象实例。
     * @return 对象实例。
     */
    E getNegative(E config, Object id);

    /**
     * 根据ID数组批量获取对象的所有属性。
     * 如果实体中有多个ID，则参数为该实体的实例数组。
     *
     * @param ids ID或包含ID值的对象实例数组。
     * @return 对象实例。
     */
    List<E> getBatch(Object... ids);

    /**
     * 查询总记录数。
     *
     * @return 总记录数。
     */
    long countAll();

    /**
     * 查询所有记录。
     *
     * @return 所有记录数据。
     */
    List<E> getAll();

    /**
     * 分页查询所有记录。
     *
     * @param pageParam 分页参数。
     * @return 所有记录数据。
     */
    PageData<E> getByPage(PageParam pageParam);

    /**
     * 根据对象的ID属性删除对象。
     *
     * @param entity 要删除的数据对象。
     */
    void delete(E entity);

    /**
     * 批量把对象实例从数据库删除。
     *
     * @param entities 对象数组。
     */
    void deleteBatch(E... entities);

    /**
     * 根据对象的ID属性删除对象。
     * 如果实体中有多个ID，则参数为该实体的实例。
     *
     * @param id ID或包含ID值的对象实例。
     */
    void deleteById(Object id);

    /**
     * 删除所有对象。
     */
    void deleteAll();

}
