package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.system.SysNotice;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 公告 信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/notice")
@Api(tags = "信息操作处理")
@Deprecated
public class SysNoticeController extends BaseController {

    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    @ApiOperation("获取通知公告列表")
    @Deprecated
    public TableDataInfo list(SysNotice notice) {
        return getDataTable(new ArrayList<>());
    }

    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    @ApiOperation("根据通知公告编号获取详细信息")
    @Deprecated
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        return AjaxResult.success();
    }


    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增通知公告")
    @Deprecated
    public AjaxResult add(@Validated @RequestBody SysNotice notice) {
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改通知公告")
    @Deprecated
    public AjaxResult edit(@Validated @RequestBody SysNotice notice) {
        return AjaxResult.success();
    }


    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    @ApiOperation("删除通知公告")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return AjaxResult.success();
    }
}
