package com.easywecom.common.service;

import com.easywecom.common.core.domain.TreeSelect;
import com.easywecom.common.core.domain.entity.SysDept;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.wecom.WeDepartment;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author admin
 */
public interface ISysDeptService {
    /**
     * 查询企微部门管理数据
     *
     * @param dept 部门信息
     * @param loginUser
     * @return 部门信息集合
     */
    List<WeDepartment> selectDeptList(WeDepartment dept, LoginUser loginUser);

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */

    List<WeDepartment> buildDeptTree(List<WeDepartment> depts);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildDeptTreeSelect(List<WeDepartment> depts);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Integer> selectDeptListByRoleId(Long roleId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     *
     * @param dept 部门信息
     * @return 结果
     */
    String checkDeptNameUnique( SysDept dept);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int insertDept(SysDept dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int deleteDeptById(Long deptId);

    /**
     * 过滤部门数据权限，留下有权限的部门
     *
     * @param list      部门集合
     * @param loginUser 登录用户实体
     * @return 过滤后的部门集合
     */
    List<WeDepartment> filterDepartmentDataScope(List<WeDepartment> list, LoginUser loginUser);


    /**
     * 为部门设置权限标识
     *
     * @param list      部门集合
     * @param loginUser 登录用户实体
     * @return 返回所有部门，在每个部门中设置权限
     */
    List<WeDepartment> filterDataScope(List<WeDepartment> list, LoginUser loginUser);

}
