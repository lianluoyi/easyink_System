package com.easyink.web.controller.wechatopen;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.AppIdVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.util.annotation.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 类名: 微信小程序/公众号相关接口
 *
 * @author : silver_chariot
 * @date : 2022/7/20 10:03
 **/
@RestController
@RequestMapping("/wechatopen")
public class WechatOpenController extends BaseController {

    private final WechatOpenService wechatOpenService;

    public WechatOpenController(WechatOpenService wechatOpenService) {
        this.wechatOpenService = wechatOpenService;
    }

    @GetMapping("/getAppIdByShortCode")
    @ApiOperation("通过短链获取公众号的appId")
    public AjaxResult<AppIdVO> getAppId(@Nullable @ApiParam("短链") String shortCode) {
        return AjaxResult.success("操作成功", wechatOpenService.getAppIdByShortCode(shortCode));
    }

    @GetMapping("/getAppIdByFormId")
    @ApiOperation("通过表单id获取公众号的appId")
    public AjaxResult<AppIdVO> getAppIdByFormId(@Nullable @ApiParam("表单formId") Long formId){
        return AjaxResult.success("操作成功", wechatOpenService.getAppIdByFormId(formId));
    }

    @GetMapping("/getDomain")
    @ApiOperation("获取公众号的域名/中间页域名(需要登录)")
    public AjaxResult getDomain() {
        return AjaxResult.success("操作成功", wechatOpenService.getDomain(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/openId")
    @ApiOperation("获取公众号Openid")
    public AjaxResult<String> getOpenId(@ApiParam("用户的code") String code ,@ApiParam("corpId")String corpId, @ApiParam("公众号appId")String appId) {
        return AjaxResult.success("操作成功", wechatOpenService.getOpenId(code,corpId, appId));
    }

    @GetMapping("/config")
    @ApiOperation("获取企业的公众号配置")
    public AjaxResult getConfig() {
        LoginUser user = LoginTokenService.getLoginUser();
        return AjaxResult.success(wechatOpenService.getConfigs(user.getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('officialAccountsManager:set')")
    @PostMapping("/config")
    @ApiOperation("修改企业的公众号配置")
    public AjaxResult updateConfig(@RequestBody WeOpenConfig config) {
        wechatOpenService.updateConfig(config, true);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('officialAccountsManager:set')")
    @GetMapping("/getWechatOpen3rdAuthUrl")
    @ApiOperation("获取微信三方平台授权页url")
    public AjaxResult getWechatOpen3rdAuthUrl() {
        return AjaxResult.success(wechatOpenService.getWechatOpen3rdAuthUrl(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/getAuthCode")
    @ApiOperation("公众号授权三方平台回调")
    public void getAuthCode(@RequestParam("corpId") String corpId,
                            @RequestParam("auth_code") String authCode,
                            String userId,
                            HttpServletResponse response) throws IOException {
        response.sendRedirect(wechatOpenService.handle3rdAuthOfficeAccount(corpId, userId, authCode));
    }
}
