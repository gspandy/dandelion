package com.ewing.dandelion.pagination;

import com.ewing.dandelion.DaoException;
import com.ewing.dandelion.GenericBaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Map;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
public abstract class GenericPaginationDao<T> extends GenericBaseDao<T> implements GenericPageableDao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericPaginationDao.class);

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
        }
        if (pageParam.isCount() && pageData.getTotal() == 0) return pageData;
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
            LOGGER.info(countSql);
            pageData.setTotal(this.queryLong(countSql, params));
        }
        if (pageParam.isCount() && pageData.getTotal() == 0) return pageData;
        String pageSql = querySql + " LIMIT " + pageParam.getOffset() + "," + pageParam.getLimit();
        LOGGER.info(pageSql);
        pageData.setContent(this.getJdbcOperations().queryForList(pageSql, params));
        return pageData;
    }

}
