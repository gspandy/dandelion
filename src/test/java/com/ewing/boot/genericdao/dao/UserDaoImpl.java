package com.ewing.boot.genericdao.dao;

import com.ewing.boot.genericdao.entity.MyUser;
import com.ewing.dandelion.GenericBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体泛型DAO接口。
 *
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
    public MyUser findMyUser(String name, String description, Integer level) {
        StringBuilder sql = new StringBuilder(getSqlGenerator().getSelectWhereTrue(getEntityClass()));
        List<Object> params = new ArrayList<>();

        // 当名称有值时根据名称精确查询
        appendHasParam(sql, " AND name = ?", params, name);

        // 当描述有值时根据描述模糊查询
        if (StringUtils.hasText(description))
            appendSqlParam(sql, " AND description like ?", params, "%" + description + "%");

        // 直接添加等级查询条件
        appendSqlParam(sql, " AND level = ?", params, level);

        return queryObject(getEntityClass(), sql.toString(), params.toArray());
    }
}
