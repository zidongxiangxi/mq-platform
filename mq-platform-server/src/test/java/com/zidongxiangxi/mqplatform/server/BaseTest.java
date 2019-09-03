package com.zidongxiangxi.mqplatform.server;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支持事物，@SpringBootTest 事物默认自动回滚
 *
 * @author chenxudong
 * @since 2019/08/30/16 17:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
public class BaseTest {

    public static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

}
