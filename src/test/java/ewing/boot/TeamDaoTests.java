package ewing.boot;

import ewing.boot.entity.Team;
import ewing.boot.entity.TeamId;
import ewing.boot.genericdao.TeamDao;
import ewing.dandelion.generation.SqlGenerator;
import ewing.dandelion.pagination.PageData;
import ewing.dandelion.pagination.PageParam;
import ewing.utils.RandomString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 团队实体泛型DAO测试。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TeamDaoTests {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private SqlGenerator sqlGenerator;

    /**
     * 创建属性齐全的Team对象。
     */
    private Team createTeam() {
        Team myTeam = new Team();
        myTeam.setName(RandomString.randomChinese(3));
        myTeam.setDescription(RandomString.randomChinese(5));
        myTeam.setCreateTime(new Date());
        return myTeam;
    }

    /**
     * 初始化数据。
     */
    private Team addTeam() {
        Team myTeam = createTeam();
        teamDao.add(myTeam);
        return myTeam;
    }

    /**
     * 清理测试数据。
     */
    private void clean(Team... myTeams) {
        teamDao.deleteBatch(myTeams);
    }

    @Test
    public void addTeamTest() {
        // 保存全部属性
        Team team = createTeam();
        teamDao.add(team);
        Assert.assertTrue(StringUtils.hasText(team.getMyId()));
        Assert.assertTrue(StringUtils.hasText(team.getYourId()));
        Assert.assertTrue(StringUtils.hasText(team.getHisId()));
        // 清理测试数据
        clean(team);

        // 批量添加对象
        Team[] teams = {createTeam(), createTeam(), createTeam()};
        teamDao.addBatch(teams);
        Assert.assertTrue(teamDao.countAll() >= teams.length);
        clean(teams);

        // 只保存name属性
        team = createTeam();
        Team config = new Team();
        config.setName("");
        teamDao.addPositive(team, config);
        Team myTeam = teamDao.get(team);
        Assert.assertNotNull(myTeam.getName());
        Assert.assertNull(myTeam.getCreateTime());
        // 清理测试数据
        clean(team);

        // 屏蔽name属性
        team = createTeam();
        config = new Team();
        config.setName("");
        teamDao.addNegative(team, config);
        myTeam = teamDao.get(team);
        Assert.assertNotNull(myTeam.getCreateTime());
        Assert.assertNull(myTeam.getName());
        // 清理测试数据
        clean(team);
    }

    @Test
    public void updateTeamTest() {
        // 更新对象全部属性
        Team myTeam = addTeam();
        myTeam.setName(RandomString.randomChinese(3));
        myTeam.setCreateTime(new Date());
        teamDao.update(myTeam);
        Team result = teamDao.get(myTeam);
        Assert.assertTrue(result.getName().equals(myTeam.getName()));

        // 只更新name属性
        Team config = new Team();
        config.setName("");
        myTeam.setName(RandomString.randomChinese(3));
        myTeam.setDescription(RandomString.randomChinese(5));
        teamDao.updatePositive(myTeam, config);
        result = teamDao.get(myTeam);
        Assert.assertTrue(result.getName().equals(myTeam.getName()));
        Assert.assertTrue(!result.getDescription().equals(myTeam.getDescription()));

        // 屏蔽更新name属性
        config = new Team();
        config.setName("");
        myTeam.setName(RandomString.randomChinese(3));
        myTeam.setDescription(RandomString.randomChinese(5));
        teamDao.updateNegative(myTeam, config);
        result = teamDao.get(myTeam);
        Assert.assertTrue(!result.getName().equals(myTeam.getName()));
        Assert.assertTrue(result.getDescription().equals(myTeam.getDescription()));

        // 批量更新对象
        Team[] teams = {createTeam(), createTeam(), createTeam()};
        teamDao.addBatch(teams);
        for (Team team : teams)
            team.setName(RandomString.randomChinese(3));
        teamDao.updateBatch(teams);
        for (Team team : teams)
            Assert.assertTrue(team.getName().equals(teamDao.get(team).getName()));
        clean(teams);

        // 清理测试数据
        clean(myTeam);
    }

    @Test
    public void getTeamTest() {
        // 根据ID获取对象
        Team team = addTeam();
        // 使用父类作为ID 父类中包含其所有ID属性
        TeamId teamId = new TeamId(team.getMyId(), team.getYourId(), team.getHisId());
        Team myTeam = teamDao.get(teamId);
        // 没有异常 简单验证
        Assert.assertTrue(team.getName().equals(myTeam.getName()));

        // 只取name属性
        Team config = new Team();
        config.setName("");
        myTeam = teamDao.getPositive(config, team);
        Assert.assertNull(myTeam.getDescription());
        Assert.assertNotNull(myTeam.getName());

        // 屏蔽name属性
        config = new Team();
        config.setName("");
        myTeam = teamDao.getNegative(config, team);
        Assert.assertNull(myTeam.getName());
        Assert.assertNotNull(myTeam.getDescription());

        // 统计总数
        long count = teamDao.countAll();
        Assert.assertTrue(count > 0);

        // 获取所有
        List<Team> myTeams = teamDao.getAll();
        Assert.assertTrue(myTeams.size() > 0);

        // 根据ID批量获取
        Team team1 = addTeam();
        Team team2 = addTeam();
        myTeams = teamDao.getBatch(team1, team2);
        Assert.assertTrue(myTeams.size() > 1);

        // 清理测试数据
        clean(team, team1, team2);
    }

    @Test
    public void deleteTeamTest() {
        // 删除对象
        Team team = addTeam();
        teamDao.deleteEntity(team);
        Team myTeam = teamDao.get(team);
        Assert.assertNull(myTeam);

        // 根据ID删除对象
        team = addTeam();
        teamDao.delete(team);
        myTeam = teamDao.get(team);
        Assert.assertNull(myTeam);

        // 批量删除对象
        Team[] teams = new Team[]{createTeam(), createTeam(), createTeam()};
        teamDao.addBatch(teams);
        teamDao.deleteBatch(teams);
        myTeam = teamDao.get(teams[0]);
        Assert.assertNull(myTeam);

        // 删除全部对象
        team = addTeam();
        teamDao.deleteAll();
        myTeam = teamDao.get(team);
        Assert.assertNull(myTeam);
    }

    @Test
    public void queryTeamTest() {
        Team team = addTeam();
        Team team2 = addTeam();

        // 分页查询所有
        PageData<Team> pageTeams = teamDao.getByPage(new PageParam(0, 10));
        Assert.assertTrue(pageTeams.getContent().size() > 0);

        // 分页查询
        String sql = sqlGenerator.getSelectWhereTrue(Team.class);
        PageData<Team> teams = teamDao.queryEntityPage(new PageParam(), Team.class, sql);
        Assert.assertTrue(teams.getTotal() > 0);

        // 清理测试数据
        clean(team, team2);
    }

}
