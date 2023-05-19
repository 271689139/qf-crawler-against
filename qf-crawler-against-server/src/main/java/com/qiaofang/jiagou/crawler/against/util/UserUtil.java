package com.qiaofang.jiagou.crawler.against.util;


import com.alibaba.fastjson.JSON;
import com.qiaofang.common.constant.CommonResponseCode;
import com.qiaofang.common.exception.BusinessException;
import com.qiaofang.common.response.DataResultResponse;
import com.qiaofang.jiagou.crawler.against.dto.UserInfoDTO;
import com.qiaofang.jiagou.innersso.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/28 2:59 下午
 */
@Slf4j
public class UserUtil {

    private final static String SEARCH_USER_URL = "spi/user/searchUser?keyword=%s";

    private final static String BATCH_GET_MOBILE_URL = "spi/user/batchGetUserMobile?userIdList=%s&operateUserId=crawleragainst";

    private final RestTemplate restTemplate = new RestTemplate();

    private final String rightServerUrl;

    public UserUtil(String rightServerUrl) {
        Assert.hasText(rightServerUrl, "rightServerUrl is null");
        this.rightServerUrl = rightServerUrl;
    }

    /**
     * 搜用户
     *
     * @param keyWords
     * @return
     */
    public List<UserInfoDTO> searchUser(String keyWords) {
        String url = CommonUtils.addTrailingSlash(rightServerUrl) + String.format(SEARCH_USER_URL, keyWords);
        DataResultResponse<Object> response = restTemplate.getForObject(url, DataResultResponse.class);
        String responseStr = JSON.toJSONString(response);
        log.info("url:{}, response:{}", url, responseStr);
        if (!CommonResponseCode.RC_SUCCESS.getResponseCode().equals(response.getResponseCode())) {
            throw new BusinessException(String.format("获取员工信息失败,原因:%s", response.getResponseMessage()));
        }
        return JSON.parseArray(JSON.toJSONString(response.getData()), UserInfoDTO.class);
    }

    /**
     * 批量获取用户手机号
     *
     * @param userIdList
     * @return
     */
    public Map<String, String> batchGetMobile(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new HashMap<>();
        }
        String url = CommonUtils.addTrailingSlash(rightServerUrl) + String.format(BATCH_GET_MOBILE_URL, String.join(",", userIdList));
        DataResultResponse<Map<String, String>> response = restTemplate.getForObject(url, DataResultResponse.class);
        String responseStr = JSON.toJSONString(response);
        log.info("url:{}, response:{}", url, responseStr);
        if (!CommonResponseCode.RC_SUCCESS.getResponseCode().equals(response.getResponseCode())) {
            throw new BusinessException(String.format("获取员工信息失败,原因:%s", response.getResponseMessage()));
        }
        return response.getData();
    }
}
