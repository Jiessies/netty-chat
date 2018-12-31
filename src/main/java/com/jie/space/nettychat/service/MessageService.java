package com.jie.space.nettychat.service;

import com.jie.space.nettychat.config.msg.ResMsg;

public interface MessageService {
    ResMsg historyChannelDelete(String openId);
}
