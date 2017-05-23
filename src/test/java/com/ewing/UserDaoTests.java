package com.ewing;

import com.ewing.common.RandomString;
import com.ewing.user.dao.UserDao;
import com.ewing.user.entity.MyUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTests {

    @Autowired
    private UserDao userDao;

    /**
     * 创建表结构。
     */
    @Before
    public void before() {
        JdbcOperations operations = userDao.getJdbcOperations();
        operations.execute("DROP TABLE IF EXISTS `MyUser`");
        operations.execute("CREATE TABLE `MyUser` (\n" +
                "  `userId` varchar(22) NOT NULL,\n" +
                "  `name` varchar(64) DEFAULT NULL,\n" +
                "  `boolValue` bit(1) DEFAULT NULL,\n" +
                "  `dateValue` datetime DEFAULT NULL,\n" +
                "  `bigDecimal` decimal(32,0) DEFAULT NULL,\n" +
                "  `intValue` int(11) DEFAULT NULL,\n" +
                "  `longValue` int(20) DEFAULT NULL,\n" +
                "  `floatValue` float(32,0) DEFAULT NULL,\n" +
                "  `doubleValue` double(64,0) DEFAULT NULL,\n" +
                "  `shortValue` smallint(8) DEFAULT NULL,\n" +
                "  `bytesValue` binary(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`userId`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
    }

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
    private void clean(MyUser myUser) {
        userDao.delete(myUser);
    }

    @Test
    public void saveUserTest() {
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

        // 只保存name属性
        user.setUserId(null);
        MyUser config = new MyUser();
        config.setName("");
        userDao.addPositive(user, config);
        // 没有异常 简单验证
        MyUser myMyUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myMyUser.getName());
        Assert.assertNull(myMyUser.getDateValue());
        // 清理测试数据
        clean(user);

        // 屏蔽bytesValue属性
        user.setUserId(null);
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        userDao.addNegative(user, config);
        // 没有异常 简单验证
        myMyUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myMyUser.getName());
        Assert.assertNull(myMyUser.getBytesValue());
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
        MyUser myMyUser = userDao.getObject(user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getUserId().equals(myMyUser.getUserId()));

        // 只取name属性
        MyUser config = new MyUser();
        config.setName("");
        // 没有异常 简单验证
        myMyUser = userDao.getPositive( config,user.getUserId());
        Assert.assertNull(myMyUser.getDateValue());
        Assert.assertNotNull(myMyUser.getName());

        // 屏蔽bytesValue属性
        config = new MyUser();
        config.setBytesValue(new byte[]{});
        // 没有异常 简单验证
        myMyUser = userDao.getNegative(config,user.getUserId());
        Assert.assertNull(myMyUser.getBytesValue());
        Assert.assertNotNull(myMyUser.getName());

        // 清理测试数据
        clean(user);
    }

    @Test
    public void deleteUserTest() {
        MyUser user = init();
        userDao.delete(user);
        // 没有异常 简单验证
        MyUser myMyUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myMyUser);

        user = init();
        userDao.deleteById(user.getUserId());
        // 没有异常 简单验证
        myMyUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myMyUser);
    }

    @Test
    public void findUserTest() {
        MyUser user = init();
        MyUser myMyUser = userDao.findByName(user.getName());
        // 没有异常 简单验证
        Assert.assertNotNull(myMyUser);
    }

}
