package com.zidongxiangxi.mqplatform.starter;

import com.zidongxiangxi.mqplatform.consumer.config.ConsumerConfig;
import com.zidongxiangxi.mqplatform.producer.config.ProducerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rabbitMq配置
 * 增加生产者和消费者配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class MqPlatformRabbitProperties extends RabbitProperties {
    /**
     * mq发送配置
     */
    private ProducerConfig producer = new ProducerConfig();

    /**
     * mq消费配置
     */
    private ConsumerConfig consumer = new ConsumerConfig();
}
