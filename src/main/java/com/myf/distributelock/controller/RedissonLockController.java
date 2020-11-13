package com.myf.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author : wind-myf
 * @date : 2020/11/12 17:33
 * @desc : Redisson实现分布式锁
 * @version : 1.0
 */
@RestController
@Slf4j
public class RedissonLockController {

    @RequestMapping("redissonLock")
    public String redissonLock(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.144.191.163:6379");
        RedissonClient redissonClient = Redisson.create(config);

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

        return "我释放了锁！";
    }
}
