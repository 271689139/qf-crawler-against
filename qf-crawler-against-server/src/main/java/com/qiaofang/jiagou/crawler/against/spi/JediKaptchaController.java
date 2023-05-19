package com.qiaofang.jiagou.crawler.against.spi;

import com.qiaofang.jiagou.crawler.against.param.KaptchaValidParam;
import com.qiaofang.jiagou.crawler.against.response.JediCustomResponse;
import com.qiaofang.jiagou.crawler.against.service.IKaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 验证码服务
 */
@RestController
@RequestMapping("/spi/jedi/kaptcha")
public class JediKaptchaController extends JediCustomControllerExceptionHandler {

    @Autowired
    private IKaptchaService kaptchaService;


    /**
     * 获取token
     *
     * @return
     */
    @GetMapping("/token")
    public JediCustomResponse<String> getToken() {
        return JediCustomResponse.ok(kaptchaService.generateToken());
    }

    /**
     * 生成验证码
     *
     * @param token  必选
     * @param width  可选参数 宽度
     * @param height 可选参数 高度
     * @return 直接返回图片流
     */
    @GetMapping("/render")
    public void render(@RequestParam String token, @RequestParam(required = false) String width, @RequestParam(required = false) String height,
                       @RequestParam(required = false) Integer validSecond, HttpServletRequest request, HttpServletResponse response) {
        kaptchaService.render(token, width, height, validSecond, request, response);
    }

    /**
     * 验证
     *
     * @param param
     * @return responseCode 为1则表示验证成功，其他都是失败，responseMessage为失败原因
     */
    @PostMapping("/valid")
    public JediCustomResponse<String> valid(@RequestBody @Valid KaptchaValidParam param) {
        kaptchaService.valid(param.getToken(), param.getCode(), param.getMatchDimensionKey());
        return JediCustomResponse.ok("验证成功");
    }
}