package com.ewing.user.dao;

import com.ewing.dandelion.GenericBaseDao;
import com.ewing.dandelion.SqlGenerator;
import com.ewing.user.entity.MyUser;
import org.springframework.stereotype.Repository;

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
        String sql = SqlGenerator.getSelectFromWhereTrue(getEntityClass()) + " AND name = ?";
        return this.queryObject(sql, name);
    }
}
