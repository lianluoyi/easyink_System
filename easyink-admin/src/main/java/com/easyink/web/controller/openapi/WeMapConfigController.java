package com.easyink.web.controller.openapi;

import com.easyink.common.annotation.Encrypt;
import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.map.WeMapConfigDTO;
import com.easyink.wecom.domain.vo.WeMapConfigVO;
import com.easyink.wecom.service.WeMapConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 地图API配置控制器
 *
 * @author wx
 * @date 2023/8/5
 */
@Api(tags = "地图API配置管理")
@RestController
@RequestMapping("/wecom/map/config")
public class WeMapConfigController extends BaseController {
    
    private final WeMapConfigService weMapConfigService;

    @Autowired
    public WeMapConfigController(WeMapConfigService weMapConfigService) {
        this.weMapConfigService = weMapConfigService;
    }
    
    /**
     * 获取地图API配置
     */
    @ApiOperation("获取地图API配置")
    @GetMapping("/get")
    @Encrypt
//    @PreAuthorize("@ss.hasPermi('wecom:map:config:view')")
    public AjaxResult getConfig(
            @ApiParam(value = "企业id") @RequestParam String corpId,
            @ApiParam(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图", required = false, defaultValue = "1") @RequestParam(required = false, defaultValue = "1") Integer mapType
    ) {

        WeMapConfigVO configVO = weMapConfigService.getConfigVO(corpId, mapType);
        return AjaxResult.success(configVO);
    }
    
    /**
     * 保存地图API配置
     */
    @ApiOperation("保存企业地图API配置")
    @PostMapping("/saveCorpConfig")
//    @PreAuthorize("@ss.hasPermi('wecom:map:config:edit')")
    @Log(title = "保存地图API配置", businessType = BusinessType.INSERT)
    public AjaxResult saveCorpConfig(@Validated @RequestBody WeMapConfigDTO config) {

        boolean result = weMapConfigService.saveOrUpdateCorpConfig(config);
        return result ? AjaxResult.success() : AjaxResult.error("保存失败");
    }
    /**
     * 保存地图API配置
     */
    @ApiOperation("保存默认地图API配置")
    @PostMapping("/saveDefaultConfig")
//    @PreAuthorize("@ss.hasPermi('wecom:map:config:edit')")
    @Log(title = "保存地图API配置", businessType = BusinessType.INSERT)
    public AjaxResult saveDefaultConfig(@Validated @RequestBody WeMapConfigDTO config) {

        boolean result = weMapConfigService.saveOrUpdateDefaultConfig(config);
        return result ? AjaxResult.success() : AjaxResult.error("保存失败");
    }
    
    /**
     * 删除地图API配置
     */
    @ApiOperation("删除地图API配置")
    @DeleteMapping("/delete")
//    @PreAuthorize("@ss.hasPermi('wecom:map:config:delete')")
    @Log(title = "删除地图API配置", businessType = BusinessType.DELETE)
    public AjaxResult deleteConfig(
            @ApiParam(value = "企业id", required = true) @RequestParam String corpId,
            @ApiParam(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图", required = true) @RequestParam Integer mapType) {
        
        boolean result = weMapConfigService.deleteCorpConfig(corpId, mapType);
        return result ? AjaxResult.success() : AjaxResult.error("删除失败");
    }
} 