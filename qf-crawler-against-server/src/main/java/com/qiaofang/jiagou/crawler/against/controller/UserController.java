package com.qiaofang.jiagou.crawler.against.controller;

import com.qiaofang.common.response.DataResultResponse;
import com.qiaofang.jiagou.crawler.against.dto.UserInfoDTO;
import com.qiaofang.jiagou.crawler.against.util.UserUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户
 *
 * @author shihao.liu
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserUtil userUtil;

    @GetMapping("/search")
    public DataResultResponse<List<UserInfoDTO>> searchUser(String keyword) {
        List<UserInfoDTO> list = userUtil.searchUser(keyword);
        int size = 7;
        if (list.size() > size) {
            list = list.subList(0, 7);
        }
        return DataResultResponse.ok(list);
    }


}
