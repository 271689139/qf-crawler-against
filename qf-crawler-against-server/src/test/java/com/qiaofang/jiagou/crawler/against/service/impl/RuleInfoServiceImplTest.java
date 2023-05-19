package com.qiaofang.jiagou.crawler.against.service.impl;

import com.alibaba.fastjson.JSON;
import com.qiaofang.jiagou.crawler.against.BaseTest;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchConditionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/13 4:38 下午
 */
public class RuleInfoServiceImplTest extends BaseTest {

    @Autowired
    private IRuleInfoService ruleInfoService;

    @Test
    public void testAddRule() {
        RuleInfoDTO ruleInfoDTO = new RuleInfoDTO();
        ruleInfoDTO.setRuleName("动态规则需要验证码");
        ruleInfoDTO.setRuleType(RuleTypeEnum.DYNAMIC);
        ruleInfoDTO.setMatchAction(MatchActionEnum.VERIFICATION);
        ruleInfoDTO.setDetectionDuration(1);
        ruleInfoDTO.setLimitRequestTimes(10);
        ruleInfoDTO.setForbiddenDuration(5);

        List<RuleMatchConfigDTO> ruleMatchConfigDTOList = Lists.newArrayList();
        RuleMatchConfigDTO matchConfigDTO = new RuleMatchConfigDTO();
        matchConfigDTO.setMatchCondition(MatchConditionEnum.IP);
//        matchConfigDTO.setLogicalSymbol(LogicalSymbolEnum.MATCH);
//        matchConfigDTO.setMatchContent("192.168.*");
        ruleMatchConfigDTOList.add(matchConfigDTO);

        RuleMatchConfigDTO matchConfigDTO2 = new RuleMatchConfigDTO();
        matchConfigDTO2.setMatchCondition(MatchConditionEnum.HEADER);
        matchConfigDTO2.setMatchConditionDetail("companyUuid");
//        matchConfigDTO2.setLogicalSymbol(LogicalSymbolEnum.CONTAIN);
//        matchConfigDTO2.setMatchContent("123456");
        ruleMatchConfigDTOList.add(matchConfigDTO2);
        ruleInfoDTO.setRuleMatchConfigDTOList(ruleMatchConfigDTOList);
        ruleInfoService.createOrUpdate(ruleInfoDTO);
        Assert.assertFalse(false);
    }

    @Test
    public void testGetAllRule() {
        List<RuleInfoDTO> allRule = ruleInfoService.getAllEnableRule();
        System.out.println(JSON.toJSONString(allRule));
        Assert.assertFalse(CollectionUtils.isEmpty(allRule));
    }
}