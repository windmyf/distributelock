package com.myf.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author : myf
 * @Date : 2020/10/26 23:14
 * @Description : 应用锁
 * @Version : 1.0
 */
@RestController
@Slf4j
public class DemoController {

    private Lock lock = new ReentrantLock();

    @RequestMapping("singleLock")
//    @Transactional(rollbackFor = Exception.class)
    public String singleLock(){

        log.info("我进入了方法");
//        lock.lock();

        // TODO 利用数据库 select …… for update

        // 如果查询为空，则抛出异常
        log.info("我进入了锁");
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        lock.unlock();

        return "我已经执行完成了";
    }

}
