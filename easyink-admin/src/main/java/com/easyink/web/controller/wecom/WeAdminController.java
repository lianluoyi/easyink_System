package com.easyink.web.controller.wecom;

import com.easyink.common.config.ThirdDefaultDomainConfig;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.AutoConfigDTO;
import com.easyink.wecom.domain.vo.WeAdminQrcodeVO;
import com.easyink.wecom.domain.vo.WeCheckQrcodeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeAutoConfigService;
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

    @GetMapping("/getDepartMemberInfo")
    @ApiOperation("获取部门成员信息")
    public AjaxResult getDepartMemberInfo(String qrcodeKey) {
        String corpId = LoginTokenService.getLoginUser()
                                         .getCorpId();
        weAutoConfigService.getDepartMemberInfo(corpId, qrcodeKey) ;
        return AjaxResult.success();
    }

    @ApiOperation("获取默认应用域名配置")
    @GetMapping("/getDefaultDomainConfig")
    public AjaxResult<ThirdDefaultDomainConfig> getDefaultDomainConfig() {
        return AjaxResult.success(weAutoConfigService.getThirdDefaultDomainConfig());
    }

    @ApiOperation("扫码登录企微后台验证手机短信验证码")
    @GetMapping("/confirmMobileCaptcha")
    public AjaxResult confirmMobileCaptcha(@ApiParam(value = "短信验证码") String captcha, @ApiParam(value = "短信验证需要的tlKey") String tlKey,
                                           @ApiParam(value = "qrKey由获取二维码接口返回") String qrcodeKey) {
        weAutoConfigService.confirmMobileCaptcha(captcha, tlKey, qrcodeKey);
        return AjaxResult.success();
    }

    @ApiOperation("重新发送手机验证码")
    @GetMapping("/sendCaptcha")
    public AjaxResult sendCaptcha(@ApiParam(value = "短信验证需要的tlKey") String tlKey, @ApiParam(value = "qrcodeKey") String qrcodeKey) {
        weAutoConfigService.sendCaptcha(tlKey, qrcodeKey);
        return AjaxResult.success();
    }


}
