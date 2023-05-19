package com.qiaofang.jiagou.crawler.against.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 5:04 下午
 */
@Data
public class AlertSendWorkMessageParam {

    private String title;
    private String text;
    @JSONField(name = "useridList")
    @JsonFormat(pattern = "useridList")
    private String useridList;
}
