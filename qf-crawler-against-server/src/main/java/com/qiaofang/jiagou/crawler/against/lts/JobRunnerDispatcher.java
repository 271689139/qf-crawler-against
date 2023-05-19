package com.qiaofang.jiagou.crawler.against.lts;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.spring.boot.annotation.JobRunner4TaskTracker;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import com.qiaofang.common.constant.CommonConstant;
import com.qiaofang.jiagou.crawler.against.enums.JobRunnerEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * lts任务执行入口
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019-06-12 15:46
 */
@JobRunner4TaskTracker
public class JobRunnerDispatcher implements JobRunner {

    @Autowired
    private RelieveForbiddenJob relieveForbiddenJob;
    @Autowired
    private RuleEndTimeHandleJob ruleEndTimeHandleJob;

    @Override
    public Result run(JobContext jobContext) throws Throwable {

        Job job = jobContext.getJob();
        String type = job.getParam(CommonConstant.LTS_TYPE_NAME);
        JobRunnerEnum jobRunnerEnum = JobRunnerEnum.getByCode(type);
        if (jobRunnerEnum == null) {
            return new Result(Action.EXECUTE_FAILED, "job不存在，请检查参数");
        }
        JobRunner jobRunner;
        switch (jobRunnerEnum) {
            case RELIEVE_FORBIDDEN_JOB:
                jobRunner = relieveForbiddenJob;
                break;
            case RULE_END_TIME_HANDLE_JOB:
                jobRunner = ruleEndTimeHandleJob;
                break;
            default:
                return new Result(Action.EXECUTE_FAILED, type + "未实现");
        }
        return jobRunner != null ? jobRunner.run(jobContext) : null;
    }
}
