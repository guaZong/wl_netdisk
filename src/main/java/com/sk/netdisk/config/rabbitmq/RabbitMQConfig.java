package com.sk.netdisk.config.rabbitmq;

import com.sk.netdisk.constant.RabbitmqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置类
 * @author lsj
 */
@Configuration
public class RabbitMQConfig {

    @Autowired
    ConnectionFactory connectionFactory;


    /**
     * 消息转换器--将java对象序列化为json,接收时反序列化为java对象
     * @return 消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 它可以在运行时动态创建队列、交换机、绑定关系以及删除这些组件。
     * 通过RabbitAdmin，我们可以在应用程序启动时自动创建所需的队列、交换机和绑定关系，从而避免手动创建这些组件带来的麻烦。
     * @return RabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 注入bean后可以直接调用 private RabbitTemplate rabbitTemplate
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

//
//    /**
//     *      * 这个方法用于创建一个监听器容器，它会监听指定的队列（这里是"queue2"）并注册一个消息监听器（这里是"MyMessageListener2"），
//     *      * 用于处理从该队列中接收到的消息。具体来说，以下是这个方法的几个关键配置项的作用：
//     *      * connectionFactory()：用于指定连接工厂，用于创建与RabbitMQ服务器的连接。
//     *      * queueNames("queue2")：用于指定要监听的队列名称。
//     *      * new MyMessageListener2()：用于指定一个自定义的消息监听器，用于处理从队列中接收到的消息。
//     *      * messageConverter()：用于指定消息转换器，用于将接收到的消息转换为Java对象。
//     *      * container.setConcurrency("1");：用于设置并发消费者的数量。
//     *      * 简单来说，这个方法的作用就是创建一个可以监听队列的容器，并注册一个消息监听器用于处理接收到的消息，
//     *      * 以实现异步消息处理的功能。在启动这个容器后，它将会一直运行并等待从队列中接收到新的消息，然后调用消息监听器处理这些消息。
//     * @return
//     */
//    @Bean
//    public SimpleMessageListenerContainer container2() {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        //这里是消费端消息确认的监听,所以只用监听哪个队列就行
//        container.setQueueNames("test_confirm_queue");
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        container.setMessageListener(new MyMessageListener());
//        container.setPrefetchCount(1000);
//        return container;
//    }

    /**
     * 定义普通的交换机和队列并绑定
     */
    @Bean
    public Queue delQueue(){
        long ttl = 1000 * 60 * 60 * 24 * 30L;
        return QueueBuilder
                .durable(RabbitmqConstants.QUEUE_DEL)
                .deadLetterExchange(RabbitmqConstants.EXCHANGE_DLX)
                .withArgument("x-message-ttl", ttl)
                .build();
    }
    @Bean
    public Exchange delExchange(){
        return ExchangeBuilder.topicExchange(RabbitmqConstants.EXCHANGE_DEL).build();
    }

    @Bean
    public Binding delBind(@Qualifier("delQueue") Queue bootQueue, @Qualifier("delExchange") Exchange bootExchange){
        return BindingBuilder.bind(bootQueue).to(bootExchange).with(RabbitmqConstants.KEY_DEL).noargs();
    }

}

