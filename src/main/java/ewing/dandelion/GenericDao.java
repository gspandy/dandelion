package ewing.dandelion;

import ewing.dandelion.generation.SqlGenerator;
import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;

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
     */
    SqlGenerator getSqlGenerator();

    /**
     * 设置Sql生成器。
     */
    void setSqlGenerator(SqlGenerator sqlGenerator);

    /**
     * 添加实体对象的全部属性到数据库。
     *
     * @param entity 实体对象。
     * @return 添加成功的实体对象。
     */
    E add(E entity);

    /**
     * 添加与配置对象积极属性对应的实体对象的属性到数据库。
     *
     * @param entity 实体对象。
     * @param config 配置对象。
     * @return 添加成功的实体对象。
     */
    E addPositive(E entity, E config);

    /**
     * 添加与配置对象消极属性对应的实体对象的属性到数据库。
     *
     * @param entity 实体对象。
     * @param config 配置对象。
     * @return 添加成功的实体对象。
     */
    E addNegative(E entity, E config);

    /**
     * 批量添加实体对象的全部属性到数据库。
     *
     * @param entities 多个实体对象。
     * @return 添加成功的实体对象。
     */
    E[] addBatch(E... entities);

    /**
     * 更新实体对象的全部属性到数据库。
     *
     * @param entity 实体对象。
     * @return 更新成功的实体对象。
     */
    E update(E entity);

    /**
     * 更新与配置对象积极属性对应的实体对象的属性到数据库。
     *
     * @param entity 实体对象。
     * @param config 配置对象。
     * @return 更新成功的实体对象。
     */
    E updatePositive(E entity, E config);

    /**
     * 更新与配置对象消极属性对应的实体对象的属性到数据库。
     *
     * @param entity 实体对象。
     * @param config 配置对象。
     * @return 更新成功的实体对象。
     */
    E updateNegative(E entity, E config);

    /**
     * 批量更新实体对象的全部属性到数据库。
     *
     * @param entities 多个实体对象。
     * @return 更新成功的实体对象。
     */
    E[] updateBatch(E... entities);

    /**
     * 根据ID或包含ID的实体或父类对象获取实体对象的全部属性。
     *
     * @param identity ID或包含ID的实体或父类对象。
     * @return 实体对象。
     */
    E get(Object identity);

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象积极属性对应的实体对象的属性。
     *
     * @param config   配置对象。
     * @param identity ID或包含ID的实体或父类对象。
     * @return 实体对象。
     */
    E getPositive(E config, Object identity);

    /**
     * 根据ID或包含ID的实体或父类对象获取与配置对象消极属性对应的实体对象的属性。
     *
     * @param config   配置对象。
     * @param identity ID或包含ID的实体或父类对象。
     * @return 实体对象。
     */
    E getNegative(E config, Object identity);

    /**
     * 根据多个ID或包含ID的实体或父类对象批量获取实体对象的全部属性。
     *
     * @param identities 多个ID或包含ID的实体或父类对象。
     * @return 实体对象。
     */
    List<E> getBatch(Object... identities);

    /**
     * 查询实体对象总数。
     *
     * @return 实体对象总数。
     */
    long countAll();

    /**
     * 查询全部实体对象。
     *
     * @return 全部实体对象。
     */
    List<E> getAll();

    /**
     * 分页查询全部实体对象。
     *
     * @param pageParam 分页参数。
     * @return 实体对象分页数据。
     */
    PageData<E> getByPage(PageParam pageParam);

    /**
     * 根据ID或包含ID的实体或父类对象删除实体对象。
     *
     * @param identity ID或包含ID的实体或父类对象。
     */
    void delete(Object identity);

    /**
     * 从数据库删除实体对象。
     *
     * @param entity 实体对象。
     */
    void deleteEntity(E entity);

    /**
     * 批量从数据库删除实体对象。
     *
     * @param entities 多个实体对象。
     */
    void deleteBatch(E... entities);

    /**
     * 删除全部实体对象。
     */
    void deleteAll();

}
