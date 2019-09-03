package com.zidongxiangxi.mqplatform.producer.enums;

import lombok.Getter;

/**
 * mq发送状态枚举类
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Getter
public enum ProducerStatusEnum {
    /**等待发送*/
    WAITING(0),
    /**重试中*/
    RETRYING(1),
    /**发送失败*/
    FAIL(2);

    private int value;

    ProducerStatusEnum(int value) {
        this.value = value;
    }

    public static ProducerStatusEnum findByValue(int value) {
        for (ProducerStatusEnum status : ProducerStatusEnum.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
