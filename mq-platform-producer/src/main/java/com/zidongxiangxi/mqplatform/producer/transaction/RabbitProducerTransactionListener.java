package com.zidongxiangxi.mqplatform.producer.transaction;

import com.zidongxiangxi.mqplatform.api.transaction.ITransactionListener;
import com.zidongxiangxi.mqplatform.producer.RabbitTransactionTemplate;
import com.zidongxiangxi.mqplatform.producer.entity.RabbitProducer;
import com.zidongxiangxi.mqplatform.producer.RabbitTransactionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * rabbitMq发送的事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Slf4j
public class RabbitProducerTransactionListener implements ITransactionListener {

    private final ThreadLocal<Stack<RabbitProducerTransactionMessageHolder>> resource = new ThreadLocal<Stack<RabbitProducerTransactionMessageHolder>>() {
        @Override
        protected Stack<RabbitProducerTransactionMessageHolder> initialValue() {
            return new Stack<>();
        }
    };

    private RabbitTransactionTemplate rabbitTransactionTemplate;

    public RabbitProducerTransactionListener(RabbitTransactionTemplate rabbitTransactionTemplate) {
        this.rabbitTransactionTemplate = rabbitTransactionTemplate;
    }

    @Override
    public void beforeCommit() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (messageHolder != null) {
            List<RabbitProducer> list = get();
            for (RabbitProducer mqProducer : list) {
                messageHolder.getProducerManager().saveMqProducer(mqProducer);
            }
        }
    }

    @Override
    public void afterCommit() {
        List<RabbitProducer> list = remove();
        for (RabbitProducer mqProducer : list) {
            try {
                rabbitTransactionTemplate.sendWithoutTransaction(mqProducer.getExchange(), mqProducer.getRoutingKey(), mqProducer.getMessage(), mqProducer.getCorrelationData());
            } catch (Throwable t) {
                log.error("消息发送失败{}", mqProducer.getMessageId(), t);
            }
        }
    }

    @Override
    public void afterCompletion() {
        List<RabbitProducer> list = remove();
        for (RabbitProducer mqProducer : list) {
            log.info("事务提交失败, 消息({})被忽略.exchange:{}.routingKey:{}", mqProducer.getMessageId(), mqProducer.getExchange(), mqProducer.getRoutingKey());
        }
    }

    @Override
    public void suspend() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (messageHolder == null) {
            return;
        }
        RabbitTransactionContext.removeMessageHolder();
        resource.get().push(messageHolder);
    }

    @Override
    public void resume() {
        RabbitTransactionContext.setMessageHolder(resource.get().pop());
    }

    private List<RabbitProducer> get() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (messageHolder == null) {
            return Collections.emptyList();
        }
        return messageHolder.getQueue();
    }

    private List<RabbitProducer> remove() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        RabbitTransactionContext.removeMessageHolder();
        if (messageHolder == null) {
            return Collections.emptyList();
        }
        return messageHolder.getQueue();
    }
}
