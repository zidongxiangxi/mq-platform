package com.zidongxiangxi.mqplatform.consumer.transaction;

/**
 * 幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public interface IConsumerSqlProvider {
    String getInsertMqSql();

    String getDeleteMqSql();
}
