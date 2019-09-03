package com.zidongxiangxi.mqplatform.api.transaction;

/**
 * 事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface ITransactionListener {
    void beforeCommit();

    void afterCommit();

    void afterCompletion();

    void suspend();

    void resume();
}
