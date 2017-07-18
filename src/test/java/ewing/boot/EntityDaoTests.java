package ewing.boot;

import ewing.boot.entity.MyUser;
import ewing.dandelion.EntityDao;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EntityDaoTests {

    @Autowired
    private EntityDao entityDao;

    /**
     * 创建属性齐全的User对象。
     */
    private MyUser createUser() {
        MyUser myUser = new MyUser();
        myUser.setName(RandomString.randomChinese(3));
        myUser.setDescription("我是" + myUser.getName() + "。");
        myUser.setLevel(5);
        myUser.setDateValue(new Date());
        myUser.setBoolValue(true);
        myUser.setBigDecimal(new BigDecimal("123.23"));
        myUser.setIntValue(22323);
        myUser.setLongValue(32345L);
        myUser.setDoubleValue(423.23);
        myUser.setFloatValue(523.23F);
        myUser.setShortValue((short) 623);
        myUser.setBytesValue(new byte[]{1, 5, 12});
        return myUser;
    }

    /**
     * 初始化数据。
     */
    private MyUser addUser() {
        MyUser myUser = createUser();
        entityDao.add(myUser);
        return myUser;
    }

    /**
     * 清理测试数据。
     */
    private void clean(MyUser... myUsers) {
        entityDao.deleteBatch(myUsers);
    }

    @Test
    public void addUserTest() {
        // 保存全部属性
        MyUser user = createUser();
        entityDao.add(user);
        Assert.assertTrue(StringUtils.hasText(user.getUserId()));
        // 清理测试数据
        clean(user);

        // 批量添加对象
        MyUser[] users = {createUser(), createUser(), createUser()};
        entityDao.addBatch(users);
        Assert.assertTrue(entityDao.countAll(MyUser.class) >= users.length);
        clean(users);

        // 只保存name属性
        user = createUser();
        MyUser config = new MyUser();
        config.setName("");
        entityDao.addPositive(user, config);
        MyUser myUser = entityDao.get(MyUser.class, user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getDescription());
        // 清理测试数据
        clean(user);

        // 屏蔽name属性
        user = createUser();
        config = new MyUser();
        config.setName("");
        entityDao.addNegative(user, config);
        myUser = entityDao.get(MyUser.class, user.getUserId());
        Assert.assertNotNull(myUser.getDescription());
        Assert.assertNull(myUser.getName());
        // 清理测试数据
        clean(user);
    }

    @Test
    public void updateUserTest() {
        // 更新对象全部属性
        MyUser myUser = addUser();
        myUser.setName(RandomString.randomChinese(3));
        myUser.setDescription("我是" + myUser.getName() + "。");
        myUser.setLevel(7);
        myUser.setDateValue(new Date());
        myUser.setBoolValue(false);
        myUser.setBigDecimal(new BigDecimal("1123.23"));
        myUser.setIntValue(122323);
        myUser.setLongValue(132345L);
        myUser.setDoubleValue(1423.23);
        myUser.setFloatValue(1523.23F);
        myUser.setShortValue((short) 1623);
        myUser.setBytesValue(new byte[]{11, 15, 112});
        entityDao.update(myUser);
        MyUser result = entityDao.get(MyUser.class, myUser.getUserId());
        Assert.assertTrue(result.getName().equals(myUser.getName()));

        // 只更新name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLevel(8);
        entityDao.updatePositive(myUser, config);
        result = entityDao.get(MyUser.class, myUser.getUserId());
        Assert.assertTrue(result.getName().equals(myUser.getName()));
        Assert.assertTrue(!result.getLevel().equals(myUser.getLevel()));

        // 屏蔽更新name属性
        config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLevel(8);
        entityDao.updateNegative(myUser, config);
        result = entityDao.get(MyUser.class, myUser.getUserId());
        Assert.assertTrue(!result.getName().equals(myUser.getName()));
        Assert.assertTrue(result.getLevel().equals(myUser.getLevel()));

        // 批量更新对象
        MyUser[] users = {createUser(), createUser(), createUser()};
        entityDao.addBatch(users);
        for (MyUser user : users)
            user.setName(RandomString.randomChinese(3));
        entityDao.updateBatch(users);
        for (MyUser user : users)
            Assert.assertTrue(user.getName().equals(entityDao.get(MyUser.class, user.getUserId()).getName()));
        clean(users);

        // 清理测试数据
        clean(myUser);
    }

    @Test
    public void getUserTest() {
        // 根据ID获取对象
        MyUser user = addUser();
        MyUser myUser = entityDao.get(MyUser.class, user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getName().equals(myUser.getName()));

        // 只取name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser = entityDao.getPositive(config, user.getUserId());
        Assert.assertNull(myUser.getDescription());
        Assert.assertNotNull(myUser.getName());

        // 屏蔽name属性
        config = new MyUser();
        config.setName("");
        myUser = entityDao.getNegative(config, user.getUserId());
        Assert.assertNull(myUser.getName());
        Assert.assertNotNull(myUser.getDescription());

        // 统计总数
        long count = entityDao.countAll(MyUser.class);
        Assert.assertTrue(count > 0);

        // 查询所有
        List<MyUser> myUsers = entityDao.getAll(MyUser.class);
        Assert.assertTrue(myUsers.size() > 0);

        // 根据ID批量查询
        MyUser user2 = addUser();
        myUsers = entityDao.getBatch(MyUser.class, user.getUserId(), user2.getUserId());
        Assert.assertTrue(myUsers.size() > 1);

        // 清理测试数据
        clean(user, user2);
    }

    @Test
    public void deleteUserTest() {
        // 删除对象
        MyUser user = addUser();
        entityDao.deleteEntity(user);
        MyUser myUser = entityDao.get(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);

        // 根据ID删除对象
        user = addUser();
        entityDao.delete(MyUser.class, user.getUserId());
        myUser = entityDao.get(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);

        // 批量删除对象
        MyUser[] users = new MyUser[]{createUser(), createUser(), createUser()};
        entityDao.addBatch(users);
        entityDao.deleteBatch(users);
        myUser = entityDao.get(MyUser.class, users[0].getUserId());
        Assert.assertNull(myUser);

        // 删除全部对象
        user = addUser();
        entityDao.deleteAll(MyUser.class);
        myUser = entityDao.get(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);
    }

    @Test
    public void queryUserTest() {
        MyUser user = addUser();

        // 分页查询所有
        PageData<MyUser> pageUsers = entityDao.getByPage(MyUser.class, new PageParam(0, 10));
        Assert.assertTrue(pageUsers.getContent().size() > 0);

        // 分页查询
        String sql = new SqlGenerator().getSelectWhereTrue(MyUser.class);
        PageData<MyUser> users = entityDao.queryEntityPage(new PageParam(), MyUser.class, sql);
        Assert.assertTrue(users.getTotal() > 0);

        // 清理测试数据
        clean(user);
    }

}
