package com.zidongxiangxi.mqplatform.producer.manager.rabbit;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.mqplatform.producer.entity.RabbitMqProducer;
import com.zidongxiangxi.mqplatform.producer.manager.IMqProducerManager;
import com.zidongxiangxi.mqplatform.producer.transaction.IProducerSqlProvider;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

/**
 * mq消息生产者manager
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class RabbitMqProducerManager implements IMqProducerManager<RabbitMqProducer> {
    private JdbcTemplate jdbcTemplate;
    private IProducerSqlProvider sqlProvider;
    private int maxExecuteTimes = 5;

    public RabbitMqProducerManager(JdbcTemplate jdbcTemplate, IProducerSqlProvider sqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
    }

    public RabbitMqProducerManager(JdbcTemplate jdbcTemplate, IProducerSqlProvider sqlProvider, int maxExecuteTimes) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
        this.maxExecuteTimes = maxExecuteTimes;
    }

    @Override
    public boolean saveMqProducer(RabbitMqProducer producer) {
        int rows = jdbcTemplate.update(sqlProvider.getInsertMqSql(), producer.getMessageId(), producer.getExchange(),
                producer.getRoutingKey(), producer.getMessage().getBody(), JSON.toJSONString(producer.getMessage().getMessageProperties()),
                JSON.toJSONString(producer.getCorrelationData()), maxExecuteTimes);
        return rows > 0;
    }

    @Override
    public boolean failSendMq(String messageId) {
        RabbitMqProducer mqProducer = null;
        try {
            mqProducer = jdbcTemplate.queryForObject(sqlProvider.getSelectMqSql(), new RabbitMqProducer(), messageId);
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
