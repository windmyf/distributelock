package com.myf.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author : wind-myf
 * @date : 2020/11/12 17:21
 * @desc : 基于Redisson实现分布式锁
 * @version : 1.0
 */
@Slf4j
public class RedissonLock {
    
    @Autowired
    RedissonClient redissonClient;

    public void redissonLock(){
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://192.144.191.163:6379");
//        RedissonClient redissonClient = Redisson.create(config);

        // 使用分布式锁
        RLock lock = redissonClient.getLock("order");

        try {
            // 获得锁
            lock.lock(30, TimeUnit.SECONDS);
            // 模拟业务
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 释放锁
            lock.unlock();
        }
    }
}
