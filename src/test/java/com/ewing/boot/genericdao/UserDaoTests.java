package com.ewing.boot.genericdao;

import com.ewing.boot.common.RandomString;
import com.ewing.boot.genericdao.dao.UserDao;
import com.ewing.boot.genericdao.entity.MyUser;
import com.ewing.dandelion.generation.SqlGenerator;
import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
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

/**
 * 用户实体泛型DAO测试。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserDaoTests {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SqlGenerator sqlGenerator;

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
        userDao.add(myUser);
        return myUser;
    }

    /**
     * 清理测试数据。
     */
    private void clean(MyUser... myUsers) {
        userDao.deleteBatch(myUsers);
    }

    @Test
    public void addUserTest() {
        // 保存全部属性
        MyUser user = createUser();
        userDao.add(user);
        Assert.assertTrue(StringUtils.hasText(user.getUserId()));
        // 清理测试数据
        clean(user);

        // 批量添加对象
        MyUser[] users = {createUser(), createUser(), createUser()};
        userDao.addBatch(users);
        Assert.assertTrue(userDao.countAll() >= users.length);
        clean(users);

        // 只保存name属性
        user = createUser();
        MyUser config = new MyUser();
        config.setName("");
        userDao.addPositive(user, config);
        MyUser myUser = userDao.get(user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getDescription());
        // 清理测试数据
        clean(user);

        // 屏蔽name属性
        user = createUser();
        config = new MyUser();
        config.setName("");
        userDao.addNegative(user, config);
        myUser = userDao.get(user.getUserId());
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
        userDao.update(myUser);
        MyUser result = userDao.get(myUser.getUserId());
        Assert.assertTrue(result.getName().equals(myUser.getName()));

        // 只更新name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLevel(8);
        userDao.updatePositive(myUser, config);
        result = userDao.get(myUser.getUserId());
        Assert.assertTrue(result.getName().equals(myUser.getName()));
        Assert.assertTrue(!result.getLevel().equals(myUser.getLevel()));

        // 屏蔽更新name属性
        config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLevel(8);
        userDao.updateNegative(myUser, config);
        result = userDao.get(myUser.getUserId());
        Assert.assertTrue(!result.getName().equals(myUser.getName()));
        Assert.assertTrue(result.getLevel().equals(myUser.getLevel()));

        // 批量更新对象
        MyUser[] users = {createUser(), createUser(), createUser()};
        userDao.addBatch(users);
        for (MyUser user : users)
            user.setName(RandomString.randomChinese(3));
        userDao.updateBatch(users);
        for (MyUser user : users)
            Assert.assertTrue(user.getName().equals(userDao.get(user.getUserId()).getName()));
        clean(users);

        // 清理测试数据
        clean(myUser);
    }

    @Test
    public void getUserTest() {
        // 根据ID获取对象
        MyUser user = addUser();
        MyUser myUser = userDao.get(user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getName().equals(myUser.getName()));

        // 只取name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser = userDao.getPositive(config, user.getUserId());
        Assert.assertNull(myUser.getDescription());
        Assert.assertNotNull(myUser.getName());

        // 屏蔽name属性
        config = new MyUser();
        config.setName("");
        myUser = userDao.getNegative(config, user.getUserId());
        Assert.assertNull(myUser.getName());
        Assert.assertNotNull(myUser.getDescription());

        // 统计总数
        long count = userDao.countAll();
        Assert.assertTrue(count > 0);

        // 获取所有
        List<MyUser> myUsers = userDao.getAll();
        Assert.assertTrue(myUsers.size() > 0);

        // 根据ID批量获取
        MyUser user2 = addUser();
        myUsers = userDao.getBatch(user.getUserId(), user2.getUserId());
        Assert.assertTrue(myUsers.size() > 1);

        // 清理测试数据
        clean(user, user2);
    }

    @Test
    public void deleteUserTest() {
        // 删除对象
        MyUser user = addUser();
        userDao.delete(user);
        MyUser myUser = userDao.get(user.getUserId());
        Assert.assertNull(myUser);

        // 根据ID删除对象
        user = addUser();
        userDao.deleteById(user.getUserId());
        myUser = userDao.get(user.getUserId());
        Assert.assertNull(myUser);

        // 批量删除对象
        MyUser[] users = new MyUser[]{createUser(), createUser(), createUser()};
        userDao.addBatch(users);
        userDao.deleteBatch(users);
        myUser = userDao.get(users[0].getUserId());
        Assert.assertNull(myUser);

        // 删除全部对象
        user = addUser();
        userDao.deleteAll();
        myUser = userDao.get(user.getUserId());
        Assert.assertNull(myUser);
    }

    @Test
    public void queryUserTest() {
        // 根据名称查询
        MyUser user = addUser();
        MyUser myUser = userDao.findByName(user.getName());
        Assert.assertNotNull(myUser);

        // 自定义查询 根据名称、描述、等级查询
        myUser = userDao.findMyUser(user.getName(), user.getName(), user.getLevel());
        Assert.assertNotNull(myUser);

        // 分页查询所有
        PageData<MyUser> pageUsers = userDao.getByPage(new PageParam(0, 10));
        Assert.assertTrue(pageUsers.getContent().size() > 0);

        // 分页查询
        String sql = sqlGenerator.getSelectWhereTrue(MyUser.class);
        PageData<MyUser> users = userDao.queryPageData(new PageParam(), MyUser.class, sql);
        Assert.assertTrue(users.getTotal() > 0);

        // 清理测试数据
        clean(user);
    }

}
