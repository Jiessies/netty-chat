package com.jie.space.nettychat.controller;

import com.alibaba.fastjson.JSON;
import com.jie.space.nettychat.config.msg.ResMsg;
import com.jie.space.nettychat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/")
    @ResponseBody
    public String hello() {
        return "hi huangmingjie";
    }

    @RequestMapping("/index")
    public String index() {
        return "/index";
    }

    @RequestMapping("/index2")
    public String index2() {
        return "/index2";
    }

    /**
     * 删除历史认证信息（接口内部调用）
     * @param openId
     * @return
     */
    @PostMapping(value = "/historyChannelDelete")
    public ResMsg historyChannelDelete(@RequestParam("openId") String openId){
        return messageService.historyChannelDelete(openId);
    }

    @GetMapping(value = "/put")
    @ResponseBody
    public String redisSet(@RequestParam(value = "key") String key){
        redisTemplate.setEnableTransactionSupport(true);
        int i = 0;
        try {
            i = (int) (Math.random()*100);
            redisTemplate.multi();
            redisTemplate.opsForValue().set("key"+i, "Value"+i, 300, TimeUnit.SECONDS);
            if(key.equals("11111")){
                throw new RuntimeException("我报错了！");
            }
            redisTemplate.exec();
        } catch (RuntimeException e) {
            e.printStackTrace();
            redisTemplate.discard();
        }
        if(key.equals("22222")){
            throw new RuntimeException("我报错了！");
        }
        return "successkey" + i;
    }

    @GetMapping(value = "/set")
    @ResponseBody
    public String setRedis(@RequestParam(value = "key") String key,
                           @RequestParam(value = "value") String value){
        redisTemplate.setEnableTransactionSupport(true);
        try {
            redisTemplate.multi();
            redisTemplate.opsForValue().set(key, value, 300, TimeUnit.SECONDS);
            if(key.equals("11111")){
                throw new RuntimeException("我报错了11111！");
            }
            redisTemplate.exec();
        } catch (RuntimeException e) {
            e.printStackTrace();
            redisTemplate.discard();
        }
        if(key.equals("22222")){
            throw new RuntimeException("我报错了22222！");
        }
        return key;
    }


    @GetMapping(value = "/set111")
    @ResponseBody
    public String set111(@RequestParam(value = "key") String key,
                           @RequestParam(value = "value") String value){
        boolean flag = this.setSynch(key, value);
        if(flag){
            return key;
        }else {
            return "fail";
        }
    }

    private boolean setSynch(String key, String value){
        redisTemplate.setEnableTransactionSupport(true);
        try {
            redisTemplate.multi();

            redisTemplate.opsForValue().set(key, value);
            if(key.equals("11111")){
                throw new RuntimeException("我报错了11111！");
            }

            redisTemplate.exec();
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            redisTemplate.discard();
            return false;
        }
    }

    @GetMapping(value = "/delete")
    @ResponseBody
    public String deleteKey(@RequestParam(value = "key") String key){
        boolean flag = redisTemplate.delete(key);
        return "success " + flag;
    }

    @GetMapping(value = "/get")
    @ResponseBody
    public String getKey(@RequestParam(value = "key") String key){
        Object object = redisTemplate.opsForValue().get(key);
        return JSON.toJSONString(object);
    }

    @GetMapping(value = "/keys")
    @ResponseBody
    public String keys(@RequestParam(value = "key") String key){
        Set<String> stringSet = redisTemplate.keys(key + "*");
        return JSON.toJSONString(stringSet);
    }
}
