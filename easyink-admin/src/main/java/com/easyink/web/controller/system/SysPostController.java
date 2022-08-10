package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.system.SysPost;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 岗位信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/post")
@Api(tags = "岗位信息操作处理")
@Deprecated
public class SysPostController extends BaseController {
    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    @ApiOperation("获取岗位列表")
    public TableDataInfo list(SysPost post) {
        return getDataTable(new ArrayList<>());
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @GetMapping("/export")
    @ApiOperation("岗位管理")
    public AjaxResult export(SysPost post) {
        return AjaxResult.success();
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @ApiOperation("根据岗位编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId) {
        return AjaxResult.success();
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增岗位")
    public AjaxResult add(@Validated @RequestBody SysPost post) {
        return AjaxResult.success();

    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改岗位")
    public AjaxResult edit(@Validated @RequestBody SysPost post) {
        return AjaxResult.success();

    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    @ApiOperation("删除岗位")
    public AjaxResult remove(@PathVariable Long[] postIds) {
        return AjaxResult.success();

    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    @ApiOperation("获取岗位选择框列表")
    public AjaxResult optionselect() {
        return AjaxResult.success();

    }
}
