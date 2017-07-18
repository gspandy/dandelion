package ewing.normal;

import ewing.dandelion.EntityBaseDao;
import ewing.dandelion.EntityDao;
import ewing.dandelion.SimpleBaseDao;
import ewing.dandelion.SimpleDao;
import ewing.dandelion.generation.SqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * SQL数据库访问配置。
 * 适用于常规Spring项目（不包含spring-boot-starter-jdbc自动配置）。
 * Spring容器中需要配置一个数据源，Druid、C3p0、DBCP、HikariCP等都可以。
 */
@Configuration
public class NormalDaoConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcOperations jdbcOperations() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcOperations namedParamOperations() {
        return new NamedParameterJdbcTemplate(jdbcOperations());
    }

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
        entityDao.setJdbcOperations(jdbcOperations());
        entityDao.setNamedParamOperations(namedParamOperations());
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
        simpleDao.setJdbcOperations(jdbcOperations());
        simpleDao.setNamedParamOperations(namedParamOperations());
        return simpleDao;
    }

}
