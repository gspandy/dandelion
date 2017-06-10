package com.ewing.usertest.dao;

import com.ewing.dandelion.GenericBaseDao;
import com.ewing.usertest.entity.MyUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ewing
 * @since 2017-04-21
 **/
@Repository
public class UserDaoImpl extends GenericBaseDao<MyUser> implements UserDao {

    /**
     * 自定义查询，根据名称查询。
     */
    @Override
    public MyUser findByName(String name) {
        String sql = getSqlGenerator().getSelectWhereTrue(getEntityClass()) + " AND name = ?";
        return queryObject(getEntityClass(), sql, name);
    }

    /**
     * 根据自定义参数条件查询。
     */
    @Override
    public MyUser findNameAndLong(String name, Long longValue) {
        StringBuilder sql = new StringBuilder(getSqlGenerator().getSelectWhereTrue(getEntityClass()));
        List<Object> params = new ArrayList<>();
        appendSqlParam(sql, " AND name = ?", params, name);
        appendHasParam(sql, " AND longValue = ?", params, longValue);
        return queryObject(getEntityClass(), sql.toString(), params.toArray());
    }
}
