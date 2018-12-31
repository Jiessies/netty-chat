package com.jie.space.nettychat.service.impl;

import com.jie.space.nettychat.config.channel.ApplicationChannelContext;
import com.jie.space.nettychat.config.msg.ResMsg;
import com.jie.space.nettychat.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResMsg historyChannelDelete(String openId) {
        ResMsg resMsg;
        try {
            String flag = "fail";
            ChannelHandlerContext historyCtx = ApplicationChannelContext.getChannel(openId);
            if(historyCtx != null){
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
                flag = "success";
                log.info("historyChannelDelete方法,删除historyKey:" + historyKey + "并关闭Channel!");
            }
            resMsg = ResMsg.succWithData(flag);
        } catch (Exception e) {
            resMsg = ResMsg.unknowWithMsg(e.toString());
            log.error("historyChannelDelete接口异常:" + e.toString());
        }
        return resMsg;
    }
}
