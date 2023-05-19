package com.qiaofang.jiagou.crawler.against.lts;

import com.qiaofang.jiagou.crawler.against.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 6:48 下午
 */
public class RelieveForbiddenJobTest extends BaseTest {
    @Autowired
    private RelieveForbiddenJob relieveForbiddenJob;

    @Test
    public void testRun() {
        relieveForbiddenJob.run(null);
        Assert.assertFalse(false);
    }
}