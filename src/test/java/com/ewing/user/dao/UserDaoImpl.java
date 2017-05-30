package com.ewing.user.dao;

import com.ewing.dandelion.GenericBaseDao;
import com.ewing.dandelion.SqlGenerator;
import com.ewing.user.entity.MyUser;
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
        String sql = SqlGenerator.getSelectWhereTrue(getEntityClass()) + " AND name = ?";
        return this.queryObject(sql, name);
    }

    /**
     * 当名称存在时根据名称查询。
     */
    @Override
    public MyUser findNameAndLong(String name, Long along) {
        StringBuilder sql = new StringBuilder(SqlGenerator.getSelectWhereTrue(getEntityClass()));
        List<Object> params = new ArrayList<>();
        this.appendHasParam(sql, " AND name = ?", params, name);
        this.appendHasParam(sql, " AND longValue = ?", params, along);
        return this.queryObject(sql.toString(), params.toArray());
    }
}
