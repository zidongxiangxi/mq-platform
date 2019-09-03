package com.zidongxiangxi.mqplatform.consumer.transaction;

import org.springframework.util.StringUtils;

/**
 * 默认的rabbitMq幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public class DefaultRabbitConsumerSqlProvider implements IConsumerSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "mq_platform.consumer";
    private static final String SELECT_SQL = "select 1 from %s where message_id = ?";
    private static final String INSERT_SQL = "insert into %s (message_id) values (?)";
    private static final String DELETE_SQL = "delete from %s where message_id = ?";

    private String selectSql, insertSql, deleteSql;

    public DefaultRabbitConsumerSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        selectSql = String.format(SELECT_SQL, tableName);
        insertSql = String.format(INSERT_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
    }

    @Override
    public String getSelectMqSql() {
        return selectSql;
    }

    @Override
    public String getInsertMqSql() {
        return insertSql;
    }

    @Override
    public String getDeleteMqSql() {
        return deleteSql;
    }
}
