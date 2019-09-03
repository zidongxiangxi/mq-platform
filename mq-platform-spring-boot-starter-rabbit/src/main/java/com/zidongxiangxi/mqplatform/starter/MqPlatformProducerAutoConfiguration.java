package com.zidongxiangxi.mqplatform.starter;

import com.zidongxiangxi.mqplatform.producer.RabbitTransactionTemplate;
import com.zidongxiangxi.mqplatform.producer.callback.RabbitMqConfirmCallback;
import com.zidongxiangxi.mqplatform.producer.manager.rabbit.RabbitMqProducerManager;
import com.zidongxiangxi.mqplatform.api.transaction.DefaultTransactionSynchronization;
import com.zidongxiangxi.mqplatform.producer.transaction.DefaultRabbitProducerSqlProvider;
import com.zidongxiangxi.mqplatform.producer.transaction.RabbitProducerTransactionListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;

/**
 * mq发送配置
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@AutoConfigureAfter(MqPlatformJdbcAutoConfiguration.class)
@AutoConfigureBefore(RabbitAutoConfiguration.class)
@ConditionalOnMissingBean({RabbitTemplate.class})
@ConditionalOnSingleCandidate(JdbcTemplate.class)
@EnableConfigurationProperties(MqPlatformRabbitProperties.class)
@ConditionalOnProperty(prefix = "spring.rabbitmq.producer", name = "transaction", havingValue = "true", matchIfMissing = true)
public class MqPlatformProducerAutoConfiguration {
    /**
     * 不管有没有CachingConnectionFactory对象都需要重新定义一个，保证publisherConfirms为true
     */
    @Configuration
    protected static class MqPlatformRabbitConnectionFactoryCreator {
        /**
         * 定义一个publisherConfirms为true的CachingConnectionFactory
         *
         * @param properties 配置
         * @param connectionNameStrategy 连接名称策略
         * @return CachingConnectionFactory
         * @throws Exception
         */
        @Bean
        @Primary
        public CachingConnectionFactory mqPlatformRabbitConnectionFactory(
                MqPlatformRabbitProperties properties,
                ObjectProvider<ConnectionNameStrategy> connectionNameStrategy)
                throws Exception {
            PropertyMapper map = PropertyMapper.get();
            CachingConnectionFactory factory = new CachingConnectionFactory(
                    getRabbitConnectionFactoryBean(properties).getObject());
            map.from(properties::determineAddresses).to(factory::setAddresses);
            map.from(properties::isPublisherReturns).to(factory::setPublisherReturns);
            MqPlatformRabbitProperties.Cache.Channel channel = properties.getCache().getChannel();
            map.from(channel::getSize).whenNonNull().to(factory::setChannelCacheSize);
            map.from(channel::getCheckoutTimeout).whenNonNull().as(Duration::toMillis)
                    .to(factory::setChannelCheckoutTimeout);
            MqPlatformRabbitProperties.Cache.Connection connection = properties.getCache()
                    .getConnection();
            map.from(connection::getMode).whenNonNull().to(factory::setCacheMode);
            map.from(connection::getSize).whenNonNull()
                    .to(factory::setConnectionCacheSize);
            map.from(connectionNameStrategy::getIfUnique).whenNonNull()
                    .to(factory::setConnectionNameStrategy);
            factory.setPublisherConfirms(true);
            return factory;
        }

        private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(
                MqPlatformRabbitProperties properties) throws Exception {
            PropertyMapper map = PropertyMapper.get();
            RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
            map.from(properties::determineHost).whenNonNull().to(factory::setHost);
            map.from(properties::determinePort).to(factory::setPort);
            map.from(properties::determineUsername).whenNonNull()
                    .to(factory::setUsername);
            map.from(properties::determinePassword).whenNonNull()
                    .to(factory::setPassword);
            map.from(properties::determineVirtualHost).whenNonNull()
                    .to(factory::setVirtualHost);
            map.from(properties::getRequestedHeartbeat).whenNonNull()
                    .asInt(Duration::getSeconds).to(factory::setRequestedHeartbeat);
            MqPlatformRabbitProperties.Ssl ssl = properties.getSsl();
            if (ssl.isEnabled()) {
                factory.setUseSSL(true);
                map.from(ssl::getAlgorithm).whenNonNull().to(factory::setSslAlgorithm);
                map.from(ssl::getKeyStoreType).to(factory::setKeyStoreType);
                map.from(ssl::getKeyStore).to(factory::setKeyStore);
                map.from(ssl::getKeyStorePassword).to(factory::setKeyStorePassphrase);
                map.from(ssl::getTrustStoreType).to(factory::setTrustStoreType);
                map.from(ssl::getTrustStore).to(factory::setTrustStore);
                map.from(ssl::getTrustStorePassword).to(factory::setTrustStorePassphrase);
                map.from(ssl::isValidateServerCertificate).to((validate) -> factory
                        .setSkipServerCertificateValidation(!validate));
                map.from(ssl::getVerifyHostname)
                        .to(factory::setEnableHostnameVerification);
            }
            map.from(properties::getConnectionTimeout).whenNonNull()
                    .asInt(Duration::toMillis).to(factory::setConnectionTimeout);
            factory.afterPropertiesSet();
            return factory;
        }
    }

    /**
     * 定义支持事务的RabbitTemplate
     */
    @Configuration
    @Import(MqPlatformRabbitConnectionFactoryCreator.class)
    protected static class MqPlatformRabbitTemplateConfiguration {

        private final MqPlatformRabbitProperties properties;

        private final ObjectProvider<MessageConverter> messageConverter;

        public MqPlatformRabbitTemplateConfiguration(MqPlatformRabbitProperties properties,
                                           ObjectProvider<MessageConverter> messageConverter) {
            this.properties = properties;
            this.messageConverter = messageConverter;
        }

        /**
         * 定义mq消息的数据库manager
         *
         * @param jdbcTemplate jdbcTemplate实例
         * @return mq消息的数据库manager
         */
        @Bean
        public RabbitMqProducerManager rabbitProducerManager(MqPlatformRabbitProperties properties, JdbcTemplate jdbcTemplate) {
            return new RabbitMqProducerManager(jdbcTemplate,
                new DefaultRabbitProducerSqlProvider(properties.getProducer().getTableName()),
                properties.getProducer().getMaxExecuteTimes());
        }

        @Bean
        @Primary
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitMqProducerManager producerManager) {
            PropertyMapper map = PropertyMapper.get();
            DefaultTransactionSynchronization transactionSynchronization = new DefaultTransactionSynchronization();
            RabbitTransactionTemplate template = new RabbitTransactionTemplate(producerManager, transactionSynchronization);
            template.setConnectionFactory(connectionFactory);
            template.setMandatory(true);
            template.setConfirmCallback(new RabbitMqConfirmCallback(producerManager));

            MessageConverter messageConverter = this.messageConverter.getIfUnique();
            if (messageConverter != null) {
                template.setMessageConverter(messageConverter);
            }
            MqPlatformRabbitProperties.Template properties = this.properties.getTemplate();
            map.from(properties::getReceiveTimeout).whenNonNull().as(Duration::toMillis)
                    .to(template::setReceiveTimeout);
            map.from(properties::getReplyTimeout).whenNonNull().as(Duration::toMillis)
                    .to(template::setReplyTimeout);
            map.from(properties::getExchange).to(template::setExchange);
            map.from(properties::getRoutingKey).to(template::setRoutingKey);
            map.from(properties::getDefaultReceiveQueue).whenNonNull()
                    .to(template::setDefaultReceiveQueue);

            transactionSynchronization.setTransactionListener(new RabbitProducerTransactionListener(template));
            return template;
        }
    }
}
