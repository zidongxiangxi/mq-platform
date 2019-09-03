package com.zidongxiangxi.mqplatform.producer;

import com.zidongxiangxi.mqplatform.api.exception.NotSupportOperationException;
import com.zidongxiangxi.mqplatform.producer.entity.RabbitProducer;
import com.zidongxiangxi.mqplatform.producer.manager.RabbitProducerManager;
import com.zidongxiangxi.mqplatform.api.transaction.DefaultTransactionSynchronization;
import com.zidongxiangxi.mqplatform.producer.transaction.RabbitProducerTransactionMessageHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 加上事务的rabbitTemplate
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Slf4j
public class RabbitTransactionTemplate extends RabbitTemplate {
    private RabbitProducerManager producerManager;
    private DefaultTransactionSynchronization transactionSynchronization;

    public RabbitTransactionTemplate() {}

    public RabbitTransactionTemplate(RabbitProducerManager producerManager) {
        this.producerManager = producerManager;
    }

    public RabbitTransactionTemplate(DefaultTransactionSynchronization transactionSynchronization) {
        this.transactionSynchronization = transactionSynchronization;
    }

    public RabbitTransactionTemplate(RabbitProducerManager producerManager,
                                     DefaultTransactionSynchronization transactionSynchronization) {
        this.producerManager = producerManager;
        this.transactionSynchronization = transactionSynchronization;
    }

    /**
     * 发送mq
     * RabbitTemplate的发送方法，最终都是调用该send方法
     * 重写该方法，处于事务的情况下，在事务中保存要发送的mq消息到数据库
     * 事务提交后，再将所有mq消息发出
     *
     * @param exchange 交换机
     * @param routingKey 路由key
     * @param message 消息
     * @param correlationData 关联数据
     * @throws AmqpException
     */
    @Override
    public void send(String exchange, String routingKey, Message message, @Nullable CorrelationData correlationData)
            throws AmqpException {
        Assert.notNull(message, "message object must not be null");
        if (Objects.nonNull(transactionSynchronization)
                && TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);

            RabbitProducer mqProducer = new RabbitProducer(exchange, routingKey, message, correlationData);
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
            RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
            if (messageHolder == null) {
                messageHolder = new RabbitProducerTransactionMessageHolder(producerManager);
                RabbitTransactionContext.setMessageHolder(messageHolder);
            }
            messageHolder.add(mqProducer);
        } else if (Objects.nonNull(producerManager)) {
            RabbitProducer mqProducer = new RabbitProducer(exchange, routingKey, message, correlationData);
            try {
                producerManager.saveMqProducer(mqProducer);
            } catch (Exception e) {
                log.error("消息保存失败{}", mqProducer.getMessageId(), e);
            }
            super.send(exchange, routingKey, message, correlationData);
        } else {
            super.send(exchange, routingKey, message, correlationData);
        }
    }

    /**
     * 发送mq消息并接收回复
     * RabbitTemplate的发送并接收方法，最终都是调用该doSendAndReceive方法
     * 事务mq暂时不支持改类型的方法，抛出不支持该操作的异常
     *
     * @param exchange 交换机
     * @param routingKey 路由key
     * @param message 消息
     * @param correlationData 关联数据
     * @return 回复的消息
     * @throws NotSupportOperationException
     */
    @Override
    public Message doSendAndReceive(String exchange, String routingKey, Message message,
                                    @Nullable CorrelationData correlationData) throws NotSupportOperationException {
        throw new NotSupportOperationException();
    }

    /**
     * 发送普通的mq消息，忽略事务
     *
     * @param exchange 交换机
     * @param routingKey 路由key
     * @param message 消息
     * @param correlationData 关联数据
     * @throws AmqpException
     */
    public void sendWithoutTransaction(String exchange, String routingKey, Message message,
                                       @Nullable CorrelationData correlationData) throws AmqpException{
        super.send(exchange, routingKey, message, correlationData);
    }
}
