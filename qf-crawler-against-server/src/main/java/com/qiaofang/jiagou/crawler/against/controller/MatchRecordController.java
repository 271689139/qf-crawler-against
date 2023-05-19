package com.qiaofang.jiagou.crawler.against.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.common.request.PageRequest;
import com.qiaofang.common.response.DataResultResponse;
import com.qiaofang.jiagou.crawler.against.constant.RightCodeConstant;
import com.qiaofang.jiagou.crawler.against.operatelog.annotation.OperateLog;
import com.qiaofang.jiagou.crawler.against.param.MatchRecordListParam;
import com.qiaofang.jiagou.crawler.against.param.RelieveForbiddenParam;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.stub.dto.MatchRecordDTO;
import com.qiaofang.jiagou.innersso.annotation.RightValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 匹配记录相关处理接口
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/21 5:57 下午
 */
@RestController
@RequestMapping("/api/matchRecord")
public class MatchRecordController extends BaseController {

    @Autowired
    private IMatchRecordService matchRecordService;

    /**
     * 解封
     *
     * @param param
     * @return
     */
    @PostMapping("relieveForbidden")
    //@RightValid(code = RightCodeConstant.MATCH_RECORD_WRITE)
    @OperateLog
    public DataResultResponse<String> relieveForbidden(@RequestBody @Valid RelieveForbiddenParam param) {
        matchRecordService.relieveForbidden(param.getId(), param.getRemark());
        return DataResultResponse.ok("success");
    }

    /**
     * 分页获取记录
     *
     * @param request
     * @return
     */
    @PostMapping("list")
    @RightValid(code = RightCodeConstant.MATCH_RECORD_QUERY)
    public DataResultResponse<IPage<MatchRecordDTO>> page(@RequestBody @Valid PageRequest<MatchRecordListParam> request) {
        PageDTO pageDTO = request.getPage();
        if (pageDTO == null) {
            pageDTO = new PageDTO(1, 20);
        }
        return DataResultResponse.ok(matchRecordService.pageRecord(pageDTO, request.getParam()));
    }


}
