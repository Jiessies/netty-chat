package com.jie.space.nettychat.config.msg;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxIMMessage implements Serializable {
    //消息类型（1认证， 2认证完成， 3用户离开， 4发送CHAT消息，5发送CHAT消息完成（接收方在线），6发送CHAT消息完成（接收方不在线），7 更新已读消息 ）
    private Integer messageType;

    private String channelId;
    //消息体
    private IMMessage imMessage;
}
