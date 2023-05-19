package com.qiaofang.jiagou.crawler.against.spi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.common.request.PageRequest;
import com.qiaofang.common.response.DataResultResponse;
import com.qiaofang.jiagou.crawler.against.controller.BaseController;
import com.qiaofang.jiagou.crawler.against.param.MatchRecordListParam;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import com.qiaofang.jiagou.crawler.against.stub.dto.CrawlerAgainstMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.MatchRecordDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchRecordStatusEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则接口
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/21 2:48 下午
 * @nodoc
 */
@RestController
@RequestMapping("spi")
public class RuleSpiController extends BaseController {

    @Autowired
    private IRuleInfoService ruleInfoService;
    @Autowired
    private IMatchRecordService matchRecordService;

    /**
     * 拉取精准匹配规则
     *
     * @return
     */
    @GetMapping("fetchAccurateRuleConfig")
    @ResponseBody
    public DataResultResponse<List<CrawlerAgainstMessageDTO>> fetchAccurateRuleConfig() {
        return DataResultResponse.ok(ruleInfoService.fetchAccurateRuleConfig());
    }


    /**
     * 分页拉取动态匹配规则
     *
     * @param request
     * @return
     */
    @PostMapping("fetchDynamicRuleConfigByPage")
    public DataResultResponse<IPage<CrawlerAgainstMessageDTO>> fetchDynamicRuleConfigByPage(@RequestBody @Valid PageRequest<String> request) {
        PageDTO pageDTO = request.getPage();
        if (pageDTO == null) {
            pageDTO = new PageDTO(1, 20);
        }
        MatchRecordListParam param = new MatchRecordListParam();
        param.setStatus(MatchRecordStatusEnum.PROCESS.getValue());
        IPage<MatchRecordDTO> pageData = matchRecordService.pageRecord(pageDTO, param);
        IPage<CrawlerAgainstMessageDTO> result = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        List<CrawlerAgainstMessageDTO> messageDTOList = pageData.getRecords().stream().map(recordDTO -> {
            CrawlerAgainstMessageDTO messageDTO = new CrawlerAgainstMessageDTO();
            BeanUtils.copyProperties(recordDTO, messageDTO);
            messageDTO.setRuleType(RuleTypeEnum.DYNAMIC);
            messageDTO.setMatchRecordId(recordDTO.getId());
            return messageDTO;
        }).collect(Collectors.toList());
        result.setRecords(messageDTOList);
        return DataResultResponse.ok(result);
    }
}
