package com.qiaofang.jiagou.crawler.against.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 验证码校验参数
 * @author shihao.liu
 * @version 1.0
 * @date 2020/8/13 10:29 上午
 */
@Data
public class KaptchaValidParam {

    @NotBlank(message = "token不能为空")
    private String token;
    @NotBlank(message = "code不能为空")
    private String code;
    @NotBlank(message = "matchDimensionKey不能为空")
    private String matchDimensionKey;
}
