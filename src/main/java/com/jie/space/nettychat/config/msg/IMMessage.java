package com.jie.space.nettychat.config.msg;

import lombok.Data;

import java.io.Serializable;

@Data
public class IMMessage implements Serializable {

    //id
    private Integer id;

    //appId
    private String appId;

    //会话ID
    private Integer conversationId;

    //发送者ID
    private String senderId;

    //接受者ID
    private String receiverId;

    //用户类型（1 发送方为销售 2 发送方为粉丝）
    private Integer userType;

    //发送内容
    private String chatContent;

    //话术状态（1未读、2已读、3删除）
    private Integer chatStatus;

    //内容类型（1文字、2表情、3文件）
    private Integer contentType;

    //会话类型（1 单对单）
    private Integer type;

    //群组ID
    private Integer groupId;

    //状态 （1 在线 2 离线）
    private Integer status;

    //suiteId（接收者不在线时，推送系统消息时用的ID）
    private String suiteId;

    private Long createTime;

    private String messageId;

}
