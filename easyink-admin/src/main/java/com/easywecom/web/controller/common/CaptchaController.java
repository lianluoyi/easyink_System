package com.easywecom.web.controller.common;

import com.easywecom.common.constant.Constants;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.utils.sign.Base64;
import com.easywecom.common.utils.uuid.IdUtils;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 类名: CaptchaController
 *
 * @author: 1*+
 * @date: 2021-08-27 15:52
 */
@Api(value = "CaptchaController", tags = "验证码接口")
@ApiSupport(order = 1, author = "1*+")
@RestController
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;

    // 验证码类型
    @Value("${ruoyi.captchaType}")
    private String captchaType;

    private static final String MATH = "math";

    /**
     * 生成验证码
     */
    @ApiOperation("生成验证码")
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) {
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr;
        String code;
        BufferedImage image;

        // 生成验证码
        if (MATH.equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }
}
