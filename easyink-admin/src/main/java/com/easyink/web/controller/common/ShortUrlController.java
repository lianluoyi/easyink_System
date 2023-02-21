package com.easyink.web.controller.common;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.ip.IpUtils;
import com.easyink.wecom.service.radar.MiniAppQrCodeUrlHandler;
import com.easyink.wecom.service.radar.RadarUrlHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 类名: 短链相关接口
 *
 * @author : silver_chariot
 * @date : 2022/7/18 17:08
 **/
@RestController
@RequestMapping("/url")
@Api(value = "ShortUrlController", tags = "短链相关")
@Slf4j
public class ShortUrlController {


    private final MiniAppQrCodeUrlHandler miniAppQrCodeUrlHandler;
    private final RadarUrlHandler radarUrlHandler;

    public ShortUrlController(MiniAppQrCodeUrlHandler miniAppQrCodeUrlHandler, RadarUrlHandler radarUrlHandler) {
        this.miniAppQrCodeUrlHandler = miniAppQrCodeUrlHandler;
        this.radarUrlHandler = radarUrlHandler;
    }

    @GetMapping("/empleCode")
    @ApiOperation("根据短链获取员工活码")
    public AjaxResult<String> empleCode(@ApiParam("短链后缀的code") @Validated @NotBlank(message = "missing param") String code) {
        return AjaxResult.success("success", miniAppQrCodeUrlHandler.getQrCode(code));
    }

    @GetMapping("/radar")
    @ApiOperation("根据短链获取雷达链接并记录")
    public AjaxResult<String> radar(@ApiParam("短链后缀的code") String shortCode, @ApiParam("用户的公众号openid") String openId) {
        String serverIp = "";
        try {
            serverIp = IpUtils.getOutIp();
        } catch (Exception e) {
            log.error("[雷达/表单链接]获取服务器ip异常.e:{}", ExceptionUtils.getStackTrace(e));
        }
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        log.info("[雷达/表单短链]有人点击了短链,shortCode:{},openId:{},ip:{},serverIp:{}", shortCode, openId, ip, serverIp);
        if (serverIp.equals(ip)) {
            log.info("[雷达/表单短链]ip与服务器ip一样,不处理,ip:{}", ip);
            return AjaxResult.success();
        }
        return AjaxResult.success("success", radarUrlHandler.getOriginUrlAndRecord(shortCode, openId));
    }


}
