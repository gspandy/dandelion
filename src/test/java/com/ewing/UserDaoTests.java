package com.ewing;

import com.ewing.common.RandomString;
import com.ewing.user.dao.UserDao;
import com.ewing.user.entity.User;
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
        operations.execute("DROP TABLE IF EXISTS `User`");
        operations.execute("CREATE TABLE `User` (\n" +
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
    private User init() {
        User user = new User();
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
        userDao.save(user);
        return user;
    }

    /**
     * 清理测试数据。
     */
    private void clean(User user) {
        userDao.delete(user);
    }

    @Test
    public void saveUserTest() {
        // 保存全部属性
        User user = new User();
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
        userDao.save(user);
        // 没有异常 简单验证
        Assert.assertTrue(StringUtils.hasText(user.getUserId()));
        // 清理测试数据
        clean(user);

        // 只保存name属性
        user.setUserId(null);
        User config = new User();
        config.setName("");
        userDao.savePositive(user, config);
        // 没有异常 简单验证
        User myUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getDateValue());
        // 清理测试数据
        clean(user);

        // 屏蔽bytesValue属性
        user.setUserId(null);
        config = new User();
        config.setBytesValue(new byte[]{});
        userDao.saveNegative(user, config);
        // 没有异常 简单验证
        myUser = userDao.getObject(user.getUserId());
        Assert.assertNotNull(myUser.getName());
        Assert.assertNull(myUser.getBytesValue());
        // 清理测试数据
        clean(user);
    }

    @Test
    public void updateUserTest() {
        User user = init();
        user.setName(RandomString.randomChinese(3));
        boolean result = userDao.update(user);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 只更新name属性
        User config = new User();
        config.setName("");
        user.setName(RandomString.randomChinese(3));
        user.setIntValue(234567);
        result = userDao.updatePositive(user, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 屏蔽更新longValue属性
        config = new User();
        config.setLongValue(1L);
        user.setName(RandomString.randomChinese(3));
        user.setLongValue(456978L);
        result = userDao.updateNegative(user, config);
        // 没有异常 简单验证
        Assert.assertTrue(result);

        // 清理测试数据
        clean(user);
    }

    @Test
    public void getUserTest() {
        User user = init();
        User myUser = userDao.getObject(user.getUserId());
        // 没有异常 简单验证
        Assert.assertTrue(user.getUserId().equals(myUser.getUserId()));

        // 只取name属性
        User config = new User();
        config.setName("");
        // 没有异常 简单验证
        myUser = userDao.getPositive(user.getUserId(), config);
        Assert.assertNull(myUser.getDateValue());
        Assert.assertNotNull(myUser.getName());

        // 屏蔽bytesValue属性
        config = new User();
        config.setBytesValue(new byte[]{});
        // 没有异常 简单验证
        myUser = userDao.getNegative(user.getUserId(), config);
        Assert.assertNull(myUser.getBytesValue());
        Assert.assertNotNull(myUser.getName());

        // 清理测试数据
        clean(user);
    }

    @Test
    public void deleteUserTest() {
        User user = init();
        userDao.delete(user);
        // 没有异常 简单验证
        User myUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myUser);

        user = init();
        userDao.deleteById(user.getUserId());
        // 没有异常 简单验证
        myUser = userDao.getObject(user.getUserId());
        Assert.assertNull(myUser);
    }

}
