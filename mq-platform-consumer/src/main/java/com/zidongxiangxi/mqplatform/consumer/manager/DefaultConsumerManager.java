package com.zidongxiangxi.mqplatform.consumer.manager;

/**
 * Created by cxd on 2019/9/1.
 */
public class DefaultConsumerManager implements IConsumerManager {
    @Override
    public boolean insertConsumeRecord(String messageId) {
        return false;
    }

    @Override
    public boolean deleteConsumeRecord(String messageId) {
        return false;
    }
}
