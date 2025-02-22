package com.angenao.gulimall.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: test001
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/4 17:53
 * @Version: 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class test001 {

    @Autowired
    RedissonClient redisson;
    @Test
    public void redissionConfigTest() {
        RLock lock = redisson.getLock("test-Hello"+ UUID.randomUUID().toString());
        lock.lock(30, TimeUnit.SECONDS);

    }
}
