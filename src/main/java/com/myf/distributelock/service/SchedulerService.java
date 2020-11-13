package com.myf.distributelock.service;

import com.myf.distributelock.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author : myf
 * @Date : 2020/10/28 21:23
 * @Description : 定时任务
 * @Version : 1.0
 */
@Slf4j
@Service
public class SchedulerService {
    @Resource
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSms(){

        // 获取分布式锁
        try (RedisLock redisLock = new RedisLock(redisTemplate,"autoSms",30)){
            if (redisLock.getLock()){

                log.info("向电话为************的用户发送短信！");
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
