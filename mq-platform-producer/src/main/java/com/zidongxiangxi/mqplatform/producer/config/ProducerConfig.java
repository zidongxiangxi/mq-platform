package com.zidongxiangxi.mqplatform.producer.config;

import lombok.Data;

/**
 * mq消息生产者配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Data
public class ProducerConfig {
    /**
     * 是否使用事务mq，默认为true
     */
    private boolean transaction = true;

    /**
     * 用于发送mq的表名
     */
    private String tableName;

    /**
     * 最大执行次数
     */
    private int maxExecuteTimes = 5;
}
