package com.jie.space.nettychat.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestTask {

    @Scheduled(cron = "${timer.test-lock-task.cron}")
    public void testLock(){

        //测试分布式定时任务，任务分流处理解决方案
    }
}
