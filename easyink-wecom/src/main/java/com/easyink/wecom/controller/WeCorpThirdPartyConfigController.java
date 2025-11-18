package com.easyink.wecom.controller;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.WeCorpThirdPartyConfig;
import com.easyink.wecom.domain.dto.form.push.DeleteThirdPartyConfigDTO;
import com.easyink.wecom.domain.dto.form.push.WeCorpThirdPartyConfigDTO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCorpThirdPartyConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 企业第三方服务推送配置Controller
 *
 * @author easyink
 * @date 2024-01-01
 */
@Api(tags = "企业第三方服务推送配置")
@RestController
@RequestMapping("/wecom/thirdPartyConfig")
@RequiredArgsConstructor
public class WeCorpThirdPartyConfigController extends BaseController {

    private final WeCorpThirdPartyConfigService thirdPartyConfigService;



    /**
     * 获取企业第三方服务推送配置详细信息
     */
    @ApiOperation("获取企业第三方服务推送配置详细信息")
//    @PreAuthorize("@ss.hasPermi('form:thirdPartyConfig:info')")
    @GetMapping("/info")
    public AjaxResult getInfo() {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WeCorpThirdPartyConfig config = thirdPartyConfigService.getByCorpId(corpId);
        return AjaxResult.success(config);
    }

    /**
     * 新增\修改企业第三方服务推送配置
     */
    @ApiOperation("修改企业第三方服务推送配置")
//    @PreAuthorize("@ss.hasPermi('form:thirdPartyConfig:edit')")
    @Log(title = "企业第三方服务推送配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody WeCorpThirdPartyConfigDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        thirdPartyConfigService.saveOrUpdateConfig(dto, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }

    /**
     * 删除企业第三方服务推送配置
     */
    @ApiOperation("删除企业第三方服务推送配置")
//    @PreAuthorize("@ss.hasPermi('form:thirdPartyConfig:remove')")
    @Log(title = "企业第三方服务推送配置", businessType = BusinessType.DELETE)
    @PostMapping("/batch/delete")
    public AjaxResult remove(DeleteThirdPartyConfigDTO dto) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        thirdPartyConfigService.deleteConfig(dto.getCorpIdList(), corpId);
        return AjaxResult.success();
    }

    /**
     * 启用/禁用配置
     */
    @ApiOperation("启用/禁用配置")
//    @PreAuthorize("@ss.hasPermi('form:thirdPartyConfig:edit')")
    @Log(title = "企业第三方服务推送配置", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@ApiParam("状态") @RequestParam Integer status) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        thirdPartyConfigService.updateStatus(corpId, status);
        return AjaxResult.success();
    }

}
