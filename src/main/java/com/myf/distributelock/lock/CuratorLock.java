package com.myf.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author : wind-myf
 * @version : 1.0
 * @date : 2020/11/12 17:06
 * @desc : curator实现分布式锁
 */
@Slf4j
public class CuratorLock {
    public String curatorLock() {
        ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", backoffRetry);
        client.start();

        InterProcessMutex mutex = new InterProcessMutex(client, "/order");
        try {
            if(mutex.acquire(30, TimeUnit.SECONDS)){
                try {
                    log.info("我获得了锁！");
                }finally {
                    mutex.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();

        return "我释放了锁！";
    }
}
