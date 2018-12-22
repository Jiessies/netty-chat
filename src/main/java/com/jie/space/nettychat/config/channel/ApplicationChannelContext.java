package com.jie.space.nettychat.config.channel;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApplicationChannelContext {

    public static Map<String, ChannelHandlerContext> onlineUsers = new ConcurrentHashMap<>();

    public static void addChannel(String openId, ChannelHandlerContext channelHandlerContext) {
        onlineUsers.put(openId, channelHandlerContext);
    }

    public static void removeChannel(String openId) {
        onlineUsers.remove(openId);
    }

    public static ChannelHandlerContext getChannel(String openId) {
        return onlineUsers.get(openId);
    }

}
