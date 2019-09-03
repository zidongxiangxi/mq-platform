package com.zidongxiangxi.mqplatform.api.manager;

/**
 * mq生产者manager接口
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface IProducerManager<T> {
    /**
     * 保存消息
     *
     * @param producer 消息体
     * @return 是否成功
     */
    boolean saveMqProducer(T producer);

    /**
     * 消息发送失败
     *
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean failSendMq(String messageId);

    /**
     * 删除消息
     *
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean deleteMq(String messageId);
}
