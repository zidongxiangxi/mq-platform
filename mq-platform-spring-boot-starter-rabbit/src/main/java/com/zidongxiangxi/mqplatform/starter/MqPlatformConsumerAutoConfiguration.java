package com.zidongxiangxi.mqplatform.starter;

import com.zidongxiangxi.mqplatform.consumer.config.MessageListenerAop;
import com.zidongxiangxi.mqplatform.consumer.manager.DefaultConsumerManager;
import com.zidongxiangxi.mqplatform.api.manager.IConsumerManager;
import com.zidongxiangxi.mqplatform.consumer.transaction.DefaultRabbitConsumerSqlProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class MqPlatformConsumerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({IConsumerManager.class})
    public IConsumerManager consumerManager(JdbcTemplate jdbcTemplate, MqPlatformRabbitProperties rabbitProperties) {
        return new DefaultConsumerManager(jdbcTemplate,
            new DefaultRabbitConsumerSqlProvider(rabbitProperties.getConsumer().getTableName()));
    }

    @Bean
    @ConditionalOnMissingBean({MessageListenerAop.class})
    public MessageListenerAop messageListenerAop(IConsumerManager consumerManager) {
        return new MessageListenerAop(consumerManager);
    }
}
