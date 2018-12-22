package com.jie.space.nettychat.config.handler;

import com.alibaba.fastjson.JSON;
import com.jie.space.nettychat.config.channel.ApplicationChannelContext;
import com.jie.space.nettychat.config.msg.IMMessage;
import com.jie.space.nettychat.config.msg.ResMsg;
import com.jie.space.nettychat.config.msg.WxIMMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@Qualifier("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        WxIMMessage wxmessage = JSON.parseObject(msg.text(), WxIMMessage.class);
        IMMessage imMessage = wxmessage.getImMessage();
        if(imMessage != null){

            String channelId = ctx.channel().id().toString();
            log.info("channelId为：" + channelId);
            String openId = imMessage.getAppId() + "&" + imMessage.getSenderId();

            if(wxmessage.getMessageType() != 1){
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ResMsg.succWithData(channelId + ":发送success"))));
            }else {
                ApplicationChannelContext.addChannel(openId, ctx);
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ResMsg.succWithData(channelId + "授权success"))));
                Map<String, String> responseMap = new HashMap<>();
                for (Map.Entry<String, ChannelHandlerContext> entry : ApplicationChannelContext.onlineUsers.entrySet()) {
                    responseMap.put(entry.getKey(), entry.getValue().channel().id().toString());
                }
                log.info("所有认证过的key有:" + JSON.toJSONString(responseMap));
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            String channelId = ctx.channel().id().toString();
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType = null;
            switch (event.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType ="读写空闲";
                    break;
            }
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ResMsg.succWithData(channelId + ":15秒没有心跳，链接被关闭了，超时类型为：" + eventType))));
            ctx.close();
            log.info(channelId + "链接被关闭了!!!!!!");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(ctx.channel().remoteAddress() + "ChannelId :" + incoming.id() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel incoming = ctx.channel();
        String channelId = incoming.id().toString();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.id() + "在线");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.id() + "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        super.exceptionCaught(ctx, cause);
        if (incoming.isActive()) ctx.close();
        log.info(incoming.id().toString() + "异常:" + cause.toString());
    }

}
