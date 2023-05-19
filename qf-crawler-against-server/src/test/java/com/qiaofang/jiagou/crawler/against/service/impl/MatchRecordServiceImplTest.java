package com.qiaofang.jiagou.crawler.against.service.impl;

import com.qiaofang.jiagou.crawler.against.BaseTest;
import com.qiaofang.jiagou.crawler.against.entity.MatchRecord;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchRecordStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/21 6:18 下午
 */
public class MatchRecordServiceImplTest extends BaseTest {
    @Autowired
    private IMatchRecordService matchRecordService;


    @Test
    public void testRelieveForbidden() {
        matchRecordService.relieveForbidden(24L, "");
        MatchRecord record = matchRecordService.getById(24L);
        assertEquals((int) record.getStatus(), MatchRecordStatusEnum.DONE.getValue());
    }
}