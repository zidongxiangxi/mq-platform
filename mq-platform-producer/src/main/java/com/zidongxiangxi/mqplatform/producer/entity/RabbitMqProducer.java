package com.zidongxiangxi.mqplatform.producer.entity;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.mqplatform.producer.enums.ProducerStatusEnum;
import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * rabbitMq的消息
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Data
public class RabbitMqProducer implements Serializable, RowMapper<RabbitMqProducer> {
    public RabbitMqProducer() {}

    public RabbitMqProducer(String exchange, String routingKey, @NotNull Message message, CorrelationData correlationData) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.message = message;
        this.correlationData = correlationData;
        messageId = Objects.nonNull(message.getMessageProperties()) ?
                message.getMessageProperties().getMessageId() : null;
        if (StringUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }
    }

    private String messageId;

    private String exchange;

    private String routingKey;

    private Message message;

    private CorrelationData correlationData;

    private int executeTimes;

    private int maxExecuteTimes;

    private ProducerStatusEnum status;

    private Date createTime;

    private Date updateTime;

    @Override
    public RabbitMqProducer mapRow(ResultSet rs, int i) throws SQLException {
        RabbitMqProducer producer = new RabbitMqProducer();
        producer.setMessageId(rs.getString("message_id"));
        producer.setExchange(rs.getString("exchange"));
        producer.setRoutingKey(rs.getString("routing_key"));
        producer.setCorrelationData(JSON.parseObject(rs.getString("correlation_data"), CorrelationData.class));
        producer.setExecuteTimes(rs.getInt("execute_times"));
        producer.setMaxExecuteTimes(rs.getInt("max_execute_times"));
        producer.setStatus(ProducerStatusEnum.findByValue(rs.getInt("status")));
        producer.setCreateTime(rs.getDate("create_time"));
        producer.setUpdateTime(rs.getDate("update_time"));
        byte[] body = rs.getBytes("body");
        String messagePropertiesStr = rs.getString("message_properties");
        producer.setMessage(new Message(body, JSON.parseObject(messagePropertiesStr, MessageProperties.class)));
        return producer;
    }
}
