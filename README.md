# 分布式锁

### 一、锁解决超卖问题

  * 超卖现象一：库存为1，产生了2笔订单；
  
        解决办法：(1) 扣减库存不在程序中进行，而是通过数据库; 
                 (2) 向数据库传递库存增量，扣减一个库存，增量为-1；
                 (3) 在数据库 update 语句计算库存，通过update行锁解决并发；
  
  * 超卖现象二：系统库存变成了负数
  
        产生原因：(1) 并发检验库存，造成库存充足的假象；
                 (2) update 更新库存，导致库存为负数；
                 
        解决方法：方法一： 更新完再检索，如果商品库存为负数则抛出异常，程序回滚；【此方法未用到锁】
                 方法二：(1) 校验库存、扣减库存统一加锁，使之成为原子性操作；
                       （2） 并发时，只有获得锁的线程才能校验库存、扣减库存；
                        (3) 扣减库存结束后释放锁；
                        (4) 确保库存不会扣成负数；
                        
               
        基于synchronized解决（方法：需要手动控制事务
                             块：类锁、对象锁）
        
        基于ReetrantLock解决（ lock()，unlock() ）         
        
        
### 二、分布式锁解决超卖问题

#### 1、基于数据库悲观锁的分布式锁
        
     步骤：
        (1) 多个进程、多个线程访问共同组件数据库
        (2) 通过 select……for update 访问同一条数据
        (3) for update 锁定数据，其他线程只能等待
        
     优缺点：
        (1) 优点：简单方便、易于理解、易于操作
        (2) 缺点：并发量大时，对数据库压力较大
        (3) 建议： 作为锁的数据库与业务数据库分开
        
        
#### 2、基于Redis的Setnx实现分布式锁     

    实现原理：(1) 获取锁的Redis命令
         SET resource_name my_random_value NX PX 3000
         
             resource_name：资源名称，可根据不同的业务区分不同的锁
             my_random_value：随机值，每个线程的随机值都不同，用于释放锁时的校验
             NX ：key不存在时设置成功，key存在时设置不成功     
             PX ：自动失效时间，出现异常情况，锁可以过期失效
             
            (2) 利用NX的原子性，多线程并发时，只有一个线程可以设置成功
            (3) 设置成功即获得锁，可以执行后续业务处理
            (4) 如果出现了异常，过了锁的有效期，锁自动释放
            
            (5) 释放锁采用Redis的delete命令
                释放锁时校验之前设置的随机数，相同才能释放
            (6) 释放锁的LUA脚本
                if redis.call("get",KEYS[1]) == ARGV[1] then
                    return redis.call("del",KEYS[1])
                else
                    return 0
                end
                
                
    问题：定义任务集群部署，任务重复执行，如何利用分布式锁解决
    
    
#### 3、基于Zookeeper的瞬时节点实现分布式锁

  (1) Zookeeper 的数据结构
        * 持久节点：不会消失，除非手动删除
        * 瞬时节点：有序，瞬时节点不可再有子节点，会话结束后瞬时节点自动消失
        * zookeeper 的观察器：
                    可设置观察器的三个方法，getData();  getChildren();  exists();
                    节点数据发生变化，发送给客户端
                    观察器只能监控一次，再监控需重新设置
        
  (2) 实现原理：
        利用Zookeeper的瞬时有序节点的特性；
        多线程并发创建瞬时节点时，得到有序的序列；
        序号最小的线程获得锁
        其他的线程则监听自己序号的前一个序号
        前一个线程执行完成，删除自己序号的节点
        下一个序号的线程得到通知，继续执行
        
     创建节点时已经确定线程执行顺序
  
 #### 4、curator客户端实现分布式锁 
    
  curator已经实现了分布式锁，只需要调用 
  
 #### 5、基于Redisson实现分布式锁
 
   Redisson已经实现了分布式锁，只需要调用
   ---> 引入Redisson的jar包
   ---> 进行Redisson与redis的配置
   ---> 使用分布式锁     