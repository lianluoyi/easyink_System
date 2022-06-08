package com.easywecom.web.controller.monitor;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.system.SysLogininfor;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.service.ISysLogininforService;
import com.easywecom.common.utils.poi.ExcelUtil;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: SysLogininforController
 *
 * @author: 1*+
 * @date: 2021-08-27 16:40
 */
@RestController
@RequestMapping("/monitor/logininfor")
@ApiSupport(order = 4, author = "1*+")
@Api(value = "SysLogininforController", tags = "系统访问记录接口")
public class SysLogininforController extends BaseController {

    @Autowired
    private ISysLogininforService logininforService;


    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    @ApiOperation("系统访问日志列表")
    public TableDataInfo<SysLogininfor> list(SysLogininfor logininfor) {
        startPage();
        logininfor.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @GetMapping("/export")
    @ApiOperation("导出系统访问日志")
    public AjaxResult export(SysLogininfor logininfor) {
        logininfor.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<>(SysLogininfor.class);
        return util.exportExcel(list, "登录日志");
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    @ApiOperation("批量移除系统访问日志")
    public AjaxResult remove(@ApiParam("记录ID数组") @PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(LoginTokenService.getLoginUser().getCorpId(), infoIds));
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    @ApiOperation("清空系统访问日志")
    public AjaxResult clean() {
        logininforService.cleanLogininfor(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }
}
