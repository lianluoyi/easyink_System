package com.easywecom.web.controller.wecom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.wecom.domain.dto.WePermanentCodeDTO;
import com.easywecom.wecom.domain.vo.*;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.We3rdAppService;
import com.easywecom.wecom.service.WeAuthCorpInfoService;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: We3rdAppController
 *
 * @author: 1*+
 * @date: 2021-09-08 16:14
 */
@RestController
@RequestMapping("/wecom/3rdapp")
@Slf4j
@ApiSupport(author = "1*+")
@Api(tags = "三方应用授权接口")
public class We3rdAppController {


    private final We3rdAppService we3rdAppService;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final RuoYiConfig ruoYiConfig;
    private final WeCorpAccountService weCorpAccountService;


    @Autowired
    public We3rdAppController(We3rdAppService we3rdAppService, WeAuthCorpInfoService weAuthCorpInfoService, RuoYiConfig ruoYiConfig, WeCorpAccountService weCorpAccountService) {
        this.we3rdAppService = we3rdAppService;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.ruoYiConfig = ruoYiConfig;
        this.weCorpAccountService = weCorpAccountService;
    }

    @GetMapping("/getPreAuthCode")
    @ApiOperation(value = "获取预授权码")
    public AjaxResult<WePreAuthCodeVO> getPreAuthCode() {
        return AjaxResult.success(we3rdAppService.getPreAuthCode());
    }

    @GetMapping("/getPermanentCode")
    @ApiOperation(value = "获取永久授权码")
    public AjaxResult getPermanentCode(WePermanentCodeDTO wePermanentCodeDTO) {
        we3rdAppService.handlePermanentCode(wePermanentCodeDTO.getAuthCode(), wePermanentCodeDTO.getSuiteId());
        return AjaxResult.success();
    }


    @GetMapping("/getPreLoginParam")
    @ApiOperation(value = "获取预登录参数")
    public AjaxResult<WePreLoginParamVO> getPreLoginParam() {
        return AjaxResult.success(we3rdAppService.getPreLoginParam());
    }


    @GetMapping("/getServerType")
    @ApiOperation(value = "获取当前服务器类型")
    public AjaxResult<WeServerTypeVO> getServerType() {
        return AjaxResult.success(we3rdAppService.getServerType());
    }

    @GetMapping("/checkDkSuiteAuthStatus")
    @ApiOperation(value = "检测代开发授权状态")
    public AjaxResult<SuiteAuthStatusVO> checkDkSuiteAuthStatus() {
        //授权未启用时 corpId是密文需要获得明文
        WeCorpAccount weCorpAccount = weCorpAccountService.getOne(new LambdaQueryWrapper<WeCorpAccount>().eq(WeCorpAccount::getExternalCorpId, LoginTokenService.getLoginUser().getCorpId()).last(GenConstants.LIMIT_1));
        String corpId = weCorpAccount == null ? LoginTokenService.getLoginUser().getCorpId() : weCorpAccount.getCorpId();
        SuiteAuthStatusVO result = SuiteAuthStatusVO.builder().authSuccess(weAuthCorpInfoService.corpAuthorized(corpId, ruoYiConfig.getProvider().getDkSuite().getDkId()))
                .corpId(LoginTokenService.getLoginUser().getCorpId()).suiteId(ruoYiConfig.getProvider().getDkSuite().getDkId()).build();
        return AjaxResult.success(result);
    }

    @GetMapping("/checkCorpId")
    @ApiOperation(value = "检测企业id是否为代开发")
    public AjaxResult<CheckCorpIdVO> checkCorpId() {
        return AjaxResult.success(weAuthCorpInfoService.isDkCorp(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/getDkQrCode")
    @ApiOperation(value = "获取待开发应用二维码")
    public AjaxResult getDkQrCode(){
        return AjaxResult.success("操作成功", ruoYiConfig.getProvider().getDkSuite().getDkQrCode());
    }
}
