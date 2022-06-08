package com.easywecom.common.mapper;

import com.easywecom.common.core.domain.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author admin
 */

@Repository
public interface SysRoleMapper {
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
     * @param corpId 公司ID
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolePermissionByUserId(@Param("corpId") String corpId, @Param("userId") String userId);

    /**
     * 根据企微userId 查询角色
     *
     * @param corpId 企业ID
     * @param userId 用户ID
     * @return
     */
    SysRole selectRoleByWeUserId(@Param("corpId") String corpId,@Param("userId") String userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRole> selectRoleAll();


    /**
     * 通过公司ID和角色ID查询角色
     *
     *
     * @param corpId 公司ID
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRole selectByCorpAndRoleId(@Param("corpId") String corpId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色
     * @return 角色信息
     */
    SysRole checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @return 角色信息
     */
    SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(SysRole role);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SysRole role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    int deleteRoleByIds(Long[] roleIds);
}
