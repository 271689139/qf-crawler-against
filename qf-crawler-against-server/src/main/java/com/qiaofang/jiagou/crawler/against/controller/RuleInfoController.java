package com.qiaofang.jiagou.crawler.against.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.common.request.PageRequest;
import com.qiaofang.common.response.DataResultResponse;
import com.qiaofang.jiagou.crawler.against.constant.RightCodeConstant;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.operatelog.annotation.OperateLog;
import com.qiaofang.jiagou.crawler.against.param.RuleCreateOrUpdateParam;
import com.qiaofang.jiagou.crawler.against.param.RuleListParam;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.LogicalSymbolEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchConditionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import com.qiaofang.jiagou.innersso.annotation.RightValid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则接口
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/21 2:48 下午
 */
@RestController
@RequestMapping("/api/rule")
@Slf4j
public class RuleInfoController extends BaseController {

    @Autowired
    private IRuleInfoService ruleInfoService;

    /**
     * 新增、编辑规则
     *
     * @param param
     * @return
     */
    @PostMapping("createOrUpdate")
    @RightValid(code = RightCodeConstant.RULE_WRITE)
    @OperateLog(type = "规则新增或编辑")
    public DataResultResponse<String> createOrUpdate(@RequestBody @Valid RuleCreateOrUpdateParam param) {
        //检查参数
        checkRuleCreateOrUpdateParam(param);
        //新增、编辑
        RuleInfoDTO dto = new RuleInfoDTO();
        BeanUtils.copyProperties(param, dto);
        //这里有事务，发消息放到外面
        ruleInfoService.createOrUpdate(dto);
        //如果变更的事精准控制，则需要通知网关重新拉取精准匹配规则
        if (RuleTypeEnum.ACCURATE.equals(dto.getRuleType())) {
            ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
        }
        return DataResultResponse.ok("success");
    }

    /**
     * 参数检查
     *
     * @param param
     */
    private void checkRuleCreateOrUpdateParam(RuleCreateOrUpdateParam param) {
        List<RuleMatchConfigDTO> configList = param.getRuleMatchConfigDTOList();
        if (StringUtils.isNotBlank(param.getMatchUrlList())) {
            Arrays.asList(param.getMatchUrlList().split(",")).forEach(url -> {
                if (!url.startsWith("/api/")) {
                    throw new IllegalArgumentException("匹配URL必须是/api开头");
                }
            });
        }
        if (StringUtils.isNotBlank(param.getExcludeUrlList())) {
            Arrays.asList(param.getExcludeUrlList().split(",")).forEach(url -> {
                if (!url.startsWith("/api/")) {
                    throw new IllegalArgumentException("排除的URL必须是/api开头");
                }
            });
        }
        if (RuleTypeEnum.DYNAMIC.equals(param.getRuleType())) {
            if (param.getDetectionDuration() == null || param.getDetectionDuration() < 1) {
                throw new IllegalArgumentException("检测频率不能为空且最少为1分钟");
            }
            if (param.getLimitRequestTimes() == null || param.getLimitRequestTimes() < 1) {
                throw new IllegalArgumentException("限制次数不能为空且至少为1次");
            }
            if (MatchActionEnum.FORBIDDEN.equals(param.getMatchAction()) && (param.getForbiddenDuration() == null || param.getForbiddenDuration() < 1)) {
                throw new IllegalArgumentException("封禁时长不能为空且最少为1分钟");
            }
        }
        List<MatchConditionEnum> dynamicMatchConditionList = Arrays.asList(MatchConditionEnum.IP, MatchConditionEnum.HEADER, MatchConditionEnum.PATH, MatchConditionEnum.COMPANY_UUID, MatchConditionEnum.USER_ID, MatchConditionEnum.USER_UUID);
        List<String> tempMarkList = Lists.newArrayList();
        for (int i = 0; i < configList.size(); i++) {
            RuleMatchConfigDTO dto = configList.get(i);
            String mark = dto.getMatchCondition().getValue().concat(dto.getMatchConditionDetail() == null ? "" : "_".concat(dto.getMatchConditionDetail()));
            if (tempMarkList.contains(mark)) {
                throw new IllegalArgumentException(String.format("匹配条件:%s重复", mark));
            }
            tempMarkList.add(mark);
            Assert.notNull(dto.getMatchCondition(), String.format("第%s条:匹配条件为空", i + 1));
            if (MatchConditionEnum.HEADER.equals(dto.getMatchCondition())) {
                Assert.notNull(dto.getMatchConditionDetail(), String.format("第%s条:匹配条件不完整", i + 1));
            }
            if (RuleTypeEnum.ACCURATE.equals(param.getRuleType())) {
                Assert.notNull(dto.getLogicalSymbol(), String.format("第%s条:逻辑符号为空", i + 1));
                Assert.hasText(dto.getMatchContent(), String.format("第%s条:匹配内容为空", i + 1));
                if (!dto.getMatchCondition().getLogicalSymbolList().contains(dto.getLogicalSymbol())) {
                    throw new IllegalArgumentException(String.format("第%s条:匹配条件%s对应逻辑符号只能选择%s", i + 1, dto.getMatchCondition(), dto.getMatchCondition().getLogicalSymbolList().stream().map(LogicalSymbolEnum::getDesc).collect(Collectors.toList())));
                }
            } else if (!dynamicMatchConditionList.contains(dto.getMatchCondition())) {
                throw new IllegalArgumentException(String.format("第%s条:动态匹配条件只能选择%s", i + 1, dynamicMatchConditionList.stream().map(MatchConditionEnum::getValue).collect(Collectors.toList())));
            }
        }
    }

    /**
     * 详细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    @RightValid(code = RightCodeConstant.RULE_QUERY)
    public DataResultResponse<RuleInfoDTO> detail(@RequestBody @PathVariable @NotNull Long id) {
        return DataResultResponse.ok(ruleInfoService.detail(id));
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("delete/{id}")
    @RightValid(code = RightCodeConstant.RULE_WRITE)
    @OperateLog(type = "删除规则")
    public DataResultResponse<String> delete(@PathVariable @NotNull Long id) {
        ruleInfoService.delete(id);
        return DataResultResponse.ok("success");
    }

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @PostMapping("list")
    @RightValid(code = RightCodeConstant.RULE_QUERY)
    public DataResultResponse<IPage<RuleInfoDTO>> list(@RequestBody @Valid PageRequest<RuleListParam> request) {
        PageDTO pageDTO = request.getPage();
        if (pageDTO == null) {
            pageDTO = new PageDTO(1, 20);
        }
        return DataResultResponse.ok(ruleInfoService.page(request.getParam(), pageDTO));
    }

    /**
     * 启用
     *
     * @param id
     * @return
     */
    @PostMapping("enable/{id}")
    @RightValid(code = RightCodeConstant.RULE_WRITE)
    @OperateLog(type = "启用规则")
    public DataResultResponse<String> enable(@PathVariable @NotNull Long id) {
        ruleInfoService.enableOrDisable(id, Boolean.TRUE);
        ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
        return DataResultResponse.ok("success");
    }

    /**
     * 停用
     *
     * @param id
     * @return
     */
    @PostMapping("disable/{id}")
    @RightValid(code = RightCodeConstant.RULE_WRITE)
    @OperateLog(type = "停用规则")
    public DataResultResponse<String> disable(@PathVariable @NotNull Long id) {
        ruleInfoService.enableOrDisable(id, Boolean.FALSE);
        ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
        return DataResultResponse.ok("success");
    }

    /**
     * 刷新
     *
     * @return
     */
    @PostMapping("refresh")
    @OperateLog(type = "刷新规则")
    public DataResultResponse<String> refresh() {
        ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
        return DataResultResponse.ok("success");
    }
}
