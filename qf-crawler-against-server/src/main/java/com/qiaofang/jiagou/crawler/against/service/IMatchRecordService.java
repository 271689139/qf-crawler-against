package com.qiaofang.jiagou.crawler.against.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.jiagou.crawler.against.param.MatchRecordListParam;
import com.qiaofang.jiagou.crawler.against.stub.dto.MatchRecordDTO;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.entity.MatchRecord;
import com.qiaofang.jiagou.crawler.against.stub.dto.HttpRequestLogMessageDTO;

/**
 * <p>
 * 匹配规则处理记录表 服务类
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
public interface IMatchRecordService extends IService<MatchRecord> {

    /**
     * 新建报错规则匹配后的处理记录，并做对应处理
     *
     * @param ruleInfoDTO
     * @param messageDTO
     * @param tallyDimensionMark 计数维度标识 eg:ruleId=3|IP=192.168.1.1|companyUuid=221234256722
     */
    void createRecord(RuleInfoDTO ruleInfoDTO, HttpRequestLogMessageDTO messageDTO, String tallyDimensionMark);

    /**
     * 解封
     *
     * @param id
     * @param remark
     */
    void relieveForbidden(Long id, String remark);

    /**
     * 分页获取匹配记录
     * @param page
     * @return
     */
    IPage<MatchRecordDTO> pageRecord(PageDTO page, MatchRecordListParam param);
}
