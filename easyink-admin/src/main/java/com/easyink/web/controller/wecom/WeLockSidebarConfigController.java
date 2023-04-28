package com.easyink.web.controller.wecom;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeLockSidebarConfig;
import com.easyink.wecom.domain.dto.LockSidebarConfigDTO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLockSidebarConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 第三方SCRM系统侧边栏配置(WeLockSidebarConfig)表控制层
 *
 * @author wx
 * @since 2023-03-14 15:39:06
 */
@RestController
@RequestMapping("/wecom/lockSideBar")
public class WeLockSidebarConfigController {
    /**
     * 服务对象
     */
    @Resource
    private WeLockSidebarConfigService weLockSidebarConfigService;


    @GetMapping("/get")
    @ApiOperation("第三方SCRM系统查询侧边栏配置")
    public AjaxResult get(@RequestParam("appId") String appId) {
        return AjaxResult.success(weLockSidebarConfigService.getConfig(appId));
    }

    @GetMapping("/list")
    @ApiOperation("查询侧边栏配置")
    public AjaxResult list() {
        return AjaxResult.success(weLockSidebarConfigService.list(new LambdaQueryWrapper<WeLockSidebarConfig>().eq(WeLockSidebarConfig::getCorpId, LoginTokenService.getLoginUser().getCorpId())));
    }

    @PutMapping
    @ApiOperation("配置侧边栏配置")
    public AjaxResult edit(@RequestBody @Validated LockSidebarConfigDTO dto) {
        weLockSidebarConfigService.edit(dto);
        return AjaxResult.success();
    }

}

