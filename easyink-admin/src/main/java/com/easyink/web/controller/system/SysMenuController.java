package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.MenuTree;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.TreeSelect;
import com.easyink.common.core.domain.entity.SysMenu;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.service.ISysMenuService;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.login.util.LoginTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/menu")
@Api(tags = "菜单信息")
public class SysMenuController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private TokenService tokenService;
    private static final String ERROR_INFO = "新增菜单'";

    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    @ApiOperation("获取菜单列表")
    public AjaxResult<List<SysMenu>> list(SysMenu menu) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuList(menu, loginUser);
        return AjaxResult.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    @ApiOperation("根据菜单编号获取详细信息")
    public AjaxResult<SysMenu> getInfo(@PathVariable Long menuId) {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }


    @GetMapping("/treeselect")
    @ApiOperation("获取菜单下拉树列表")
    public AjaxResult<List<TreeSelect>> treeselect(SysMenu menu) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuList(menu, loginUser);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    @ApiOperation("加载对应角色菜单列表树")
    public AjaxResult<MenuTree> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        MenuTree roleMenuTree = menuService.getRoleMenuTreeSelect(roleId,loginUser);
        return AjaxResult.success(roleMenuTree);
    }


    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增菜单")
    public AjaxResult<Integer> add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error(ERROR_INFO + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return AjaxResult.error(ERROR_INFO + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(LoginTokenService.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }


    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改菜单")
    public AjaxResult<Integer> edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return AjaxResult.error(ERROR_INFO + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return AjaxResult.error(ERROR_INFO + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }


    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    @ApiOperation("删除菜单")
    public AjaxResult<Integer> remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return AjaxResult.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}