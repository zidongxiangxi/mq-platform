package com.zidongxiangxi.mqplatform.consumer.transaction;

import com.zidongxiangxi.mqplatform.api.transaction.ITransactionListener;

/**
 * rabbitMq消费的事务监听
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public class RabbitConsumerTransactionListener implements ITransactionListener {
    @Override
    public void beforeCommit() {

    }

    @Override
    public void afterCommit() {

    }

    @Override
    public void afterCompletion() {

    }

    @Override
    public void suspend() {

    }

    @Override
    public void resume() {

    }
}
