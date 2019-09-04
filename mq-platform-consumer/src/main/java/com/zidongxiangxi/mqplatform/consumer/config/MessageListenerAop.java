package com.zidongxiangxi.mqplatform.consumer.config;

import com.zidongxiangxi.mqplatform.api.manager.IConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;

import com.rabbitmq.client.Channel;
import org.springframework.util.StringUtils;

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
    private IConsumerManager consumerManager;
    private static final int MAX_RETRY_TIMES = 5;

    public MessageListenerAop(IConsumerManager consumerManager) {
        this.consumerManager = consumerManager;
    }

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void rabbitListener() {
    }

    /**
     * 拦截方法
     *
     * @param pjp 切点
     * @return 返回值
     * @throws Throwable
     */
    @Around("rabbitListener()")
    public Object doRabbitListenerAround(ProceedingJoinPoint pjp) throws Throwable {
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
        if (Objects.isNull(message)) {
            return pjp.proceed();
        }
        String messageId = getMessageId(message);
        if (StringUtils.isEmpty(messageId)) {
            return pjp.proceed();
        }
        if (!consumerManager.insertConsumeRecord(messageId)) {
            if (Objects.isNull(channel)) {
                return null;
            } else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }
        boolean success = false;
        Object result = null;
        // TODO 重试次数从配置读取
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            try {
                result = pjp.proceed();
                success = true;
                break;
            } catch (Exception e) {
                log.warn("fail to consume message: {}", message.toString());
            }
        }
        if (success) {
            if (Objects.nonNull(channel)) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } else {
            // TODO 1、触发告警；2、考虑死信机制，例如与删除幂等记录的同一个事务中保存mq消息
            consumerManager.deleteConsumeRecord(messageId);
            if (Objects.nonNull(channel)) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
        return result;
    }

    private String getMessageId(Message message) {
        return Objects.nonNull(message.getMessageProperties()) ? message.getMessageProperties().getMessageId() : null;
    }
}
