package com.easyink.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.MenuTree;
import com.easyink.common.core.domain.TreeSelect;
import com.easyink.common.core.domain.entity.SysMenu;
import com.easyink.common.core.domain.entity.SysUser;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.system.MetaVo;
import com.easyink.common.core.domain.system.RouterVo;
import com.easyink.common.enums.BaseStatusEnum;
import com.easyink.common.mapper.SysMenuMapper;
import com.easyink.common.mapper.SysRoleMenuMapper;
import com.easyink.common.service.ISysMenuService;
import com.easyink.common.utils.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 *
 * @author admin
 */
@Slf4j
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final RuoYiConfig ruoYiConfig;

    @Autowired
    public SysMenuServiceImpl(@NotNull SysMenuMapper menuMapper, @NotNull SysRoleMenuMapper roleMenuMapper, @NotNull RuoYiConfig ruoYiConfig) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.ruoYiConfig = ruoYiConfig;
    }

    /**
     * 根据用户查询系统菜单列表
     *
     * @param loginUser 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(LoginUser loginUser) {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setVisible(BaseStatusEnum.CLOSE.getCode().toString());
        return menuMapper.selectMenuList(sysMenu);
    }


    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, LoginUser user) {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (user.isSuperAdmin()) {
            menuList = menuMapper.selectMenuList(menu);
        } else if (ObjectUtil.isNotNull(user.getWeUser())) {
            menu.getParams().put("userId", user.getWeUser().getUserId());
            menu.getParams().put("corpId",user.getCorpId());
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }


    /**
     * 根据企微用户ID查询权限
     *
     * @param corpId
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByWeUserId(String corpId, String userId) {
        List<String> perms = menuMapper.selectMenuPermsByWeUserId(corpId, userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }


    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Long roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    private static List<Long> frameMenuId = Lists.newArrayList(2188L, 2001L, 2229L, 2282L, 2312L, 2079L, 1L, 2L);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            // 如果是三方应用需要屏蔽一些菜单
            if (ruoYiConfig.isThirdServer()){
                if (isBanMenu(menu)) {
                    continue;
                }
            }
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
            router.setIsPage(UserConstants.TYPE_DIR.equals(menu.getMenuType()));
            router.setIsFrameMenu(frameMenuId.contains(menu.getMenuId()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && CollUtil.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMeunFrame(menu)) {
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(org.apache.commons.lang3.StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 过滤菜单 如果一个二级菜单没有子节点 或 只剩一个节点 且 该节点被屏蔽
     *
     * @param menu  菜单
     * @return
     */
    private boolean needFilterMenu(SysMenu menu) {
        if(!UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
            return false;
        }
        // 长度是1
        int one = 1;
        int firstIndex = 0;
        // 如果一个二级菜单没有子节点 或 只剩一个节点 且 该节点被屏蔽
        return menu.getChildren().isEmpty() || (menu.getChildren().size() == one && isBanMenu(menu.getChildren().get(firstIndex)));
    }

    /**
     * 判断是否是第三方应用服务且是被屏蔽的菜单
     *
     * @param menu 菜单实体
     * @return
     */
    private boolean isBanMenu(SysMenu menu) {
        if (menu == null || menu.getMenuId() == null) {
            return false;
        }
        try {
            return ListUtil.toList(UserConstants.THIRD_APP_BAN_MENU_IDS.split(",")).contains(String.valueOf(menu.getMenuId()));
        } catch (Exception e) {
            log.info("解析第三方应用屏蔽菜单失败,e:{}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        Iterator<SysMenu> iterator = menus.iterator();
            while (iterator.hasNext()){
                SysMenu t = iterator.next();
                // 根据传入的某个父节点ID,遍历该父节点的所有子节点
                if (t.getParentId() == 0) {
                    recursionFn(menus, t);
                    returnList.add(t);
                }
            }

        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }


    /**
     * 构建角色管理所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTreeForRoleManage(List<SysMenu> menus) {
        List<SysMenu> roleReturnList = new ArrayList<>();
        Iterator<SysMenu> iterator = menus.iterator();
        while (iterator.hasNext()) {
            SysMenu t = iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (UserConstants.ROOT_MENU_PARENT_ID.equals(t.getParentId().toString())) {
                recursionFnForRoleManage(menus, t);
                roleReturnList.add(t);
            }
        }
        if (roleReturnList.isEmpty()) {
            roleReturnList = menus;
        }
        return roleReturnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }


    /**
     * 构建角色-菜单 前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelectForRole(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTreeForRoleManage(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insertMenu(menu);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateMenu(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据缓存的登录用户实体选择菜单
     *
     * @param loginUser 登录用户信息
     * @return
     */
    @Override
    public List<SysMenu> selectMenuTreeByLoginUser(LoginUser loginUser) {
        List<SysMenu> menus = null;
        if (loginUser.isSuperAdmin()) {
            menus = menuMapper.selectMenuTreeAll();
        } else if (StringUtils.isNotNull(loginUser.getWeUser())) {
            menus = menuMapper.selectMenuTreeByWeUserId(loginUser.getCorpId(), loginUser.getWeUser().getUserId());
        }
        return getChildPerms(menus, 0);
    }

    @Override
    public MenuTree getRoleMenuTreeSelect(Long roleId, LoginUser loginUser) {
        List<SysMenu> menus = this.selectMenuList(loginUser);
        List<Integer> checkedKeys = this.selectMenuListByRoleId(roleId);
        List<TreeSelect> menusTreeList = this.buildMenuTreeSelectForRole(menus);
        return new MenuTree(checkedKeys, menusTreeList);
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = org.apache.commons.lang3.StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMeunFrame(menu)) {
            routerName = org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String symbol ="/";
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = symbol + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMeunFrame(menu)) {
            routerPath = symbol;
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMeunFrame(menu)) {
            component = menu.getComponent();
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMeunFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext(); ) {
            SysMenu t = iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysMenu> it = childList.iterator();
                while (it.hasNext()) {
                    SysMenu n = it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 角色管理-递归获取菜单树
     *
     * @param list 菜单结合
     * @param t    菜单对象
     */
    private void recursionFnForRoleManage(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildListForRole(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysMenu> it = childList.iterator();
                while (it.hasNext()) {
                    SysMenu n = it.next();
                    recursionFnForRoleManage(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 角色管理- 获取子节点列表
     *
     * @param list 菜单集合
     * @param t    菜单
     * @return 子菜单集合
     */
    private List<SysMenu> getChildListForRole(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                // 如果子菜单 是目录菜单则直接再去下一级的菜单
                if (UserConstants.TYPE_DIR.equals(n.getMenuType())) {
                    tlist.addAll(getChildListForRole(list, n));
                } else {
                    tlist.add(n);
                }
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return CollUtil.isNotEmpty(getChildList(list, t));
    }
}
