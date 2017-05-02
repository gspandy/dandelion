package com.ewing.user.dao;

import com.ewing.dandelion.pagination.GenericPaginationDao;
import com.ewing.user.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author Ewing
 * @since 2017-04-21
 **/
@Repository
public class UserDaoImpl extends GenericPaginationDao<User> implements UserDao {

}
