package com.zidongxiangxi.mqplatform.consumer.transaction;

/**
 * 幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public interface IConsumerSqlProvider {
    /**
     * 获取查询sql
     *
     * @return sql语句
     */
    String getSelectMqSql();

    /**
     * 获取插入sql
     *
     * @return sql语句
     */
    String getInsertMqSql();

    /**
     * 获取删除sql
     *
     * @return sql语句
     */
    String getDeleteMqSql();
}
