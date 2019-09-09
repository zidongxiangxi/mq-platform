package com.zidongxiangxi.mqplatform.consumer.processor;

import com.zidongxiangxi.mqplatform.consumer.constant.BeanNameConstants;
import com.zidongxiangxi.mqplatform.consumer.interceptor.IdempotentOperationsInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.util.Objects;

/**
 * rabbit相关bean的后置加工
 *
 * @author chenxudong
 * @date 2019/09/09
 */
@Slf4j
public class DefaultRabbitBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!(bean instanceof SimpleRabbitListenerContainerFactory)
            || !Objects.equals("rabbitListenerContainerFactory", beanName)) {
            return bean;
        }

        IdempotentOperationsInterceptor idempotentAdvice = null;
        try {
            idempotentAdvice = beanFactory.getBean(BeanNameConstants.INTERNAL_IDEMPOTENT_OPERATIONS_INTERCEPTOR,
                IdempotentOperationsInterceptor.class);
        } catch (Throwable ignore) {}
        if (Objects.isNull(idempotentAdvice)) {
            return bean;
        }

        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory =
            (SimpleRabbitListenerContainerFactory) bean;
        Advice[] adviceChain = rabbitListenerContainerFactory.getAdviceChain();
        if (Objects.isNull(adviceChain) || adviceChain.length == 0) {
            rabbitListenerContainerFactory.setAdviceChain(adviceChain);
        } else {
            Advice[] newAdviceChain = new Advice[adviceChain.length + 1];
            System.arraycopy(adviceChain, 0, newAdviceChain, 1, adviceChain.length);
            newAdviceChain[0] = idempotentAdvice;
            rabbitListenerContainerFactory.setAdviceChain(newAdviceChain);
        }
        return bean;
    }
}
