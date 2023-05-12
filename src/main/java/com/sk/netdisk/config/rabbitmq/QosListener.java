package com.sk.netdisk.config.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * Consumer限流机制
 * 1.确保ack机制为手动确认-->手动确认才会生效
 * 2.listener-container配置属性 perfetch=1,表示消费端每次从mq拉取一条消息来消费,直到手动确认消费完毕之后才会拉取吓一条消息
 * @author lsj
 */
public class QosListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //1.接收消息
        System.out.println(message.getBody());
    }
}
