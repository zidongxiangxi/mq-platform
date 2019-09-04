package com.zidongxiangxi.mqplatform.producer;

import com.zidongxiangxi.mqplatform.producer.transaction.RabbitProducerTransactionMessageHolder;

/**
 * rabbitMq事务消息上线文
 *
 * @author chenxudong
 * @date 2019/08/31
 */
public class RabbitTransactionContext {
    private static final ThreadLocal<RabbitProducerTransactionMessageHolder> MESSAGE_HOLDER_THREAD_LOCAL =
        new ThreadLocal<>();

    public static RabbitProducerTransactionMessageHolder getMessageHolder() {
        return MESSAGE_HOLDER_THREAD_LOCAL.get();
    }

    public static void setMessageHolder(RabbitProducerTransactionMessageHolder messageHolder) {
        MESSAGE_HOLDER_THREAD_LOCAL.set(messageHolder);
    }

    public static void removeMessageHolder() {
        MESSAGE_HOLDER_THREAD_LOCAL.remove();
    }
}
