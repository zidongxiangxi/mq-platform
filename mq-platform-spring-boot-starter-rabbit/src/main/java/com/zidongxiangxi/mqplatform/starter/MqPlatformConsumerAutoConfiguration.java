package com.zidongxiangxi.mqplatform.starter;

import com.zidongxiangxi.mqplatform.consumer.interceptor.IdempotentOperationsInterceptor;
import com.zidongxiangxi.mqplatform.consumer.manager.DefaultConsumerManager;
import com.zidongxiangxi.mqplatform.api.manager.IConsumerManager;
import com.zidongxiangxi.mqplatform.consumer.processor.DefaultRabbitBeanPostProcessor;
import com.zidongxiangxi.mqplatform.consumer.transaction.DefaultRabbitConsumerSqlProvider;
import com.zidongxiangxi.mqplatform.consumer.constant.BeanNameConstants;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * mq发送配置
 *
 * @author chenxudong
 * @date 2019/09/01
 */
@ConditionalOnSingleCandidate(JdbcTemplate.class)
@EnableConfigurationProperties(MqPlatformRabbitProperties.class)
@ConditionalOnProperty(prefix = "spring.rabbitmq.consumer", name = "idempotent", havingValue = "true")
@AutoConfigureAfter({RabbitAutoConfiguration.class})
public class MqPlatformConsumerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({IConsumerManager.class})
    public IConsumerManager consumerManager(JdbcTemplate jdbcTemplate, MqPlatformRabbitProperties rabbitProperties) {
        return new DefaultConsumerManager(jdbcTemplate,
            new DefaultRabbitConsumerSqlProvider(rabbitProperties.getConsumer().getTableName()));
    }

    @Bean(name = BeanNameConstants.INTERNAL_IDEMPOTENT_OPERATIONS_INTERCEPTOR)
    @ConditionalOnMissingBean({IdempotentOperationsInterceptor.class})
    public IdempotentOperationsInterceptor idempotentOperationsInterceptor(IConsumerManager consumerManager,
        MqPlatformRabbitProperties mqPlatformRabbitProperties) {
        return new IdempotentOperationsInterceptor(consumerManager, mqPlatformRabbitProperties.getConsumer());
    }

    @Bean
    public DefaultRabbitBeanPostProcessor defaultRabbitBeanPostProcessor() {
        return new DefaultRabbitBeanPostProcessor();
    }
}
