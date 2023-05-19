package com.qiaofang.jiagou.crawler.against.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.entity.RuleInfo;
import com.qiaofang.jiagou.crawler.against.param.RuleListParam;
import com.qiaofang.jiagou.crawler.against.stub.dto.CrawlerAgainstMessageDTO;

import java.util.List;

/**
 * <p>
 * 反爬规则表 服务类
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
public interface IRuleInfoService extends IService<RuleInfo> {


    /**
     * 新增、编辑反爬规则
     *
     * @param ruleInfoDTO
     */
    void createOrUpdate(RuleInfoDTO ruleInfoDTO);

    /**
     * 启用停用
     *
     * @param id
     * @param enable
     */
    void enableOrDisable(Long id, Boolean enable);

    /**
     * 获取所有规则
     *
     * @return
     */
    List<RuleInfoDTO> getAllEnableRule();

    /**
     * 分页查询规则
     *
     * @param param
     * @param pageDTO
     * @return
     */
    IPage<RuleInfoDTO> page(RuleListParam param, PageDTO pageDTO);

    /**
     * 规则详情
     *
     * @param id
     * @return
     */
    RuleInfoDTO detail(Long id);

    /**
     * 删除
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 获取精准匹配规则
     *
     * @return
     */
    List<CrawlerAgainstMessageDTO> fetchAccurateRuleConfig();

    /**
     * 发送精准匹配规则更新消息
     */
    void sendAccurateRuleConfigUpdateKafkaMessage();
}
