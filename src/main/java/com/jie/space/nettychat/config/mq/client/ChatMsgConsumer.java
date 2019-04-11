package com.jie.space.nettychat.config.mq.client;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ChatMsgConsumer {

    @RabbitListener(queues = MqNameConst.WX_APPLET_CHAT_MESSAGE, containerFactory = "rabbitListenerContainerFactory")
    @RabbitHandler
    public void process(Channel channel, Message message) {

        try {
            log.info(JSON.toJSONString(message));
            log.info(new String(message.getBody()));
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //丢弃这条消息
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            log.error("mq receiver fail : " + e.toString());
        }
    }

}
