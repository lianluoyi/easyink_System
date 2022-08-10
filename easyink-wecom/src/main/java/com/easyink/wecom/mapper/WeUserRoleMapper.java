package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.wecom.domain.WeUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企微用户-角色关系持久层接口
 *
 * @author : silver_chariot
 * @date : 2021/8/18 15:37
 */
@Repository
@Mapper
public interface WeUserRoleMapper extends BaseMapper<WeUserRole> {
    /**
     * 批量插入用户-角色关系
     *
     * @param list 实体集合
     * @return
     */
    Integer batchInsertUserRole(@Param("list") List<WeUserRole> list);

    /**
     * 批量插入 角色
     *
     * @param initRoleList 角色实体集合
     * @return 插入成功数量
     */
    Integer batchInsertRole(@Param("list") List<SysRole> initRoleList);

    /**
     * 插入角色
     *
     * @param role 角色实体
     * @return 成功数量
     */
    Integer insertRole(SysRole role);

    /**
     * 根据corpId和roleKey查询角色
     *
     * @param role 角色实体
     * @return
     */
    SysRole selectRoleByCorpIdAndRoleKey(SysRole role);

    /**
     * 批量插入角色菜单信息
     *
     * @param roleId  角色id
     * @param menuArr 菜单集合
     * @return
     */
    Integer insertRoleMenu(@Param("roleId") Long roleId, @Param("array") String[] menuArr);

    /**
     * 插入用户-角色关系
     *
     * @param weUserRole
     * @return
     */
    Integer insertUserRole(WeUserRole weUserRole);

    /**
     * 根据角色id查询角色
     *
     * @param corpId 企业id
     * @param roleId 角色id
     * @return {@link SysRole}
     */
    SysRole selectByRoleId(@Param("corpId") String corpId,@Param("roleId") Long roleId);

    /**
     * 批量新增或更新员工角色
     *
     * @param list list
     * @return Integer
     */
    Integer batchInsertOrUpdateUserRole(List<WeUserRole> list);
}
