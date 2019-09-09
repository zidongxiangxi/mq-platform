package com.zidongxiangxi.mqplatform.consumer.interceptor;

import com.rabbitmq.client.Channel;
import com.zidongxiangxi.mqplatform.api.manager.IConsumerManager;
import com.zidongxiangxi.mqplatform.consumer.config.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 幂等拦截器
 *
 * @author chenxudong
 * @date 2019/09/09
 */
@Slf4j
public class IdempotentOperationsInterceptor implements MethodInterceptor {
    private IConsumerManager consumerManager;
    private ConsumerConfig config;

    public IdempotentOperationsInterceptor(IConsumerManager consumerManager, ConsumerConfig config) {
        this.consumerManager = consumerManager;
        this.config = config;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (!config.isIdempotent()) {
            return methodInvocation.proceed();
        }

        Object[] args = methodInvocation.getArguments();
        Message message = null;
        Channel channel = null;
        for (Object arg : args) {
            if (arg instanceof Message) {
                message = (Message) arg;
            } else if (arg instanceof Channel) {
                channel = (Channel) arg;
            }
        }
        if (Objects.isNull(message)) {
            return methodInvocation.proceed();
        }
        String messageId = getMessageId(message);
        if (StringUtils.isEmpty(messageId)) {
            return methodInvocation.proceed();
        }
        if (!consumerManager.insertConsumeRecord(messageId)) {
            if (Objects.isNull(channel)) {
                return null;
            } else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }
        boolean success = false;
        Object result;
        // TODO 重试次数从配置读取
        try {
            result = methodInvocation.proceed();
            success = true;
        } catch (Throwable e) {
            log.warn("fail to consume message: {}", message.toString());
            throw e;
        } finally {
            if (!success) {
                try {
                    // TODO 1、触发告警；2、考虑死信机制，例如与删除幂等记录的同一个事务中保存mq消息
                    consumerManager.deleteConsumeRecord(messageId);
                    if (Objects.nonNull(channel)) {
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    }
                } catch (Throwable e) {
                    log.warn("fail to delete consume message: {}", message.toString());
                }
            }
        }
        return result;
    }

    private String getMessageId(Message message) {
        return Objects.nonNull(message.getMessageProperties()) ? message.getMessageProperties().getMessageId() : null;
    }
}
