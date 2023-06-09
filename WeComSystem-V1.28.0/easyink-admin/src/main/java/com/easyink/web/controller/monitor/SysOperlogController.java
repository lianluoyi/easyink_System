package com.easyink.web.controller.monitor;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.system.SysOperLog;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.service.ISysOperLogService;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.login.util.LoginTokenService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录
 *
 * @author admin
 */
@RestController
@RequestMapping("/monitor/operlog")
@ApiSupport(order = 5, author = "1*+")
@Api(value = "SysOperlogController", tags = "系统操作记录接口")
public class SysOperlogController extends BaseController {
    @Autowired
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    @ApiOperation("系统操作日志列表")
    public TableDataInfo<SysOperLog> list(SysOperLog operLog) {
        startPage();
        operLog.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @GetMapping("/export")
    @ApiOperation("导出系统操作日志")
    public AjaxResult export(SysOperLog operLog) {
        operLog.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<>(SysOperLog.class);
        return util.exportExcel(list, "操作日志");
    }

    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    @ApiOperation("批量删除系统操作日志")
    public AjaxResult remove(@ApiParam("记录ID数组") @PathVariable Long[] operIds) {
        return toAjax(operLogService.deleteOperLogByIds(LoginTokenService.getLoginUser().getCorpId(), operIds));
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    @ApiOperation("清空系统操作日志")
    public AjaxResult clean() {
        operLogService.cleanOperLog(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }
}
