package com.zidongxiangxi.mqplatform.producer;

import com.zidongxiangxi.mqplatform.producer.transaction.RabbitProducerTransactionMessageHolder;

/**
 * rabbitMq事务消息上线文
 *
 * @author chenxudong
 * @date 2019/08/31
 */
public class RabbitTransactionContext {
    private static ThreadLocal<RabbitProducerTransactionMessageHolder> messageHolderThreadLocal = new ThreadLocal<>();

    public static RabbitProducerTransactionMessageHolder getMessageHolder() {
        return messageHolderThreadLocal.get();
    }

    public static void setMessageHolder(RabbitProducerTransactionMessageHolder messageHolder) {
        messageHolderThreadLocal.set(messageHolder);
    }

    public static void removeMessageHolder() {
        messageHolderThreadLocal.remove();
    }
}
