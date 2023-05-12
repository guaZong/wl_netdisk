package com.sk.netdisk.util.Redis.RedisLock;

/**
 * Redis分布式锁
 * @author lsj
 */
public interface ILock {
    /**
     * 获取锁
     * @param timeout 过期时间
     * @return
     */
    boolean tryLock(long timeout);

    /**
     * 释放锁
     */
    void unLock();
}
