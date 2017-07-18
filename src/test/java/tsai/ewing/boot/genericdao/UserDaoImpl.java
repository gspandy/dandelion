package tsai.ewing.boot.genericdao;

import org.springframework.stereotype.Repository;
import tsai.ewing.boot.entity.MyUser;
import tsai.ewing.dandelion.GenericBaseDao;
import tsai.ewing.dandelion.SqlBuilder;

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
        String sql = sqlGenerator.getSelectWhereTrue(entityClass) + " AND name = ?";
        return queryEntity(entityClass, sql, name);
    }

    /**
     * 根据自定义参数条件查询。
     */
    @Override
    public MyUser findMyUser(String name, String description, Integer level) {
        SqlBuilder sqlBuilder = new SqlBuilder(sqlGenerator.getSelectWhereTrue(entityClass));

        // 当名称有值时根据名称精确查询
        sqlBuilder.appendHasValue(" AND name = ?", name);

        // 当描述有值时根据描述模糊查询
        sqlBuilder.appendContains(" AND description LIKE ?", description);

        // 直接添加等级查询条件
        sqlBuilder.appendSqlParams(" AND level = ?", level);

        return queryEntity(entityClass, sqlBuilder.toString(), sqlBuilder.getParams());
    }
}
