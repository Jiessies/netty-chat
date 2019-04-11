package com.jie.space.nettychat.config.mq.client;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
@Profile({"default", "product", "gray", "pre", "test"})
public class ProducerConfig {

    /**
     * 声明MQ admin
     */
    @Bean
    protected RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 声明pricingNotifyQ
     */
    @Bean
    Queue queueWxAppletChatMessage(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(MqNameConst.WX_APPLET_CHAT_MESSAGE, true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 声明pricingNotifyQ
     */
    @Bean
    Queue queueWxAppletChatMessageD(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(MqNameConst.WX_APPLET_CHAT_MESSAGE_D, true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 声明 Fanout队列
     */
    @Bean
    FanoutExchange exchangeFanout(RabbitAdmin rabbitAdmin) {
        FanoutExchange fanoutExchange = new FanoutExchange(MqNameConst.WX_APPLET_CHAT_MESSAGE_MQ_EXCHANGE);
        rabbitAdmin.declareExchange(fanoutExchange);
        return fanoutExchange;
    }

    /**
     * 声明 DirectExchange 队列
     */
    @Bean
    DirectExchange exchange(RabbitAdmin rabbitAdmin) {
        DirectExchange directExchange = new DirectExchange(MqNameConst.WX_APPLET_CHAT_MESSAGE_MQ_EXCHANGE_D);
        rabbitAdmin.declareExchange(directExchange);
        return directExchange;
    }

    /**
     * 将 MQ 绑定到 exchange上
     */
    @Bean
    Binding bindingExchange(Queue queueWxAppletChatMessage, FanoutExchange exchangeFanout, RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(queueWxAppletChatMessage).to(exchangeFanout);
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    /**
     * 将 queue 绑定到 exchange上
     */
    @Bean
    Binding bindingExchange(Queue queueWxAppletChatMessageD, DirectExchange exchange, RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(queueWxAppletChatMessageD).to(exchange).with(MqNameConst.WX_APPLET_CHAT_MESSAGE);
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    /**
     * 声明MQ消息模板, 并建立绑定消息方式
     */
    @Bean
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMessagingTemplate rabbitMessagingTemplate = new RabbitMessagingTemplate();
        rabbitMessagingTemplate.setMessageConverter(jackson2Converter());
        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMessagingTemplate;
    }

    /**
     * 声明MQ消息传递转换方式
     */
    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

}
