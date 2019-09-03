package com.zidongxiangxi.mqplatform.producer.transaction;

import com.zidongxiangxi.mqplatform.producer.entity.RabbitProducer;
import com.zidongxiangxi.mqplatform.producer.manager.RabbitProducerManager;

import java.util.LinkedList;
import java.util.List;

/**
 * rabbitMq事务消息holder
 *
 * @author chenxudong
 * @date 2019/08/31
 */
public class RabbitProducerTransactionMessageHolder {
    private final RabbitProducerManager producerManager;
    private List<RabbitProducer> queue = new LinkedList<>();

    public RabbitProducerTransactionMessageHolder(RabbitProducerManager producerManager) {
        this.producerManager = producerManager;
    }

    public void add(RabbitProducer mqProducer) {
        queue.add(mqProducer);
    }

    public List<RabbitProducer> getQueue() {
        return queue;
    }

    public RabbitProducerManager getProducerManager() {
        return producerManager;
    }
}
