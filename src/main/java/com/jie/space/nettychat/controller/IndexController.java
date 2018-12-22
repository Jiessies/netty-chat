package com.jie.space.nettychat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class IndexController {

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

}
