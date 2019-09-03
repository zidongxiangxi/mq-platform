package com.zidongxiangxi.mqplatform.consumer.manager;

/**
 * Created by cxd on 2019/9/1.
 */
public interface IConsumerManager {
    boolean insertConsumeRecord(String messageId);

    boolean deleteConsumeRecord(String messageId);
}
