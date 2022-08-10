package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.WeMaterialConfig;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeMaterialConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 类名：WeMaterialConfigController
 *
 * @author Society my sister Li
 * @date 2021-10-11 15:56
 */
@Api(tags = "企业素材配置表")
@RestController
@RequestMapping("/wecom/materialConfig")
public class WeMaterialConfigController {

    private final WeMaterialConfigService weMaterialConfigService;

    @Autowired
    public WeMaterialConfigController(WeMaterialConfigService weMaterialConfigService) {
        this.weMaterialConfigService = weMaterialConfigService;
    }

    @Log(title = "获取素材配置", businessType = BusinessType.OTHER)
    @GetMapping(value = "/get")
    @ApiOperation("获取素材配置信息")
    public AjaxResult<WeMaterialConfig> get() {
        return AjaxResult.success(weMaterialConfigService.findByCorpId(LoginTokenService.getLoginUser().getCorpId()));
    }

    @Log(title = "更新素材配置", businessType = BusinessType.OTHER)
    @PutMapping("/update")
    @ApiOperation("更新素材配置")
    public AjaxResult upload(@Valid @RequestBody WeMaterialConfig weMaterialConfig) {
        weMaterialConfig.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weMaterialConfigService.update(weMaterialConfig);
        return AjaxResult.success();
    }
}
