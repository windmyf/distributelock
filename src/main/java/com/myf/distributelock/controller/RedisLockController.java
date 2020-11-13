package com.myf.distributelock.controller;

import com.myf.distributelock.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author : myf
 * @Date : 2020/10/27 21:49
 * @Description : 基于redis的分布式锁
 * @Version : 1.0
 */
@RestController
@Slf4j
public class RedisLockController {

    @Autowired
    private RedisTemplate redisTemplate;
    String key = "redisKey";
    String value = UUID.randomUUID().toString();


    /**
     * @desc ：
     * @author : windmyf
     * @date : 2020/10/28 22:23
     */
    @RequestMapping("redisLock")
    public String redisLock() {
        log.info("我进入了方法");

        RedisLock redisLock = new RedisLock(redisTemplate, "redisKey", 30);

        boolean lock = redisLock.getLock();

        if (lock) {
            log.info("我获得了锁");
            try {
                // TODO 此处模拟处理其他任务
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                boolean b = redisLock.unLock();

                log.info("释放锁结果：{}",b);
            }
        }
        log.info("方法执行完成");
        return "方法执行完成";
    }
}
