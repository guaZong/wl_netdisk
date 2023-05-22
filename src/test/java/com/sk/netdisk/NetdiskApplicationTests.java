package com.sk.netdisk;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sk.netdisk.config.rabbitmq.RabbitMQConfig;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.service.impl.DataServiceImpl;
import com.sk.netdisk.util.Redis.RedisIdWorker;
import com.sk.netdisk.util.SendSmsUtil;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class NetdiskApplicationTests {
    @Autowired
    RedisIdWorker redisIdWorker;
    @Autowired
    DataServiceImpl dataService;

    @Test
    void contextLoads() throws InterruptedException {
//        ThreadPoolExecutor executor=new ThreadPoolExecutor(24,30,10, TimeUnit.MILLISECONDS,
//                new LinkedBlockingDeque<>());
//        Runnable task=()->{
//            long sjGoods = redisIdWorker.nextId("sjGoods");
//            System.out.println(sjGoods);
//        };
//        for (int i = 0; i < 100; i++) {
//            executor.submit(task);
//        }
        new Thread(() -> {
            System.out.println(redisIdWorker.nextId("sjGoods"));
        }).start();

        new Thread(() -> {
            System.out.println(redisIdWorker.nextId("sjGoods"));
        }).start();

        new Thread(() -> {
            System.out.println(redisIdWorker.nextId("sjGoods"));
        }).start();

        new Thread(() -> {
            System.out.println(redisIdWorker.nextId("sjGoods"));
        }).start();
        Thread.sleep(5000);
    }

    @Autowired
    SendSmsUtil sendSmsUtil;
//    @Test
//    public void testSend() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            sendSmsUtil.sendSms("15036538191","123456");
//        }
//
//    }


    @Test
    public void testpro() throws Exception {

        rabbitTemplate.convertAndSend("test1_exchange", "", "haha");
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    @Test
    public void testConfirm() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        rabbitTemplate.convertAndSend("test1_exchange", "bind", "66666");
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * confirm方法
             * @param correlationData 相关配置信息
             * @param ack exchange交换机是否成功收到消息
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("发送方法被执行了,不知道返回的结果是啥");
                System.out.println("发送是否成功呢+ " + ack);
                if (!ack) {
                    System.out.println("失败原因: " + cause);
                }
                latch.countDown();
            }
        });
        // 等待确认
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                System.out.println("发送失败，等待超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("发送失败，等待被中断");
        }
    }

    /**
     * 回退模式,当消息发送给exchange之后,exchange路由到Queue失败时,才会执行ReturnCallBack
     * 1.开启回退模式
     * 2. 设置returncallback
     * 3.设置exchange处理消息模式
     * 1.如果没有消息路由到queue,那么默认丢消息
     * 2.如果没有路由到queue,将消息给发送方returncallback
     */
    @Test
    public void testReturn() {
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String toutingKey) {
                System.out.println(message);
                System.out.println(replyCode);
                System.out.println(replyText);
                System.out.println(exchange);
                System.out.println(toutingKey);
            }
        });

        rabbitTemplate.convertAndSend("test1_exchange", "bind", "66666");
    }


    /**
     * Consumer ACk机制
     * 1. 确认接收方式,默认是自动签收,现在设置手动签收
     * 2. 让监听器类实现ChannelAwareMessageListener接口,下面有个onmessage方法里面有channel
     * 3. 如果消息处理成功,则调用channel的basicAck接收
     * 4. 如果消息处理失败,则调用channel的basicNack拒绝接收,可以让中间件重新发送
     */


    @Autowired
    DataMapper dataMapper;

    @Test
    public void test5() {
        List<Integer> dataIds=new ArrayList<>();
        dataIds.add(51548);
        System.out.println(dataMapper.findDataByIds(dataIds));

    }


}
