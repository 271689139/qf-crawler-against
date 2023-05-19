package com.qiaofang.jiagou.crawler.against.lts;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import com.qiaofang.jiagou.crawler.against.entity.MatchRecord;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchRecordStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * 解封job
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019-07-01 17:14
 */
@Component
@Slf4j
public class RelieveForbiddenJob implements JobRunner {

    @Autowired
    private IMatchRecordService matchRecordService;

    @Override
    public Result run(JobContext jobContext) {
        log.info("RelieveForbiddenJob start ...");
        QueryWrapper<MatchRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("status", MatchRecordStatusEnum.PROCESS.getValue());
        wrapper.in("match_action", MatchActionEnum.FORBIDDEN.getValue(), MatchActionEnum.VERIFICATION.getValue());
        wrapper.le("end_time", new Date());
        wrapper.last("limit 500");
        List<MatchRecord> recordList = matchRecordService.list(wrapper);
        if (CollectionUtils.isEmpty(recordList)) {
            log.info("当前没有需要处理的封禁记录");
            return new Result(Action.EXECUTE_SUCCESS, this.getClass() + " success");
        }
        recordList.forEach(record -> matchRecordService.relieveForbidden(record.getId(), "封禁时间到期解封"));
        log.info("RelieveForbiddenJob end ...");
        return new Result(Action.EXECUTE_SUCCESS, this.getClass() + " success");
    }


}
