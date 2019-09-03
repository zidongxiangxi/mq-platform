package com.zidongxiangxi.mqplatform.producer.manager;

/**
 * mq生产者manager接口
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface IMqProducerManager<T> {
    boolean saveMqProducer(T producer);

    boolean failSendMq(String messageId);

    boolean deleteMq(String messageId);
}
