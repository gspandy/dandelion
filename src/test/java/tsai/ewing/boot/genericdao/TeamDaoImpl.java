package tsai.ewing.boot.genericdao;

import org.springframework.stereotype.Repository;
import tsai.ewing.boot.entity.Team;
import tsai.ewing.dandelion.GenericBaseDao;

/**
 * 团队实体泛型DAO。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@Repository
public class TeamDaoImpl extends GenericBaseDao<Team> implements TeamDao {

}
