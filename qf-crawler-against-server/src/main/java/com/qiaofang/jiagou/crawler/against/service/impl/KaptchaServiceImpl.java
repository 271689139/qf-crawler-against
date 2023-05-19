package com.qiaofang.jiagou.crawler.against.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.kaptcha.exception.KaptchaRenderException;
import com.baomidou.kaptcha.spring.boot.KaptchaProperties;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.qiaofang.common.exception.BusinessException;
import com.qiaofang.common.util.DateUtil;
import com.qiaofang.jiagou.crawler.against.constant.RedisKeyConstant;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.entity.MatchRecord;
import com.qiaofang.jiagou.crawler.against.enums.ResponseCodeEnum;
import com.qiaofang.jiagou.crawler.against.enums.RuleOriginEnum;
import com.qiaofang.jiagou.crawler.against.service.IKaptchaService;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchConditionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchRecordStatusEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import com.qiaofang.jiagou.crawler.against.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/7/6 3:15 下午
 */
@Service
@Slf4j
public class KaptchaServiceImpl implements IKaptchaService {

    @Autowired
    private DefaultKaptcha kaptcha;
    @Autowired
    private IMatchRecordService matchRecordService;
    @Autowired
    private IRuleInfoService ruleInfoService;
    @Resource
    private KaptchaProperties kaptchaProperties;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public String generateToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(RedisKeyConstant.CAPTCHA_TOKEN + token, "0", 5, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void render(String token, String width, String height, Integer validSecond, HttpServletRequest request, HttpServletResponse response) {
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate");
        response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setContentType("image/jpeg");
        String code = redisUtil.get(RedisKeyConstant.CAPTCHA_TOKEN + token, String.class);
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("token无效或已过期");
        }
        code = kaptcha.createText();
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(kaptcha.createImage(code), "jpg", out);
            //默认5分钟有效
            validSecond = validSecond == null || validSecond <= 0 ? 300 : validSecond;
            validSecond = validSecond > 3600 * 4 ? 3600 * 4 : validSecond;
            redisUtil.set(RedisKeyConstant.CAPTCHA + token, code, validSecond, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new KaptchaRenderException(e);
        }
    }

    @Override
    public void valid(String token, String code, String matchDimensionKey) {
        try {
            String rightCode = redisUtil.get(RedisKeyConstant.CAPTCHA + token, String.class);
            if (StringUtils.isBlank(rightCode)) {
                throw new BusinessException(ResponseCodeEnum.CAPTCHA_NOT_FOUND.getCode(), ResponseCodeEnum.CAPTCHA_NOT_FOUND.getMsg());
            }
            if (!rightCode.equalsIgnoreCase(code)) {
                throw new BusinessException(ResponseCodeEnum.CAPTCHA_WRONG.getCode(), ResponseCodeEnum.CAPTCHA_WRONG.getMsg());
            }
        } finally {
            redisUtil.delete(RedisKeyConstant.CAPTCHA + token);
        }
        QueryWrapper<MatchRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("match_dimension_key", matchDimensionKey);
        wrapper.eq("status", MatchRecordStatusEnum.PROCESS.getValue());
        List<MatchRecord> matchRecordList = matchRecordService.list(wrapper);
        if (CollectionUtils.isNotEmpty(matchRecordList)) {
            matchRecordService.relieveForbidden(matchRecordList.get(0).getId(), "用户输入验证码解封");
            //通过验证码解封之后的一些操作，目前是对当前用户添加指定时长的白名单
            afterRelieveFromCaptcha(matchRecordList.get(0));
        } else {
            log.warn("验证码校验成功，但是没有对应的封禁记录,matchDimensionKey:{}", matchDimensionKey);
        }
    }

    /**
     * 通过验证码解封后的操作
     *
     * @param matchRecord
     */
    private void afterRelieveFromCaptcha(MatchRecord matchRecord) {
        try {
            RuleInfoDTO tempRule = new RuleInfoDTO();
            tempRule.setRuleType(RuleTypeEnum.ACCURATE);
            tempRule.setRuleName("验证码验证成功后添加临时白名单");
            tempRule.setOrigin(RuleOriginEnum.SYSTEM_TEMP.getValue());
            //5分钟的白名单
            tempRule.setEndTime(DateUtil.addDate(new Date(), Calendar.MINUTE, 5));
            tempRule.setMatchAction(MatchActionEnum.PASS);
            tempRule.setSortNo(Integer.MIN_VALUE);
            List<RuleMatchConfigDTO> ruleMatchConfigDTOList = JSON.parseArray(matchRecord.getMatchConfigSnapshot(), RuleMatchConfigDTO.class);
            List<RuleMatchConfigDTO> tempRuleMatchConfigDTOList = ruleMatchConfigDTOList.stream()
                    .filter(dto -> dto.getMatchCondition().equals(MatchConditionEnum.COMPANY_UUID) || dto.getMatchCondition().equals(MatchConditionEnum.USER_UUID))
                    .collect(Collectors.toList());
            if (tempRuleMatchConfigDTOList.size() != 2) {
                log.info("当前解封的维度不包含公司uuid跟userId，不进行临时白名单添加");
                return;
            }
            tempRule.setRuleMatchConfigDTOList(tempRuleMatchConfigDTOList);
            ruleInfoService.createOrUpdate(tempRule);
            ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
        } catch (Exception e) {
            log.error("afterRelieveFromCaptcha error, matchRecordId:{}", matchRecord.getId(), e);
        }
    }

}
