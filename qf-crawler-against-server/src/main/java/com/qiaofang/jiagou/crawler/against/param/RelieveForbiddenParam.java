package com.qiaofang.jiagou.crawler.against.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/21 6:25 下午
 */
@Data
public class RelieveForbiddenParam {

    @NotNull(message = "id不能为空")
    private Long id;
    private String remark;
}
