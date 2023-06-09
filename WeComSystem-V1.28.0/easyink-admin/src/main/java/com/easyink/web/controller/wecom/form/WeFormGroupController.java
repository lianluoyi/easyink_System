package com.easyink.web.controller.wecom.form;


import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.form.ChangeFormGroupSortDTO;
import com.easyink.wecom.domain.dto.form.FormGroupAddDTO;
import com.easyink.wecom.domain.dto.form.FormGroupUpdateDTO;
import com.easyink.wecom.domain.vo.form.FormGroupTreeVO;
import com.easyink.wecom.domain.vo.form.FormGroupTrees;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.form.WeFormGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 表单分组表(WeFormGroup)表控制层
 *
 * @author tigger
 * @since 2023-01-09 11:23:21
 */
@RestController
@RequestMapping("/wecom/form/group")
public class WeFormGroupController extends BaseController {

    @Autowired
    private WeFormGroupService weFormGroupService;


    @GetMapping("/tree")
    public AjaxResult<List<FormGroupTreeVO>> selectTree(
            @RequestParam(value = "sourceType") Integer sourceType,
            @RequestParam(value = "departmentId",required = false) Integer departmentId
            ) {

        return AjaxResult.success(this.weFormGroupService.selectTree(sourceType,departmentId,
                LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/trees")
    public AjaxResult<FormGroupTrees> selectTrees(@RequestParam(value = "departmentId",required = false) Integer departmentId) {
        return AjaxResult.success(this.weFormGroupService.selectTrees(departmentId,
                LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("新增分组")
    @PostMapping("/add")
    public AjaxResult insert(@Validated @RequestBody FormGroupAddDTO addDTO) {
        this.weFormGroupService.saveGroup(addDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("删除分组")
    @DeleteMapping("/remove")
    public AjaxResult delete(@RequestParam("id") Integer id) {
        this.weFormGroupService.deleteGroup(id, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }


    @ApiOperation("更新分组")
    @PostMapping("/edit")
    public AjaxResult update(@Validated @RequestBody FormGroupUpdateDTO updateDTO) {
        this.weFormGroupService.updateGroup(updateDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("修改排序")
    @PostMapping("/changeSort")
    public AjaxResult changeSort(@Validated @RequestBody ChangeFormGroupSortDTO sortDTO) {
        this.weFormGroupService.changeSort(sortDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

}

