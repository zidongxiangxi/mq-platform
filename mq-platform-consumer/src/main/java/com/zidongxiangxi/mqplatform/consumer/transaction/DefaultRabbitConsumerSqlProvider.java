package com.zidongxiangxi.mqplatform.consumer.transaction;

/**
 * 默认的rabbitMq幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public class DefaultRabbitConsumerSqlProvider implements IConsumerSqlProvider {
    @Override
    public String getInsertMqSql() {
        return null;
    }

    @Override
    public String getDeleteMqSql() {
        return null;
    }
}
