package com.zidongxiangxi.mqplatform.producer.manager;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.mqplatform.producer.entity.RabbitProducer;
import com.zidongxiangxi.mqplatform.api.manager.IProducerManager;
import com.zidongxiangxi.mqplatform.api.transaction.IProducerSqlProvider;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

/**
 * mq消息生产者manager
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class RabbitProducerManager implements IProducerManager<RabbitProducer> {
    private JdbcTemplate jdbcTemplate;
    private IProducerSqlProvider sqlProvider;
    private int maxExecuteTimes = 5;

    public RabbitProducerManager(JdbcTemplate jdbcTemplate, IProducerSqlProvider sqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
    }

    public RabbitProducerManager(JdbcTemplate jdbcTemplate, IProducerSqlProvider sqlProvider, int maxExecuteTimes) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
        this.maxExecuteTimes = maxExecuteTimes;
    }

    @Override
    public boolean saveMqProducer(RabbitProducer producer) {
        int rows = jdbcTemplate.update(sqlProvider.getInsertMqSql(), producer.getMessageId(), producer.getExchange(),
                producer.getRoutingKey(), producer.getMessage().getBody(), JSON.toJSONString(producer.getMessage().getMessageProperties()),
                JSON.toJSONString(producer.getCorrelationData()), maxExecuteTimes);
        return rows > 0;
    }

    @Override
    public boolean failSendMq(String messageId) {
        RabbitProducer mqProducer = null;
        try {
            mqProducer = jdbcTemplate.queryForObject(sqlProvider.getSelectMqSql(), new RabbitProducer(), messageId);
        } catch (EmptyResultDataAccessException ignore) {}
        if (Objects.isNull(mqProducer)) {
            return false;
        }
        int executeTimes = mqProducer.getExecuteTimes() + 1;
        int rows;
        if (executeTimes >= mqProducer.getMaxExecuteTimes()) {
            rows = jdbcTemplate.update(sqlProvider.getFailMqSql(), messageId);
        } else {
            rows = jdbcTemplate.update(sqlProvider.getBlockMqSql(), messageId);
        }
        return rows > 0;
    }

    @Override
    public boolean deleteMq(String messageId) {
        int rows = jdbcTemplate.update(sqlProvider.getDeleteMqSql(), messageId);
        return rows > 0;
    }
}
