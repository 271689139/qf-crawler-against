package com.qiaofang.jiagou.crawler.against.param;

import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/27 2:56 下午
 */
@Data
public class MatchRecordListParam implements Serializable {

    /**
     * 匹配动作FORBIDDEN-封禁 WARNING-报警 VERIFICATION-需要验证
     */
    private MatchActionEnum matchAction;

    /**
     * 匹配计数维度标识
     */
    private String matchDimensionMark;

    /**
     * 匹配计数维度key
     */
    private String matchDimensionKey;

    /**
     * 状态 0-进行中 1-已完成
     */
    private Integer status;

    /**
     * 封禁开始时间的范围
     */
    private String startTimeStart;
    private String startTimeEnd;

}
