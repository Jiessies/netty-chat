package com.jie.space.nettychat.config.mq.client;

public interface MqNameConst {

    String WX_APPLET_CHAT_MESSAGE = "wx-appletChatMessage"; //聊天队列
    String WX_APPLET_CHAT_MESSAGE_D = "wx-appletChatMessage_D"; //聊天队列
    String WX_APPLET_CHAT_MESSAGE_MQ_EXCHANGE = "wx-appletChatMessage-mq-exchange"; //广播exchange队列
    String WX_APPLET_CHAT_MESSAGE_MQ_EXCHANGE_D = "wx-appletChatMessage-mq-exchange_D"; //广播exchange队列
}
