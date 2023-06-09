package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.annotation.RepeatSubmit;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.system.SysConfig;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.service.ISysConfigService;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.login.util.LoginTokenService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/config")
@ApiSupport(order = 7, author = "1*+")
@Api(value = "SysUserOnlineController", tags = "参数配置接口")
public class SysConfigController extends BaseController {
    @Autowired
    private ISysConfigService configService;

    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    @ApiOperation("参数配置列表")
    public TableDataInfo<SysConfig> list(SysConfig config) {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @GetMapping("/export")
    @ApiOperation("导出参数配置")
    public AjaxResult export(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<>(SysConfig.class);
        return util.exportExcel(list, "参数数据");
    }

    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    @ApiOperation("获取参数配置详情")
    public AjaxResult<SysConfig> getInfo(@ApiParam("配置ID") @PathVariable Long configId) {
        return AjaxResult.success(configService.selectConfigById(configId));
    }

    @ApiOperation("获取参数值")
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult<String> getConfigKey(@ApiParam("配置键值") @PathVariable String configKey) {
        return AjaxResult.success(configService.selectConfigByKey(configKey));
    }

    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    @ApiOperation("新增参数配置")
    public AjaxResult add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(LoginTokenService.getUsername());
        return toAjax(configService.insertConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("编辑参数配置")
    public AjaxResult edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    @ApiOperation("删除参数配置")
    public AjaxResult remove(@ApiParam("配置ID数组") @PathVariable Long[] configIds) {
        return toAjax(configService.deleteConfigByIds(configIds));
    }

    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCache")
    @ApiOperation("清空参数缓存")
    public AjaxResult clearCache() {
        configService.clearCache();
        return AjaxResult.success();
    }
}
