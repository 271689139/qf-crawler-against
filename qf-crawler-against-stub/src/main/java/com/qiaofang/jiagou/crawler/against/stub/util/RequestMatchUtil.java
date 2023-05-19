package com.qiaofang.jiagou.crawler.against.stub.util;

import com.qiaofang.jiagou.crawler.against.stub.dto.HttpRequestLogMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.LogicalSymbolEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 匹配配置判断工具类
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/17 4:37 下午
 */
@Slf4j
public class RequestMatchUtil {

    /**
     * 根据请求信息判断是否匹配配置的规则
     *
     * @param httpRequestLogMessageDTO 请求信息
     * @param ruleMatchConfigDTOList   配置的规则
     * @return
     */
    public static boolean match(HttpRequestLogMessageDTO httpRequestLogMessageDTO, List<RuleMatchConfigDTO> ruleMatchConfigDTOList) {
        try {
            if (ruleMatchConfigDTOList == null || ruleMatchConfigDTOList.isEmpty()){
                return true;
            }
            Map<String, String> headers = supplementUpperCase(httpRequestLogMessageDTO.getHeaders());
            for (RuleMatchConfigDTO configDTO : ruleMatchConfigDTOList) {
                String resourceValue = getResourceValue(httpRequestLogMessageDTO, headers, configDTO);
                if (!logicalSymbolCalculation(resourceValue, configDTO.getMatchContent(), configDTO.getLogicalSymbol())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("match error", e);
            return false;
        }
    }

    /**
     * 根据配置项对应的资源value
     * @param httpRequestLogMessageDTO
     * @param headers
     * @param configDTO
     * @return
     */
    private static String getResourceValue(HttpRequestLogMessageDTO httpRequestLogMessageDTO, Map<String, String> headers, RuleMatchConfigDTO configDTO) {
        String resource = null;
        switch (configDTO.getMatchCondition()) {
            case IP:
                resource = httpRequestLogMessageDTO.getOriginIp();
                break;
            case HEADER:
                resource = headers.get(configDTO.getMatchConditionDetail());
                if (resource == null) {
                    resource = headers.get(configDTO.getMatchConditionDetail().toUpperCase());
                }
                break;
            case PATH:
                resource = httpRequestLogMessageDTO.getPath();
                break;
            case PARAM:
                if (httpRequestLogMessageDTO.getParams() != null) {
                    resource = httpRequestLogMessageDTO.getParams().get(configDTO.getMatchConditionDetail());
                }
                break;
            case HTTP_METHOD:
                resource = httpRequestLogMessageDTO.getMethod();
                if (configDTO.getMatchContent() != null) {
                    configDTO.setMatchContent(configDTO.getMatchContent().toUpperCase());
                }
                break;
            case USER_ID:
                resource = httpRequestLogMessageDTO.getUserId();
                break;
            case USER_UUID:
                resource = httpRequestLogMessageDTO.getUserUuid();
                break;
            case COMPANY_UUID:
                resource = httpRequestLogMessageDTO.getCompanyUuid();
                break;
            default:
                log.error("其他条件暂不支持");
        }
        return resource;
    }

    /**
     * 所有key大写处理后补充到map里 主要用于大小写不敏感处理
     *
     * @param map
     * @return
     */
    private static Map<String, String> supplementUpperCase(Map<String, String> map) {
        Map<String, String> result = new HashMap<>(16);
        if (map == null) {
            return result;
        }
        Map<String, String> upperCaseHeaders = new HashMap<>(16);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() != null && !map.containsKey(entry.getKey().toUpperCase())) {
                upperCaseHeaders.put(entry.getKey().toUpperCase(), entry.getValue());
            }
        }
        result.putAll(map);
        result.putAll(upperCaseHeaders);
        return result;
    }

    /**
     * 逻辑计算
     *
     * @param resource      计算的源值
     * @param standard      标准
     * @param logicalSymbol 符号
     * @return
     */
    private static boolean logicalSymbolCalculation(String resource, String standard, LogicalSymbolEnum logicalSymbol) {
        if (resource == null || standard == null || resource.isEmpty() || standard.isEmpty()) {
            return false;
        }
        switch (logicalSymbol) {
            case MATCH:
                return Pattern.matches(standard, resource);
            case NOT_MATCH:
                return !Pattern.matches(standard, resource);
            case CONTAIN:
                return resource.contains(standard);
            case NOT_CONTAIN:
                return !resource.contains(standard);
            case EQUALS:
                return resource.equals(standard);
            case NOT_EQUALS:
                return !resource.equals(standard);
            case BELONG:
                return Arrays.asList(standard.split(",")).contains(resource);
            case NOT_BELONG:
                return !Arrays.asList(standard.split(",")).contains(resource);
            default:
                return false;
        }
    }

    /**
     * 组装匹配维度标识
     *
     * @param ruleId
     * @param matchConfigDTOList
     * @param httpRequestLogMessageDTO
     * @return
     */
    public static String assembleMatchDimensionMark(Long ruleId, List<RuleMatchConfigDTO> matchConfigDTOList, HttpRequestLogMessageDTO httpRequestLogMessageDTO) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = new TreeMap<>();
        Map<String, String> headers = supplementUpperCase(httpRequestLogMessageDTO.getHeaders());
        for (RuleMatchConfigDTO configDTO : matchConfigDTOList) {
            String key = configDTO.getMatchCondition().getValue();
            String value = null;
            switch (configDTO.getMatchCondition()) {
                case IP:
                    value = httpRequestLogMessageDTO.getOriginIp();
                    break;
                case HEADER:
                    String matchContent = headers.get(configDTO.getMatchConditionDetail());
                    if (matchContent == null || matchContent.isEmpty()) {
                        matchContent = headers.get(configDTO.getMatchConditionDetail().toUpperCase());
                    }
                    key = key.concat("_").concat(configDTO.getMatchConditionDetail());
                    value = matchContent;
                    break;
                case PATH:
                    value = httpRequestLogMessageDTO.getPath();
                    break;
                case COMPANY_UUID:
                    value = httpRequestLogMessageDTO.getCompanyUuid();
                    break;
                case USER_ID:
                    value = httpRequestLogMessageDTO.getUserId();
                    break;
                case USER_UUID:
                    value = httpRequestLogMessageDTO.getUserUuid();
                    break;
                default:
                    log.error("其他条件暂不支持");
                    break;
            }
            map.put(key, value);
            configDTO.setMatchContent(value);
            configDTO.setLogicalSymbol(LogicalSymbolEnum.EQUALS);
        }
        if (map.isEmpty()) {
            log.error("规则配置非法，无法组装维度标识,ruleId:{}", ruleId);
            return null;
        }
        builder.append("ruleId").append("=").append(ruleId).append("&");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                log.info("请求信息中key:{}对应的value为空", entry.getKey());
                return null;
            }
            builder.append(entry.getKey()).append("=").append(value).append("&");
        }
        return builder.substring(0, builder.length() - 1);
    }
}
