package com.ewing.normal;

import com.ewing.dandelion.CommonBaseDao;
import com.ewing.dandelion.CommonDao;
import com.ewing.dandelion.SqlGenerator;
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
    public DataSource dataSource;

    @Bean
    public JdbcOperations jdbcOperations() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcOperations namedParamOperations() {
        return new NamedParameterJdbcTemplate(jdbcOperations());
    }

    @Bean
    public SqlGenerator sqlGenerator() {
        return new SqlGenerator();
    }

    @Bean
    public CommonDao commonDao() {
        CommonDao commonDao = new CommonBaseDao();
        commonDao.setJdbcOperations(jdbcOperations());
        commonDao.setNamedParamOperations(namedParamOperations());
        commonDao.setSqlGenerator(sqlGenerator());
        return commonDao;
    }

}
