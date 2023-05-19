package com.qiaofang.jiagou.crawler.against.util;

import com.google.common.collect.Lists;
import com.qiaofang.core.cacheclient.CoredisTemplate;
import com.qiaofang.jiagou.crawler.against.BaseTest;
import com.qiaofang.jiagou.crawler.against.entity.RuleInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/6/22 2:10 下午
 */
public class RedisUtilTest extends BaseTest {
    @Autowired
    private CoredisTemplate<String, Object> coredisTemplate;

    @Autowired
    private RedisUtil redisUtil;


    @Test
    public void testName() {
        List<RuleInfo> list = Lists.newArrayList();
        RuleInfo rule = new RuleInfo();
        rule.setId(1L);
        rule.setRuleName("testRule");
        rule.setCreateTime(new Date());
        RuleInfo rule2 = new RuleInfo();
        rule.setId(2L);
        rule.setRuleName("testRule2");
        rule.setCreateTime(new Date());

        list.add(rule);
        list.add(rule2);
        redisUtil.set("redis.test", list, 1, TimeUnit.HOURS);
        List<RuleInfo> ruleInfoList = redisUtil.getList("redis.test", RuleInfo.class);
        Assert.assertFalse(ruleInfoList == null || ruleInfoList.isEmpty());
    }

    @Test
    public void testSetAndGet() {
        RuleInfo rule = new RuleInfo();
        rule.setId(1L);
        rule.setRuleName("testRule");
        rule.setCreateTime(new Date());
        coredisTemplate.opsForValue().set("redis.test", rule);

        Class<RuleInfo> clazz = RuleInfo.class;
        RuleInfo ruleInfo = clazz.cast(coredisTemplate.opsForValue().get("redis.test"));
        Assert.assertNotNull(ruleInfo);
    }
}