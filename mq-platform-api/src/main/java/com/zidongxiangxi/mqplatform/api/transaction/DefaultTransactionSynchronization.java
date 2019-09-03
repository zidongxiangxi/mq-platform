package com.zidongxiangxi.mqplatform.api.transaction;

import lombok.Setter;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class DefaultTransactionSynchronization implements TransactionSynchronization {
    @Setter
    private ITransactionListener transactionListener;

    @Override
    public void suspend() {
        if (transactionListener != null) {
            transactionListener.suspend();
        }
    }

    @Override
    public void resume() {
        if (transactionListener != null) {
            transactionListener.resume();
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (readOnly || transactionListener == null) {
            return;
        }
        transactionListener.beforeCommit();
    }

    @Override
    public void beforeCompletion() {
        if (transactionListener != null) {
            transactionListener.beforeCommit();
        }
    }

    @Override
    public void afterCommit() {
        if (transactionListener != null) {
            transactionListener.afterCommit();
        }
    }

    @Override
    public void afterCompletion(int status) {
        if (transactionListener != null) {
            transactionListener.afterCompletion();
        }
    }
}
