package com.zidongxiangxi.mqplatform.consumer.config;

import lombok.Data;

/**
 * mq消息消费者配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Data
public class ConsumerConfig {
    /**
     * 是否使用消费幂等，默认为false
     */
    private boolean idempotent = false;

    /**
     * 用于实现幂等消息mq的表名
     */
    private String tableName;
}
