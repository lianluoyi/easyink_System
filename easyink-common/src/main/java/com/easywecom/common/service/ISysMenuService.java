package com.easywecom.common.service;

import com.easywecom.common.core.MenuTree;
import com.easywecom.common.core.domain.TreeSelect;
import com.easywecom.common.core.domain.entity.SysMenu;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.system.RouterVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author admin
 */
public interface ISysMenuService {
    /**
     * 根据用户查询系统菜单列表
     *
     * @param loginUser 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(LoginUser loginUser);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, Long userId);
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, LoginUser user);

    /**
     * 根据企微用户ID查询权限
     *
     * @param corpId 公司ID
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByWeUserId(String corpId, String userId);


    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Integer> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<SysMenu> buildMenuTreeForRoleManage(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 构建角色-菜单 前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelectForRole(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    String checkMenuNameUnique(SysMenu menu);

    /**
     * 根据缓存的登录用户实体选择菜单
     *
     * @param loginUser 登录用户信息
     * @return
     */
    List<SysMenu> selectMenuTreeByLoginUser(LoginUser loginUser);

    /**
     * 根据角色id 获取角色管理中的菜单树
     *
     * @param roleId    角色ID
     * @param loginUser 登录用户实体
     * @return 菜单树
     */
    MenuTree getRoleMenuTreeSelect(Long roleId, LoginUser loginUser);
}
