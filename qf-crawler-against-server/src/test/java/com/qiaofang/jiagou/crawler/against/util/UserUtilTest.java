package com.qiaofang.jiagou.crawler.against.util;

import com.qiaofang.jiagou.crawler.against.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/6/22 2:10 下午
 */
public class UserUtilTest extends BaseTest {
    @Autowired
    private UserUtil userUtil;


    @Test
    public void testBatchGetMobile() {
        Map<String, String> map = userUtil.batchGetMobile(Arrays.asList("15502163729485074", "0966515535861952"));
        System.out.println(map);
        Assert.assertFalse(map == null || map.isEmpty());
    }
}