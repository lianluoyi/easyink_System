package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.SysDept;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.service.ISysDeptService;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.login.util.LoginTokenService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/dept")
@ApiSupport(order = 8, author = "1*+")
@Api(value = "SysDeptController", tags = "系统部门接口", hidden = true)
public class SysDeptController extends BaseController {
    @Autowired
    private ISysDeptService deptService;


    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    @ApiOperation(value = "根据部门编号获取详细信息", hidden = true)
    @Deprecated
    public AjaxResult<SysDept> getInfo(@ApiParam("部门ID") @PathVariable Long deptId) {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    @GetMapping("/treeselect")
    @ApiOperation(value = "获取部门下拉树列表", hidden = true)
    public AjaxResult treeselect() {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        List<WeDepartment> depts = deptService.selectDeptList(
                WeDepartment.builder().corpId(loginUser.getCorpId()).build(),
                loginUser);
        return AjaxResult.success(deptService.buildDeptTreeSelect(depts));
    }

    @ApiOperation(value = "加载对应角色部门列表树", hidden = true)
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public AjaxResult roleDeptTreeselect(@ApiParam("角色ID") @PathVariable("roleId") Long roleId) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        List<WeDepartment> depts = deptService.selectDeptList(
                WeDepartment.builder().corpId(loginUser.getCorpId()).build(),
                loginUser);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.buildDeptTreeSelect(depts));
        return ajax;
    }

    @ApiOperation(value = "新增部门", hidden = true)
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    @Deprecated
    public AjaxResult add(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(LoginTokenService.getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    @ApiOperation(value = "修改部门", hidden = true)
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @Deprecated
    public AjaxResult edit(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(dept.getDeptId())) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0) {
            return AjaxResult.error("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    @ApiOperation(value = "删除部门", hidden = true)
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    @Deprecated
    public AjaxResult remove(@ApiParam("部门ID") @PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return AjaxResult.error("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.error("部门存在用户,不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
