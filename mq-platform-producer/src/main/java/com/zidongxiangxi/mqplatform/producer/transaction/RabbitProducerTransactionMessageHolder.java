package com.zidongxiangxi.mqplatform.producer.transaction;

import com.zidongxiangxi.mqplatform.producer.entity.RabbitMqProducer;
import com.zidongxiangxi.mqplatform.producer.manager.rabbit.RabbitMqProducerManager;

import java.util.LinkedList;
import java.util.List;

/**
 * rabbitMq事务消息holder
 *
 * @author chenxudong
 * @date 2019/08/31
 */
public class RabbitProducerTransactionMessageHolder {
    private final RabbitMqProducerManager producerManager;
    private List<RabbitMqProducer> queue = new LinkedList<>();

    public RabbitProducerTransactionMessageHolder(RabbitMqProducerManager producerManager) {
        this.producerManager = producerManager;
    }

    public void add(RabbitMqProducer mqProducer) {
        queue.add(mqProducer);
    }

    public List<RabbitMqProducer> getQueue() {
        return queue;
    }

    public RabbitMqProducerManager getProducerManager() {
        return producerManager;
    }
}
