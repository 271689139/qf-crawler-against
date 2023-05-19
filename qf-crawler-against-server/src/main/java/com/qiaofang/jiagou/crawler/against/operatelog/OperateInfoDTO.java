package com.qiaofang.jiagou.crawler.against.operatelog;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 操作日志信息
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/13 4:20 下午
 */
@Data
@AllArgsConstructor
public class OperateInfoDTO {
    /**
     * 参数
     */
    private Object param;
    /**
     * 结果
     */
    private Object result;
}
