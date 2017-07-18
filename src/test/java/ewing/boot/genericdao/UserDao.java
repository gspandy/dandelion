package ewing.boot.genericdao;

import ewing.boot.entity.MyUser;
import ewing.dandelion.GenericDao;

/**
 * 用户实体泛型DAO。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
public interface UserDao extends GenericDao<MyUser> {

    /**
     * 自定义查询，根据名称查询。
     */
    MyUser findByName(String name);

    /**
     * 当名称存在时根据名称查询。
     */
    MyUser findMyUser(String name, String description, Integer level);
}
