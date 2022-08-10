package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeApp;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: WeAppController
 *
 * @author: 1*+
 * @date: 2021-08-27 17:19
 */
@Api(value = "WeAppController", tags = "企业微信应用配置接口", hidden = true)
@RestController
@RequestMapping("/wecom/weapp")
public class WeAppController extends BaseController {


    /**
     * 应用列表
     *
     * @return 应用列表
     */
    @ApiOperation("获取应用列表")
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success();
    }

    @ApiOperation("添加应用")
    @ApiOperationSupport(ignoreParameters = {"id"})
    @PostMapping("/addWeapp")
    @Deprecated
    public AjaxResult addWeApp(@RequestBody WeApp weApp) {
        return AjaxResult.success();
    }

    @ApiOperation("更新应用")
    @PutMapping("/updateWeApp")
    @Deprecated
    public AjaxResult updateWeApp(@RequestBody WeApp weApp) {
        return AjaxResult.success();
    }


}
