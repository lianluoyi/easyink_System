package com.easywecom.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.constant.UserConstants;
import com.easywecom.common.core.domain.entity.SysMenu;
import com.easywecom.common.core.domain.entity.SysRole;
import com.easywecom.common.core.domain.system.SysRoleDept;
import com.easywecom.common.core.domain.system.SysRoleMenu;
import com.easywecom.common.enums.BaseStatusEnum;
import com.easywecom.common.enums.DataScopeEnum;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.RoleTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.exception.wecom.WeComException;
import com.easywecom.common.mapper.*;
import com.easywecom.common.service.ISysRoleService;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色 业务层处理
 *
 * @author admin
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
//    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param corpId 公司ID
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(String corpId, String userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(corpId, userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @param corpId
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll(String corpId) {
        return SpringUtils.getAopProxy(this).selectRoleList(SysRole.builder().corpId(corpId).build());
    }

    /**
     * 通过角色ID查询角色
     *
     * @param corpId
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(String corpId, Long roleId) {
        SysRole role = roleMapper.selectByCorpAndRoleId(corpId, roleId);
        if (DataScopeEnum.CUSTOM.getCode().equals(role.getDataScope())) {
            role.setDeptIds(
                    roleDeptMapper.getDeptByRoleId(corpId, role.getRoleId()).toArray(new Long[]{})
            );
        }
        return role;
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleNameUnique(role);
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        SysRole checkRole = roleMapper.selectByCorpAndRoleId(role.getCorpId(), role.getRoleId());
        if (ObjectUtil.isNull(checkRole)) {
            throw new CustomException("不存在该角色");
        }
        if (RoleTypeEnum.SYS_ADMIN.getType().equals(checkRole.getRoleType())) {
            throw new CustomException("不允许修改超级管理员角色");
        }
    }

    /**
     * 判断是否是系统默认角色
     *
     * @param corpId
     * @param roleId 角色ID
     * @return TRUE or FALSE
     */
    @Override
    public boolean isDefaultRole(String corpId, Long roleId) {
        SysRole role = roleMapper.selectByCorpAndRoleId(corpId, roleId);
        return this.isDefaultRole(role);
    }

    @Override
    public boolean isDefaultRole(SysRole role) {
        if (ObjectUtil.isNull(role)) {
            return false;
        }
        return RoleTypeEnum.SYS_ADMIN.getType().equals(role.getRoleType())
                || RoleTypeEnum.SYS_DEFAULT.getType().equals(role.getRoleType());
    }

    @Override
    public void checkDefaultRoleEditName(SysRole editRole) {
        // 原角色信息
        SysRole originRole = roleMapper.selectByCorpAndRoleId(editRole.getCorpId(), editRole.getRoleId());
        // 判断是否是默认角色
        if (!isDefaultRole(originRole)) {
            return;
        }
        String originName = originRole.getRoleName();
        if (StringUtils.isBlank(originName)) {
            return;
        }
        // 判断是否修改了默认角色的角色名字
        if (!originName.equals(editRole.getRoleName())) {
            throw new WeComException("系统默认角色的名字不可修改");
        }
    }
    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 新增角色信息
        role.setStatus(BaseStatusEnum.CLOSE.getCode().toString());
        role.setMenuIds(Constants.INIT_MENU_LIST);
        roleMapper.insertRole(role);
        // 如果自定义数据范围则插入
        insertRoleDeptRelation(role);
        return insertRoleMenu(role);
    }

    /**
     * 插入角色和部门关系
     *
     * @param role 系统角色实体
     */
    public void insertRoleDeptRelation(SysRole role) {
        if(StringUtils.isBlank(role.getCorpId())) {
            return;
        }
        List<SysRoleDept> list = new ArrayList<>();
        if (role.getDeptIds() != null && role.getDeptIds().length > 0) {
            for (Long dept : role.getDeptIds()) {
                list.add(new SysRoleDept(role.getRoleId(), role.getCorpId(),dept));
            }
            roleDeptMapper.batchRoleDept(list);
        }

    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        //修改 部门的数据权限
        if (DataScopeEnum.CUSTOM.getCode().equals(role.getDataScope())
                && null != role.getDeptIds() ) {
            roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
            insertRoleDept(role);
        }
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     * @return 成功插入的数量
     */
    public int insertRoleMenu(SysRole role) {
        // 新增用户与角色管理
        List<SysRoleMenu> list = genRoleMenuList(role.getMenuIds(), role.getRoleId());
        // 绑定指定新增菜单的父菜单（防止有部分2级菜单在不存在于角色管理中没有角色绑定到)
        List<Long> parentMenuIds = sysMenuMapper.selectParentMenuList(role.getMenuIds()).stream().map(SysMenu::getParentId).collect(Collectors.toList());
        List<SysRoleMenu> parentList = genRoleMenuList(parentMenuIds.toArray(new Long[]{}), role.getRoleId());
        list.addAll(parentList);
        // 获取默认页面
        List<Long> defaultPageIds = sysMenuMapper.selectDefaultPage().stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        List<SysRoleMenu> defaultRoleMenuList = genRoleMenuList(defaultPageIds.toArray(new Long[]{}), role.getRoleId());
        list.addAll(defaultRoleMenuList);
        if (CollUtil.isNotEmpty(list)) {
            return roleMenuMapper.batchRoleMenu(list);
        }
        return BigDecimal.ONE.intValue();
    }

    /**
     * 生成角色-菜单 实体集合
     * @param menuIds
     * @return
     */
    private List<SysRoleMenu> genRoleMenuList(Long[] menuIds,Long roleId) {
        List<SysRoleMenu> list = new ArrayList<>();
        for (Long menuId : menuIds) {
            if (menuId == null) {
                continue;
            }
            //新增菜单
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            list.add(rm);
        }
        return list;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     * @return 成功插入的条数
     */
    public int insertRoleDept(SysRole role) {
        int rows = 1;
        if (ObjectUtil.isNull(role) || StringUtils.isBlank(role.getCorpId()) || StringUtils.isEmpty(role.getDeptIds())) {
            return rows;
        }
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            rd.setCorpId(role.getCorpId());
            list.add(rd);
        }
        if (CollUtil.isNotEmpty(list)) {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    /**
     * 批量删除角色信息
     *
     * @param corpId  公司ID
     * @param roleIds 需要删除的角色ID
     * @return 成功删除的条数
     */
    @Override
    public int deleteRoleByIds(String corpId, Long[] roleIds) {
        for (Long roleId : roleIds) {
            if (isDefaultRole(corpId, roleId)) {
                throw new CustomException("系统默认角色无法删除");
            }
            SysRole role = selectRoleById(corpId, roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new CustomException(ResultTip.TIP_ATTRIBUTED, String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        return roleMapper.deleteRoleByIds(corpId, roleIds);
    }
}
