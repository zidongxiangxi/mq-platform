package com.zidongxiangxi.mqplatform.api.manager;

/**
 * mq消息消费manager
 *
 * @author chenxudong
 * @date 2019/09/03
 */
public interface IConsumerManager {
    /**
     * 插入消费记录
     *
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean insertConsumeRecord(String messageId);

    /**
     * 删除消费记录
     *
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean deleteConsumeRecord(String messageId);
}
