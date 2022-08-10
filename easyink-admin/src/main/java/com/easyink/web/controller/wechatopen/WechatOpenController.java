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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/appId")
    @ApiOperation("获取公众号的appId")
    public AjaxResult<AppIdVO> getAppId(String shortCode) {
        return AjaxResult.success("操作成功", wechatOpenService.getAppId(shortCode));
    }

    @GetMapping("/openId")
    @ApiOperation("获取公众号Openid")
    public AjaxResult<String> getOpenId(@ApiParam("用户的code") String code ,@ApiParam("corpId")String corpId) {
        return AjaxResult.success("操作成功", wechatOpenService.getOpenId(code,corpId));
    }

    @GetMapping("/config")
    @ApiOperation("获取企业的公众号配置")
    public AjaxResult<WeOpenConfig> getConfig() {
        LoginUser user = LoginTokenService.getLoginUser();
        return AjaxResult.success(wechatOpenService.getConfig(user.getCorpId()));
    }

    @PostMapping("/config")
    @ApiOperation("修改企业的公众号配置")
    public AjaxResult updateConfig(@RequestBody WeOpenConfig config) {
        wechatOpenService.updateConfig(config);
        return AjaxResult.success();
    }
}
