package com.easywecom.web.controller.wecom;

import com.easywecom.common.config.ThirdDefaultDomainConfig;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.wecom.domain.dto.AutoConfigDTO;
import com.easywecom.wecom.domain.vo.WeAdminQrcodeVO;
import com.easywecom.wecom.domain.vo.WeCheckQrcodeVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeAutoConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 类名: 企业微信后台接口
 *
 * @author: 1*+
 * @date: 2021-08-06 16:53
 */
@Api(value = "WeAdminController", tags = "企业微信后台接口")
@RestController
@RequestMapping("/wecom/admin")
public class WeAdminController extends BaseController {


    private final WeAutoConfigService weAutoConfigService;

    @Autowired
    public WeAdminController(WeAutoConfigService weAutoConfigService) {
        this.weAutoConfigService = weAutoConfigService;
    }

    @ApiOperation("获取登录企业微信后台的二维码")
    @GetMapping("/getAdminLoginQrcode")
    public AjaxResult<WeAdminQrcodeVO> getAdminLoginQrcode() {
        WeAdminQrcodeVO weAdminQrcodeVO = weAutoConfigService.getAdminQrcode();
        return AjaxResult.success(weAdminQrcodeVO);
    }

    @ApiOperation("检测登录企业微信后台的二维码")
    @GetMapping("/checkAdminLoginQrcode")
    public AjaxResult<WeCheckQrcodeVO> checkAdminLoginQrcode(@ApiParam("二维码Key") @RequestParam("qrcodeKey") String qrcodeKey, @ApiParam("二维码状态") @RequestParam("status") String status) {
        WeCheckQrcodeVO weCheckQrcodeVO = weAutoConfigService.check(qrcodeKey, status, LoginTokenService.getLoginUser());
        return AjaxResult.success(weCheckQrcodeVO);
    }

    /**
     * 启动自动配置
     *
     * @return
     */
    @ApiOperation("启动自动配置")
    @PostMapping("/autoConfig")
    public AjaxResult autoConfig(@Valid @RequestBody AutoConfigDTO autoConfigDTO) {
        weAutoConfigService.autoConfig(autoConfigDTO, LoginTokenService.getLoginUser());
        //主要是为了补偿admin帐号首次配置内部应用时，原本是没有corpid
        LoginTokenService.refreshDataScope();
        return AjaxResult.success();
    }

    @ApiOperation("获取默认应用域名配置")
    @GetMapping("/getDefaultDomainConfig")
    public AjaxResult<ThirdDefaultDomainConfig> getDefaultDomainConfig() {
        return AjaxResult.success(weAutoConfigService.getThirdDefaultDomainConfig());
    }


}
