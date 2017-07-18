package tsai.ewing.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import tsai.ewing.dandelion.EntityBaseDao;
import tsai.ewing.dandelion.EntityDao;
import tsai.ewing.dandelion.SimpleBaseDao;
import tsai.ewing.dandelion.SimpleDao;
import tsai.ewing.dandelion.generation.SqlGenerator;

/**
 * SQL数据库访问配置。
 * 适用于Spring Boot项目（包含spring-boot-starter-jdbc自动配置）。
 */
@Configuration
public class BootDaoConfig {

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private NamedParameterJdbcOperations namedParamOperations;

    /**
     * 配置Sql生成器对象。
     * 构造参数为true时使用下划线风格。
     */
    @Bean
    public SqlGenerator sqlGenerator() {
        return new SqlGenerator();
    }

    /**
     * 配置EntityDao的实现EntityBaseDao对象。
     */
    @Bean
    public EntityDao EntityDao() {
        EntityDao entityDao = new EntityBaseDao();
        entityDao.setJdbcOperations(jdbcOperations);
        entityDao.setNamedParamOperations(namedParamOperations);
        entityDao.setSqlGenerator(sqlGenerator());
        return entityDao;
    }

    /**
     * 配置SimpleDao的实现SimpleBaseDao对象。
     * EntityBaseDao继承自该类，包含该类所有方法。
     * 如无需要可以不配置SimpleBaseDao对象。
     */
    // @Bean
    public SimpleDao simpleDao() {
        SimpleDao simpleDao = new SimpleBaseDao();
        simpleDao.setJdbcOperations(jdbcOperations);
        simpleDao.setNamedParamOperations(namedParamOperations);
        return simpleDao;
    }

}
