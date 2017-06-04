package com.ewing;

import com.ewing.common.RandomString;
import com.ewing.dandelion.CommonDao;
import com.ewing.user.entity.MyUser;
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
public class CommonDaoTests {

    @Autowired
    private CommonDao commonDao;

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
        commonDao.add(myUser);
        return myUser;
    }

    /**
     * 清理测试数据。
     */
    private void clean(MyUser... myUsers) {
        for (MyUser myUser : myUsers)
            commonDao.delete(myUser);
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
        commonDao.add(user);
        // 没有异常 简单验证
        Assert.assertTrue(StringUtils.hasText(user.getUserId()));
        // 清理测试数据
        clean(user);

        // 批量添加
        MyUser[] users = {user, new MyUser(), new MyUser()};
        commonDao.addBatch(users);
        // 没有异常 简单验证
        Assert.assertTrue(commonDao.countAll(user.getClass()) >= users.length);
        clean(users);

        // 只保存name属性
        user.setUserId(null);
        MyUser config = new MyUser();
        config.setName("");
        commonDao.addPositive(user, config);
        // 没有异常 简单验证
        MyUser myUser = commonDao.getObject(MyUser.class, user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getDateValue());
        // 清理测试数据
        clean(user);

        // 屏蔽bytesValue属性
        user.setUserId(null);
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        commonDao.addNegative(user, config);
        // 没有异常 简单验证
        myUser = commonDao.getObject(MyUser.class, user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getBytesValue());
        // 清理测试数据
        clean(user);
    }

    @Test
    public void updateUserTest() {
        MyUser myUser = init();
        myUser.setName(RandomString.randomChinese(3));
        boolean result = commonDao.update(myUser);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 只更新name属性
        MyUser config = new MyUser();
        config.setName("");
        myUser.setName(RandomString.randomChinese(3));
        myUser.setIntValue(234567);
        result = commonDao.updatePositive(myUser, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 屏蔽更新longValue属性
        config = new MyUser();
        config.setLongValue(1L);
        myUser.setName(RandomString.randomChinese(3));
        myUser.setLongValue(456978L);
        result = commonDao.updateNegative(myUser, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 清理测试数据
        clean(myUser);
    }

    @Test
    public void getUserTest() {
        MyUser user = init();
        MyUser myUser = commonDao.getObject(MyUser.class, user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getUserId().equals(myUser.getUserId()));

        // 只取name属性
        MyUser config = new MyUser();
        config.setName("");
        // 没有异常 简单验证
        myUser = commonDao.getPositive(config, user.getUserId());
        Assert.assertNull(myUser.getDateValue());
        Assert.assertNotNull(myUser.getName());

        // 屏蔽bytesValue属性
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        // 没有异常 简单验证
        myUser = commonDao.getNegative(config, user.getUserId());
        Assert.assertNull(myUser.getBytesValue());
        Assert.assertNotNull(myUser.getName());

        // 清理测试数据
        clean(user);
    }

    @Test
    public void deleteUserTest() {
        MyUser user = init();
        commonDao.delete(user);
        // 没有异常 简单验证
        MyUser myUser = commonDao.getObject(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);

        user = init();
        commonDao.deleteById(MyUser.class, user.getUserId());
        // 没有异常 简单验证
        myUser = commonDao.getObject(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);

        user = init();
        commonDao.deleteAll(user.getClass());
        // 没有异常 简单验证
        myUser = commonDao.getObject(MyUser.class, user.getUserId());
        Assert.assertNull(myUser);
    }

    @Test
    public void queryUserTest() {
        MyUser user = init();
        // 统计测试
        long count = commonDao.countAll(MyUser.class);
        // 没有异常 简单验证
        Assert.assertTrue(count > 0);

        // 查询所有
        List<MyUser> myUsers = commonDao.getAll(MyUser.class);
        // 没有异常 简单验证
        Assert.assertTrue(myUsers.size() > 0);

        // 清理测试数据
        clean(user);
    }

}
