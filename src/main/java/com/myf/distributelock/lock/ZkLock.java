package com.myf.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @Author : myf
 * @Date : 2020/10/28 22:40
 * @Description : zookeeper实现分布式锁
 * @Version : 1.0
 */
@Slf4j
public class ZkLock implements AutoCloseable, Watcher {

    private ZooKeeper zooKeeper;
    String znode = null;

    public ZkLock() throws IOException {
        this.zooKeeper = new ZooKeeper("localhost:2181", 10000, this);
    }

    /**
     * @desc ：获取分布式锁
     * @author : windmyf
     * @date : 2020/10/28 22:44
     */
    public boolean getLock(String businessCode) {
        try {
            Stat exists = zooKeeper.exists("/" + businessCode, false);

            if (exists != null) {
                // 创建业务根节点
                zooKeeper.create("/" + businessCode, businessCode.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            // 创建瞬时有序节点
            znode = zooKeeper.create("/" + businessCode + "/" + businessCode + "_", businessCode.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            // 获取业务节点下所有子节点
            List<String> childrenNodes = zooKeeper.getChildren("/" + businessCode, false);

            // 子节点排序
            Collections.sort(childrenNodes);
            // 获取序号最小(第一个)子节点
            String firstNode = childrenNodes.get(0);
            // 如果创建的节点时第一个子节点，则获得锁
            if (znode.endsWith(firstNode)){
                return true;
            }
            // 不是第一个子节点，则监听前一个节点
            String lastNode = firstNode;
            for (String node:childrenNodes){
                if (znode.endsWith(node)){
                    zooKeeper.exists("/"+businessCode+"/"+lastNode,true);
                    break;
                }else {
                    lastNode = node;
                }
            }
            // 因为等待的时候锁还会释放
            synchronized (this){
                wait();
            }

            return true;


        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @desc ：释放锁
     * @author : windmyf
     * @date : 2020/10/28 23:05
     */
    @Override
    public void close() throws Exception {
        zooKeeper.delete(znode,-1);
        zooKeeper.close();
        log.info("锁已经释放");
    }

    /**
     * @desc ：监听事件
     * @author : windmyf
     * @date : 2020/10/28 23:06
     */
    @Override
    public void process(WatchedEvent watchedEvent) {

        if (watchedEvent.getType() == Event.EventType.NodeDeleted){
            synchronized (this){
                notify();
            }

        }
    }
}
