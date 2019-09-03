package com.zidongxiangxi.mqplatform.starter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据库配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnSingleCandidate(DataSource.class)
public class MqPlatformJdbcAutoConfiguration {
    /**
     * 如果缺少jdbcTemplate对象，则定义一个
     *
     * @param dataSource 数据源
     * @return jdbcTemplate
     */
    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
