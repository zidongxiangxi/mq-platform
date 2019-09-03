package com.zidongxiangxi.mqplatform.consumer.manager;

import com.zidongxiangxi.mqplatform.api.manager.IConsumerManager;
import com.zidongxiangxi.mqplatform.api.transaction.IConsumerSqlProvider;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Objects;

/**
 * mq消息消费manager
 *
 * @author chenxudong
 * @date 2019/09/03
 */
public class DefaultConsumerManager implements IConsumerManager {
    private JdbcTemplate jdbcTemplate;
    private IConsumerSqlProvider sqlProvider;

    public DefaultConsumerManager(JdbcTemplate jdbcTemplate, IConsumerSqlProvider sqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
    }
    @Override
    public boolean insertConsumeRecord(String messageId) {
        List<Integer> result = jdbcTemplate.queryForList(sqlProvider.getSelectMqSql(), Integer.class, messageId);
        if (Objects.nonNull(result) && !result.isEmpty()) {
            return false;
        }
        try {
            return jdbcTemplate.update(sqlProvider.getInsertMqSql(), messageId) > 0;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public boolean deleteConsumeRecord(String messageId) {
        return jdbcTemplate.update(sqlProvider.getDeleteMqSql(), messageId) > 0;
    }
}
