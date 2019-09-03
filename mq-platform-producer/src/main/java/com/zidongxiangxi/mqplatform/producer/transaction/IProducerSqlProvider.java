package com.zidongxiangxi.mqplatform.producer.transaction;

/**
 * 事务mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface IProducerSqlProvider {
    String getInsertMqSql();

    String getSelectMqSql();

    String getBlockMqSql();

    String getFailMqSql();

    String getDeleteMqSql();

    String getListBlockMqSql();

    String getListFailMqSql();
}
