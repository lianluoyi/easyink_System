package com.easyink.common.mapper;

import org.springframework.stereotype.Repository;

/**
 * 用户与角色关联表 数据层
 *
 * @author admin
 */

@Repository
public interface SysUserRoleMapper {
    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);
}
