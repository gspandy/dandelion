package com.ewing.user.dao;

import com.ewing.dandelion.GenericDao;
import com.ewing.user.entity.MyUser;

/**
 * @author Ewing
 * @since 2017-04-21
 **/
public interface UserDao extends GenericDao<MyUser> {

    /**
     * 自定义查询，根据名称查询。
     */
    MyUser findByName(String name);

}
