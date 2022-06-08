package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.WeCategory;
import com.easywecom.wecom.domain.dto.WeCategorySidebarSwitchDTO;
import com.easywecom.wecom.domain.vo.WeCategoryBaseInfoVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeCategoryService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: WeCategoryController
 *
 * @author: 1*+
 * @date: 2021-08-27 11:08
 */
@RestController
@RequestMapping("/wecom/category")
@Api(value = "WeCategoryController", tags = "企业微信素材分类接口")
public class WeCategoryController extends BaseController {

    @Autowired
    private WeCategoryService weCategoryService;


    @PreAuthorize("@ss.hasPermi('wechat:category:list')")
    @GetMapping("/list")
    @ApiOperation("类目树")
    public AjaxResult list(@ApiParam(value = "分类类型", allowableValues = "range[0, 5]") @RequestParam("mediaType") Integer mediaType) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weCategoryService.findWeCategoryByMediaType(corpId, mediaType));
    }

    /**
     * 通过id查询类目详细信息
     */
//    @PreAuthorize("@ss.hasPermi('wechat:category:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("通过id查询类目详细信息")
    public AjaxResult<WeCategory> getInfo(@ApiParam("类目ID") @PathVariable("id") Long id) {
        return AjaxResult.success(weCategoryService.getById(id));
    }

    /**
     * 添加类目
     */
//    @PreAuthorize("@ss.hasPermi('wechat:category:add')")
    @Log(title = "添加类目", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = {"id"})
    @ApiOperation("添加类目")
    public AjaxResult add(@RequestBody WeCategory category) {
        category.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCategoryService.insertWeCategory(category);
        return AjaxResult.success();
    }

    /**
     * 更新目录
     */
//    @PreAuthorize("@ss.hasPermi('wechat:category:edit')")
    @Log(title = "更新目录", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("更新目录")
    public AjaxResult edit(@RequestBody WeCategory category) {
        category.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCategoryService.updateWeCategory(category);
        return AjaxResult.success();
    }


    /**
     * 删除类目
     */
//    @PreAuthorize("@ss.hasPermi('wechat:category:remove')")
    @Log(title = "删除类目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除类目")
    public AjaxResult remove(@PathVariable Long[] ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        weCategoryService.deleteWeCategoryById(corpId, ids);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wechat:material:sidebar')")
    @Log(title = "侧边栏开关", businessType = BusinessType.DELETE)
    @PutMapping("/sidebarSwitch")
    @ApiOperation("侧边栏开关")
    public AjaxResult sidebarSwitch(@Validated @RequestBody WeCategorySidebarSwitchDTO sidebarSwitchDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        sidebarSwitchDTO.setCorpId(corpId);
        weCategoryService.sidebarSwitch(sidebarSwitchDTO);
        return AjaxResult.success();
    }


    @GetMapping("/getList")
    @ApiOperation("获取所有类型列表")
    public AjaxResult getList() {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        List<WeCategoryBaseInfoVO> showWeCategory = weCategoryService.findListByCorpId(corpId);
        return AjaxResult.success(showWeCategory);
    }
}
