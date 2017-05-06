package com.ewing.user.dao;

import com.ewing.dandelion.SqlGenerator;
import com.ewing.dandelion.pagination.GenericPaginationDao;
import com.ewing.user.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author Ewing
 * @since 2017-04-21
 **/
@Repository
public class UserDaoImpl extends GenericPaginationDao<User> implements UserDao {

    /**
     * 自定义查询，根据名称查询。
     */
    @Override
    public User findByName(String name) {
        String sql = SqlGenerator.getSelectFromWhereTrue(getEntityClass()) + " AND name = ?";
        return this.queryObject(sql, name);
    }
}
