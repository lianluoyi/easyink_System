package com.easywecom.common.service;

import com.easywecom.common.core.domain.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author admin
 */
public interface ISysRoleService {
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     *
     * @param corpId 企业ID
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(String corpId, String userId);

    /**
     * 查询所有角色
     *
     * @param corpId 公司ID
     * @return 角色列表
     */
    List<SysRole> selectRoleAll(String corpId);

    /**
     * 通过角色ID查询角色
     *
     * @param corpId 公司ID
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRole selectRoleById(String corpId, Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    String checkRoleNameUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 判断是否是系统默认角色
     *
     * @param corpId 公司ID
     * @param roleId 角色ID
     * @return TRUE or FALSE
     */
    boolean isDefaultRole(String corpId, Long roleId);

    /**
     * 判断是否是系统默认角色
     *
     * @param role 角色
     * @return TRUE or FALSE
     */
    boolean isDefaultRole(SysRole role);

    /**
     * 检查是否是默认角色且修改了默认角色的名字
     *
     * @param editRole 修改后的角色
     */
    void checkDefaultRoleEditName(SysRole editRole);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(SysRole role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int authDataScope(SysRole role);

    /**
     * 批量删除角色信息
     *
     *
     * @param corpId 公司ID
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    int deleteRoleByIds(String corpId, Long[] roleIds);
}
