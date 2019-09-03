package com.zidongxiangxi.mqplatform.producer.transaction;

import com.zidongxiangxi.mqplatform.api.transaction.IProducerSqlProvider;
import org.springframework.util.StringUtils;

/**
 * 默认的事务mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class DefaultRabbitProducerSqlProvider implements IProducerSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "mq_platform.rabbit_producer";
    private static final String INSERT_SQL = "insert into %s (message_id, exchange, routing_key, body, message_properties,"
            + " correlation_data," + "max_execute_times) values (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select  * from %s where message_id=?";
    private static final String BLOCK_SQL = "update %s set status=1, execute_times=execute_times+1 where message_id=?";
    private static final String FAIL_SQL = "update %s set status=2, execute_times=execute_times+1 where message_id=?";
    private static final String DELETE_SQL = "delete from %s where message_id=?";
    private static final String LIST_BLOCK_SQL = "select * from %s where stats=1 or (status = 0 and ? > create_time) "
            + "limit ?, ?";
    private static final String LIST_FAIL_SQL = "select * from %s where stats=2 limit ?, ?";

    private String insertSql, selectSql, blockSql, failSql, deleteSql, listBlockSql, listFailSql;

    public DefaultRabbitProducerSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        insertSql = String.format(INSERT_SQL, tableName);
        selectSql = String.format(SELECT_SQL, tableName);
        blockSql = String.format(BLOCK_SQL, tableName);
        failSql = String.format(FAIL_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
        listBlockSql = String.format(LIST_BLOCK_SQL, tableName);
        listFailSql = String.format(LIST_FAIL_SQL, tableName);
    }

    @Override
    public String getInsertMqSql() {
        return insertSql;
    }

    @Override
    public String getSelectMqSql() {
        return selectSql;
    }

    @Override
    public String getBlockMqSql() {
        return blockSql;
    }

    @Override
    public String getFailMqSql() {
        return failSql;
    }

    @Override
    public String getDeleteMqSql() {
        return deleteSql;
    }

    @Override
    public String getListBlockMqSql() {
        return listBlockSql;
    }

    @Override
    public String getListFailMqSql() {
        return listFailSql;
    }
}
