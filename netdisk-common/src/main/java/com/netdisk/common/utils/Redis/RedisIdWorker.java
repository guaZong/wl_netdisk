package com.netdisk.common.utils.Redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    @Resource
    RedisUtil redisUtil;
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 定义初始时间,开始时间戳,这个数是代表的日期是2022/1/1 0:0:00
     */
    private static final long BEGIN_TIMESTAMP =1640995200L;

    public RedisIdWorker(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * redis 全局id生成器
     * @param keyPrefix key值
     * @return
     */
    public long nextId(String keyPrefix){
        //1.生成时间戳-当前时间减去初始时间
        LocalDateTime now = LocalDateTime.now();
        long nowSecond=now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp=nowSecond-BEGIN_TIMESTAMP;
        //2.生程序列号
            //生成一天的订单量-date
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count= redisTemplate.opsForValue().increment("incr:"+keyPrefix+":"+date);
        //3.拼接并返回--位运算 将时间戳向左移动32位,随后将序列号拼接上前32位
        return timeStamp << 32 | count;
    }

}
