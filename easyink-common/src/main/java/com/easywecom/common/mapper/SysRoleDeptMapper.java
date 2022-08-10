package com.easywecom.common.mapper;

import com.easywecom.common.core.domain.system.SysRoleDept;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色与部门关联表 数据层
 *
 * @author admin
 */

@Repository
public interface SysRoleDeptMapper {
    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteRoleDept(Long[] ids);

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 批量新增角色部门信息
     *
     * @param roleDeptList 角色部门列表
     * @return 结果
     */
    int batchRoleDept(List<SysRoleDept> roleDeptList);

    /**
     * 根据部门ID获取其当前部门和所有下级部门ID
     *
     * @param corpId       公司ID
     * @param departmentId 所属部门ID
     * @return
     */
    String getDeptAndChildDept(@Param("corpId") String corpId, @Param("departmentId") Long departmentId);


    /**
     * 根据角色id获取部门数组
     *
     * @param corpId 公司Id
     * @param roleId 角色Id
     * @return
     */
    List<Long> getDeptByRoleId(@Param("corpId") String corpId, @Param("roleId") Long roleId);

    /**
     * 根据企业id获取所有的部门id集合
     *
     * @param corpId 企业id
     * @return 部门id 集合
     */
    List<String> getAllDeptList(String corpId);
}
