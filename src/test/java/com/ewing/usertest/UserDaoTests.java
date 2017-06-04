package com.ewing.usertest;

import com.ewing.common.RandomString;
import com.ewing.dandelion.SqlGenerator;
import com.ewing.dandelion.pagination.PageData;
import com.ewing.dandelion.pagination.PageParam;
import com.ewing.usertest.dao.UserDao;
import com.ewing.usertest.entity.MyUser;
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
public class UserDaoTests {

    @Autowired
    private UserDao userDao;

    /**
     * 初始化数据。
     */
    private MyUser init() {
        MyUser myUser = new MyUser();
        myUser.setName(RandomString.randomChinese(3));
        myUser.setDateValue(new Date());
        myUser.setBoolValue(true);
        myUser.setBigDecimal(new BigDecimal("1213.23"));
        myUser.setIntValue(12323);
        myUser.setLongValue(548959845L);
        myUser.setDoubleValue(547858.123);
        myUser.setFloatValue(343.434F);
        myUser.setShortValue((short) 123);
        myUser.setBytesValue(new byte[]{1, 5, 12});
        userDao.add(myUser);
        return myUser;
    }

    /**
     * 清理测试数据。
     */
    private void clean(MyUser... myUsers) {
        for (MyUser myUser : myUsers)
            userDao.delete(myUser);
    }

    @Test
    public void addUserTest() {
        // 保存全部属性
        MyUser user = new MyUser();
        user.setName(RandomString.randomChinese(3));
        user.setDateValue(new Date());
        user.setBoolValue(true);
        user.setBigDecimal(new BigDecimal("1213.23"));
        user.setIntValue(12323);
        user.setLongValue(548959845L);
        user.setDoubleValue(547858.123);
        user.setFloatValue(343.434F);
        user.setShortValue((short) 123);
        user.setBytesValue(new byte[]{1, 5, 12});
        userDao.add(user);
        // 没有异常 简单验证
        Assert.assertTrue(StringUtils.hasText(user.getUserId()));
        // 清理测试数据
        clean(user);

        // 批量添加
        MyUser[] users = {user, new MyUser(), new MyUser()};
        userDao.addBatch(users);
        // 没有异常 简单验证
        Assert.assertTrue(userDao.countAll() >= users.length);
        clean(users);

        // 只保存name属性
        user.setUserId(null);
        MyUser config = new MyUser();
        config.setName("");
        userDao.addPositive(user, config);
        // 没有异常 简单验证
        MyUser myUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getDateValue());
        // 清理测试数据
        clean(user);

        // 屏蔽bytesValue属性
        user.setUserId(null);
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        userDao.addNegative(user, config);
        // 没有异常 简单验证
        myUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getBytesValue());
        // 清理测试数据
        clean(user);
    }

    @Test
    public void updateUserTest() {
        MyUser myUser = init();
        myUser.setName(RandomString.randomChinese(3));
        boolean result = userDao.update(myUser);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 只更新name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setIntValue(234567);
        result = userDao.updatePositive(myUser, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 屏蔽更新longValue属性
        config = new MyUser();
        config.setLongValue(1L);
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLongValue(456978L);
        result = userDao.updateNegative(myUser, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 清理测试数据
        clean(myUser);
    }

    @Test
    public void getUserTest() {
        MyUser user = init();
        MyUser myUser = userDao.getObject(user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getUserId().equals(myUser.getUserId()));

        // 只取name属性
        MyUser config = new MyUser();
        config.setName("");
        // 没有异常 简单验证
        myUser = userDao.getPositive(config, user.getUserId());
        Assert.assertNull(myUser.getDateValue());
        Assert.assertNotNull(myUser.getName());

        // 屏蔽bytesValue属性
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        // 没有异常 简单验证
        myUser = userDao.getNegative(config, user.getUserId());
        Assert.assertNull(myUser.getBytesValue());
        Assert.assertNotNull(myUser.getName());

        // 清理测试数据
        clean(user);
    }

    @Test
    public void deleteUserTest() {
        MyUser user = init();
        userDao.delete(user);
        // 没有异常 简单验证
        MyUser myUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myUser);

        user = init();
        userDao.deleteById(user.getUserId());
        // 没有异常 简单验证
        myUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myUser);

        user = init();
        userDao.deleteAll();
        // 没有异常 简单验证
        myUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myUser);
    }

    @Test
    public void queryUserTest() {
        MyUser user = init();
        MyUser myUser = userDao.findByName(user.getName());
        // 没有异常 简单验证
        Assert.assertNotNull(myUser);

        myUser = userDao.findNameAndLong(user.getName(), user.getLongValue());
        // 没有异常 简单验证
        Assert.assertNotNull(myUser);

        // 统计测试
        long count = userDao.countAll();
        // 没有异常 简单验证
        Assert.assertTrue(count > 0);

        // 查询所有
        List<MyUser> myUsers = userDao.getAll();
        // 没有异常 简单验证
        Assert.assertTrue(myUsers.size() > 0);

        // 分页查询
        String sql = SqlGenerator.getSelectWhereTrue(MyUser.class);
        PageData<MyUser> users = userDao.queryPageData(new PageParam(), MyUser.class, sql);
        // 没有异常 简单验证
        Assert.assertTrue(users.getTotal() > 0);

        // 清理测试数据
        clean(user);
    }

}
