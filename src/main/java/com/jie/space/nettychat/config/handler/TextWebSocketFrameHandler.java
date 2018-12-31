package com.jie.space.nettychat.config.handler;

import com.alibaba.fastjson.JSON;
import com.jie.space.nettychat.config.channel.ApplicationChannelContext;
import com.jie.space.nettychat.config.msg.IMMessage;
import com.jie.space.nettychat.config.msg.ResMsg;
import com.jie.space.nettychat.config.msg.WxIMMessage;
import com.jie.space.nettychat.utils.YklyRestTemplate;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@Qualifier("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static String serverPort;

    @Value("${server.port}")
    public void setApiKey(String serverPort) {
        this.serverPort = serverPort;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private YklyRestTemplate restTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,

                                TextWebSocketFrame msg) throws Exception {

        WxIMMessage wxmessage = null;
        IMMessage imMessage = null;
        try {
            wxmessage = JSON.parseObject(msg.text(), WxIMMessage.class);
            imMessage = wxmessage.getImMessage();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("channelRead0异常:", e);
            ctx.close();
        }
        if(imMessage != null){
            if (wxmessage.getMessageType() == 1) {
                singleServerAuth(ctx, wxmessage, imMessage);
            }else if (wxmessage.getMessageType() == 4){
//                receivedAndSendChatMessage(ctx, wxmessage, imMessage);
            }else {
                log.info("singleServerAuth方法，错误的消息类型！");
            }
        }
    }

    private void singleServerAuth(ChannelHandlerContext ctx, WxIMMessage wxmessage, IMMessage imMessage) {

        try {
            if(StringUtils.isEmpty(imMessage.getSenderId()) || "null".equals(imMessage.getSenderId()) || "undefined".equals(imMessage.getSenderId())){
                log.error("singleServerAuth方法认证时 SenderId为: " + imMessage.getSenderId());
                return;
            }

            String channelId = ctx.channel().id().toString();
            String openId = imMessage.getAppId() + "&" + imMessage.getSenderId();
            String historyAddress = redisTemplate.opsForValue().get("SINGLE_AUTHENTICATION_ADDRESS&" + openId);

            if (historyAddress != null) {
                //需要删除其他服务器的channel
                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("openId", openId);
                String response = restTemplate.lPost(historyAddress + "/historyChannelDelete", null, requestMap);
                log.info("singleServerAuth方法,认证时，删除已经认证过的ChannelHandlerContext，请求的服务器为:" + historyAddress + ",请求结入参为:" + JSON.toJSONString(requestMap) + "，请求结果为:" + response);
            } else {
                ChannelHandlerContext historyCtx = ApplicationChannelContext.getChannel(openId);
                if (historyCtx != null) {
                    String historyKey = "SINGLE_AUTHENTICATION_KEY&" + historyCtx.channel().id().toString();
                    String addressKey = "SINGLE_AUTHENTICATION_ADDRESS&" + openId;
                    boolean hasHisKey = redisTemplate.hasKey(historyKey);
                    boolean hasAddKey = redisTemplate.hasKey(addressKey);
                    if(hasHisKey){
                        redisTemplate.delete(historyKey);
                    }
                    if(hasAddKey){
                        redisTemplate.delete(addressKey);
                    }
                    historyCtx.close();
                    ApplicationChannelContext.removeChannel(openId);
                    log.info("singleServerAuth方法,删除historyKey:" + historyKey + "并关闭Channel!");
                }
            }

            boolean flag = authWebsocket(ctx, channelId, openId);
            if(flag){
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ResMsg.succWithData("success"))));
            }else {
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ResMsg.succWithData("fail"))));
            }

            Map<String, String> responseMap = new HashMap<>();
            for (Map.Entry<String, ChannelHandlerContext> entry : ApplicationChannelContext.onlineUsers.entrySet()) {
                responseMap.put(entry.getKey(), entry.getValue().channel().id().toString());
            }
            log.info("所有认证过的key有:" + JSON.toJSONString(responseMap));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("singleServerAuth接口异常：", e);
        }
    }

    private Boolean authWebsocket(ChannelHandlerContext ctx, String channelId, String openId){

        Boolean flag = true;
        redisTemplate.setEnableTransactionSupport(true);
        try {
            redisTemplate.multi();

            redisTemplate.opsForValue().set("SINGLE_AUTHENTICATION_KEY&" + channelId, openId);
            String ipAddress = getSingleHostAddress();
            redisTemplate.opsForValue().set("SINGLE_AUTHENTICATION_ADDRESS&" + openId, ipAddress);
            log.info(openId + "认证时存储对应的channelId为:" + channelId + ",存储对应的ipAddress为:" + ipAddress);
            ApplicationChannelContext.addChannel(openId, ctx);

            redisTemplate.exec();
        } catch (Exception e) {
            e.printStackTrace();
            redisTemplate.discard();
            flag = false;
            log.error("authWebsocket接口异常：", e);
        }
        return flag;
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
        String openId = redisTemplate.opsForValue().get("SINGLE_AUTHENTICATION_KEY&" + channelId);

        if (openId == null) {
            for (Map.Entry<String, ChannelHandlerContext> entry : ApplicationChannelContext.onlineUsers.entrySet()) {
                if (channelId.equals(entry.getValue().channel().id().toString())) {
                    openId = entry.getKey();
                    break;
                }
            }
        }

        if (openId != null) {
            ApplicationChannelContext.removeChannel(openId);
            redisTemplate.delete("SINGLE_AUTHENTICATION_ADDRESS&" + openId);
            redisTemplate.opsForValue().set("LAST_DROP_TIME&" + openId, String.valueOf(System.currentTimeMillis()), 86400, TimeUnit.SECONDS);
            log.info("掉线时删除redis用户:" + "SINGLE_AUTHENTICATION_ADDRESS&" + openId + " 成功。");

        }

        redisTemplate.delete("SINGLE_AUTHENTICATION_KEY&" + channelId);
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

    public static String getSingleHostAddress() throws UnknownHostException {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (ip.isSiteLocalAddress()) {
                        return "http://" + ip.getHostAddress() + ":" + serverPort;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
        return "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
    }
}
