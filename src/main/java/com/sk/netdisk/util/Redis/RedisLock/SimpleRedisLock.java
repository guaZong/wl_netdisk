package com.sk.netdisk.util.Redis.RedisLock;

import com.sk.netdisk.util.Redis.RedisUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁--简单实现
 * @author lsj
 */
public class SimpleRedisLock implements ILock{

    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean tryLock(long timeout) {
        //获取当前线程标识
        long threadId=Thread.currentThread().getId();
        //设置setnx互斥锁,属于悲观锁,key是name,value是threadId,过期时间是timeout
        Boolean aBoolean = stringRedisTemplate.opsForValue()
                .setIfAbsent("lock:"+name, threadId + "", timeout, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(aBoolean);
    }

    @Override
    public void unLock() {
        stringRedisTemplate.delete("lock:"+name);
    }
}
