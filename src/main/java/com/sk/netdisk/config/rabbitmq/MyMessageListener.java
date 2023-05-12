package com.sk.netdisk.config.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.io.IOException;
import java.util.List;
/**
 * Consumer ACk机制
 * 1. 确认接收方式,默认是自动签收,现在设置手动签收
 * 2. 让监听器类实现ChannelAwareMessageListener接口,下面有个onmessage方法里面有channel
 * 3. 如果消息处理成功,则调用channel的basicAck接收
 * 4. 如果消息处理失败,则调用channel的basicNack拒绝接收,可以让中间件重新发送
 * @author lsj
 */
public class MyMessageListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag=message.getMessageProperties().getDeliveryTag();

       try {
           //1.接受转换消息
           System.out.println(new String(message.getBody()));
           //2.处理业务逻辑
           System.out.println("处理业务逻辑");
           //3.手动签收
           channel.basicAck(deliveryTag,true);
       }catch (IOException e){
           //4.拒绝签收
           channel.basicNack(deliveryTag,true,true);
       }

    }

    /**
     * 该方法会在消息到达指定队列时被自动调用，并将接收到的消息作为参数传入。
     *
     * 在实现 MessageListener 接口时，需要重写 onMessage(Message message) 方法，
     * 并根据业务需求进行消息处理。例如，可以使用消息转换器将接收到的消息转换为指定格式的数据，并将其存储到数据库或者发送到其他系统中。
     * 或者有一个订单超时队列,如果订单超时就通过这个方法将该订单进行取消,在数据库里面
     * @param message 接收到的消息
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("onMessage");
    }

    @Override
    public void containerAckMode(AcknowledgeMode mode) {

    }

    /**
     * 当 MessageListener 监听的队列上存在多个消息时，RabbitMQ 会将这些消息打包成一个批次（batch），
     * 并将其一次性传递给 onMessageBatch(List<Message> messages) 方法，以提高消息处理的效率。
     * 如果 MessageListener 接口的实现类同时实现了 onMessage(Message message) 方法和 onMessageBatch(List<Message> messages) 方法，
     * 那么在消息到达队列时，优先调用 onMessageBatch(List<Message> messages) 方法，
     * 如果队列中只有单个消息，则调用 onMessage(Message message) 方法。
     * @param messages
     */
    @Override
    public void onMessageBatch(List<Message> messages) {
        System.out.println("onMessageBatch");
    }
}
