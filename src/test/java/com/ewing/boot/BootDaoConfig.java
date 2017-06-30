package com.ewing.boot;

import com.ewing.dandelion.CommonBaseDao;
import com.ewing.dandelion.CommonDao;
import com.ewing.dandelion.generation.SqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

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

    @Bean
    public SqlGenerator sqlGenerator() {
        return new SqlGenerator();
    }

    @Bean
    public CommonDao commonDao() {
        CommonDao commonDao = new CommonBaseDao();
        commonDao.setJdbcOperations(jdbcOperations);
        commonDao.setNamedParamOperations(namedParamOperations);
        commonDao.setSqlGenerator(sqlGenerator());
        return commonDao;
    }

}
