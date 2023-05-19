package com.qiaofang.jiagou.crawler.against.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author shihao.liu
 */
@RestController
public class IndexController {

    @GetMapping(value = {"/", "/info", "/info2", "/info3"})
    @ResponseBody
    public String index() {
        return "hello crawler against";
    }

}
