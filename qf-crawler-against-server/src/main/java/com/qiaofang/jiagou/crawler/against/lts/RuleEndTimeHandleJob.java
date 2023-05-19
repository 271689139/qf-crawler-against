package com.qiaofang.jiagou.crawler.against.lts;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import com.qiaofang.jiagou.crawler.against.entity.RuleInfo;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 规则截止时间处理job
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/7/14 5:09 下午
 */
@Component
@Slf4j
public class RuleEndTimeHandleJob implements JobRunner {

    @Autowired
    private IRuleInfoService ruleInfoService;

    @Override
    public Result run(JobContext jobContext) {
        log.info("RuleEndTimeHandleJob start ...");
        QueryWrapper<RuleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("enable", 1);
        wrapper.le("end_time", new Date());
        wrapper.last("limit 500");
        List<RuleInfo> needDisableRuleInfoList = ruleInfoService.list(wrapper);
        if (CollectionUtils.isEmpty(needDisableRuleInfoList)) {
            log.info("当前没有到期的规则记录");
            return new Result(Action.EXECUTE_SUCCESS, this.getClass() + " success");
        }
        needDisableRuleInfoList.forEach(rule -> ruleInfoService.enableOrDisable(rule.getId(), Boolean.FALSE));
        log.info("RuleEndTimeHandleJob end ...");
        return new Result(Action.EXECUTE_SUCCESS, this.getClass() + " success");
    }


}