package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.dto.wegrouptag.*;
import com.easywecom.wecom.domain.vo.wegrouptag.PageWeGroupTagCategoryVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeGroupTagCategoryService;
import com.easywecom.wecom.service.WeGroupTagRelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名：WeGroupTagController
 *
 * @author Society my sister Li
 * @date 2021-11-12 15:55
 */
@RestController
@RequestMapping("/wecom/grouptag")
@Api(tags = "群标签相关接口")
public class WeGroupTagController extends BaseController {

    private final WeGroupTagCategoryService weGroupTagCategoryService;
    private final WeGroupTagRelService weGroupTagRelService;

    @Autowired
    public WeGroupTagController(WeGroupTagCategoryService weGroupTagCategoryService, WeGroupTagRelService weGroupTagRelService) {
        this.weGroupTagCategoryService = weGroupTagCategoryService;
        this.weGroupTagRelService = weGroupTagRelService;
    }

    @Log(title = "新增群标签组", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增群标签组")
    public AjaxResult add(@Validated @RequestBody AddWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupTagCategoryService.add(weGroupTagCategory);
        return AjaxResult.success();
    }

    @Log(title = "编辑群标签组", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("编辑群标签组")
    public AjaxResult update(@Validated @RequestBody UpdateWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupTagCategoryService.update(weGroupTagCategory);
        return AjaxResult.success();
    }

    @Log(title = "删除群标签组", businessType = BusinessType.DELETE)
    @DeleteMapping
    @ApiOperation("删除群标签组")
    public AjaxResult del(@Validated @RequestBody DelWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupTagCategoryService.delete(weGroupTagCategory);
        return AjaxResult.success();
    }

    @Log(title = "分页查询标签组列表", businessType = BusinessType.OTHER)
    @GetMapping("/page")
    @ApiOperation("分页查询标签组列表")
    public TableDataInfo<PageWeGroupTagCategoryVO> page(@Validated PageWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        return getDataTable(weGroupTagCategoryService.page(weGroupTagCategory));
    }


    @Log(title = "查询标签组列表", businessType = BusinessType.OTHER)
    @GetMapping("/list")
    @ApiOperation("查询标签组列表")
    public AjaxResult list(@Validated FindWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weGroupTagCategoryService.list(weGroupTagCategory));
    }

    @Log(title = "查询标签组列表", businessType = BusinessType.OTHER)
    @GetMapping("/get")
    @ApiOperation("查询标签组列表")
    public AjaxResult findInfo(@Validated FindWeGroupTagCategoryDTO weGroupTagCategory) {
        weGroupTagCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weGroupTagCategoryService.findInfo(weGroupTagCategory));
    }


    @Log(title = "批量打标签", businessType = BusinessType.INSERT)
    @PostMapping("/batchAddTagRel")
    @ApiOperation("批量打标签")
    @PreAuthorize("@ss.hasPermi('customerManage:group:edit')")
    public AjaxResult batchAddTagRel(@Validated @RequestBody BatchTagRelDTO batchTagRelDTO) {
        batchTagRelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupTagRelService.batchAddTagRel(batchTagRelDTO);
        return AjaxResult.success();
    }


    @Log(title = "批量移除标签", businessType = BusinessType.DELETE)
    @PostMapping("/batchDelTagRel")
    @ApiOperation("批量打标签")
    public AjaxResult batchDelTagRel(@Validated @RequestBody BatchTagRelDTO batchTagRelDTO) {
        batchTagRelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupTagRelService.batchDelTagRel(batchTagRelDTO);
        return AjaxResult.success();
    }
}
