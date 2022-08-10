package com.easywecom.web.controller.system;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.constant.UserConstants;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.entity.SysRole;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.service.ISysRoleService;
import com.easywecom.common.utils.poi.ExcelUtil;
import com.easywecom.wecom.login.util.LoginTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/role")
@Api(tags = "角色信息")
public class SysRoleController extends BaseController {
    @Autowired
    private ISysRoleService roleService;

//    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    @ApiOperation("列表查询")
    public TableDataInfo<SysRole> list(SysRole role) {
        startPage();
        role.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @GetMapping("/export")
    @ApiOperation("导出")
    public AjaxResult export(SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        role.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
        return util.exportExcel(list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    @ApiOperation("根据角色编号获取详细信息")
    public AjaxResult<SysRole> getInfo(@PathVariable Long roleId) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        return AjaxResult.success(roleService.selectRoleById(loginUser.getCorpId(), roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增角色")
    public AjaxResult add(@Validated @RequestBody SysRole role) {
        role.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        role.setCreateBy(LoginTokenService.getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @ApiOperation("修改保存角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role) {
        role.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // 检查是否修改了默认角色的名字
        roleService.checkDefaultRoleEditName(role);
        role.setUpdateBy(LoginTokenService.getUsername());

        if (roleService.updateRole(role) > 0) {
            // 更新缓存用户权限
            LoginTokenService.refreshDataScope();
            return AjaxResult.success();
        }
        return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }


    @ApiOperation("修改保存数据权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        role.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.authDataScope(role));
    }

    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }


    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(roleService.deleteRoleByIds(corpId, roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation("获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult<List<SysRole>> optionselect() {
        return AjaxResult.success(roleService.selectRoleAll(LoginTokenService.getLoginUser().getCorpId()));
    }
}
