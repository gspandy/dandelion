package com.ewing.boot.genericdao;

import com.ewing.boot.entity.Team;
import com.ewing.dandelion.GenericBaseDao;
import org.springframework.stereotype.Repository;

/**
 * 团队实体泛型DAO。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@Repository
public class TeamDaoImpl extends GenericBaseDao<Team> implements TeamDao {

}
