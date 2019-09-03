package com.zidongxiangxi.mqplatform.producer.callback;

import com.zidongxiangxi.mqplatform.producer.manager.rabbit.RabbitMqProducerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * rabbitMq消息发送确认
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Slf4j
public class RabbitMqConfirmCallback implements RabbitTemplate.ConfirmCallback {
    private RabbitMqProducerManager manager;

    public RabbitMqConfirmCallback(RabbitMqProducerManager manager) {
        this.manager = manager;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack && Objects.nonNull(correlationData) && !StringUtils.isEmpty(correlationData.getId())) {
            log.info("发送mq消息成功， messageId:{}", correlationData.getId());
            manager.deleteMq(correlationData.getId());
        } else if (ack) {
            log.warn("发送mq消息成功，但是缺少关联id");
        } else {
            log.error("发送mq消息失败, messageId:{}, reason:{}", correlationData.getId(), cause);
            manager.failSendMq(correlationData.getId());
        }
    }
}
