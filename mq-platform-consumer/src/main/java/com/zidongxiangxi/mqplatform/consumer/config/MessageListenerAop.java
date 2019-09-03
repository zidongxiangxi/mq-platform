package com.zidongxiangxi.mqplatform.consumer.config;

import com.zidongxiangxi.mqplatform.consumer.manager.IConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.rabbitmq.client.Channel;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * mq消费者自动配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Slf4j
@Aspect
public class MessageListenerAop {
    @Autowired
    private IConsumerManager consumerManager;

    /**
     * 定义切入点
     */
    @Pointcut("within(org.springframework.amqp.rabbit.listener.api.MessageListenerAdapter.onMessage(..))")
    public void messageListenerAround() {
    }

    /**
     * 拦截onMessage方法
     */
    @Around("messageListenerAround()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object target = pjp.getTarget();
        Object[] args = pjp.getArgs();
        Message message = null;
        Channel channel = null;
        for (Object arg : args) {
            if (arg instanceof Message) {
                message = (Message) arg;
            } else if (arg instanceof Channel) {
                channel = (Channel) arg;
            }
        }

        if (Objects.isNull(message) || Objects.isNull(message.getMessageProperties())
                || StringUtils.isEmpty(message.getMessageProperties().getMessageId())) {
            return pjp.proceed();
        }
        String messageId = message.getMessageProperties().getMessageId();
        if (!consumerManager.insertConsumeRecord(messageId)) {
            Field field = ReflectionUtils.findField(target.getClass(), "isManualAck");
            Boolean isManualAck = false;
            if (Objects.nonNull(field)) {
                isManualAck = (Boolean) ReflectionUtils.getField(field, target);
            }
            if (isManualAck && Objects.nonNull(channel)) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
            return null;
        }
        boolean success = false;
        try {
            Object result = pjp.proceed();
            success = true;
            return result;
        } finally {
            if (!success) {
                consumerManager.deleteConsumeRecord(messageId);
            }
        }
    }
}
