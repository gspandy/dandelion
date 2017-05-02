package com.ewing.dandelion.pagination;

import com.ewing.dandelion.GenericDao;

import java.util.Map;

/**
 * 基本数据访问接口。
 *
 * @author Ewing
 * @since 2017-03-01
 **/
public interface GenericPageableDao<T> extends GenericDao<T> {

    /**
     * 分页查询多条记录并封装成指定类型的对象集合。
     *
     * @param querySql 查询语句。
     * @return 指定类型的对象。
     */
    PageData<T> queryPageData(PageParam pageParam, String querySql, Object... params);

    /**
     * 分页查询多条记录并封装成Map集合。
     *
     * @param querySql 查询语句。
     * @return 存储结果的Map集合。
     */
    PageData<Map<String, Object>> queryPageMap(PageParam pageParam, String querySql, Object... params);

}
