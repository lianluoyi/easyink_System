package com.easyink.web.controller.wecom;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.service.UpdateIDSecurityService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * ClassName： UpdateIDSecurityController
 *
 * @author wx
 * @date 2022/8/19 9:49
 */
@Api("企业微信帐号ID安全性全面升级")
@RestController
@RequestMapping("/wecom/updateid")
@Slf4j
public class UpdateIDSecurityController {

    @Autowired
    private UpdateIDSecurityService updateIDSecurityService;

    @ApiOperation("进行id转换")
    @GetMapping("")
    public <T> AjaxResult<T> transfer(@RequestParam(value = "enableFullUpdate") Boolean enableFullUpdate, @RequestParam(value = "corpId") String corpId) {
        try {
            updateIDSecurityService.corpIdHandle(enableFullUpdate, corpId);
        } catch (Exception e) {
            log.error("企业微信帐号ID安全性全面升级 id转换失败 ex：{}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("转换失败");
        }
        return AjaxResult.success();
    }
}
