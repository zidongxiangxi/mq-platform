package com.zidongxiangxi.mqplatform.api.exception;

/**
 * 重复消息id异常
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class DuplicateEventIdException extends MqPlatformException {
    private static final String ERROR_MESSAGE = "消息id重复";

    public DuplicateEventIdException() {
        super(ERROR_MESSAGE);
    }
}
