package com.easyink.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.domain.TreeSelect;
import com.easyink.common.core.domain.entity.SysDept;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.exception.CustomException;
import com.easyink.common.mapper.SysDeptMapper;
import com.easyink.common.service.ISysDeptService;
import com.easyink.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author admin
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService {
    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 查询部门管理数据
     *
     * @param dept      部门信息
     * @param loginUser
     * @return 部门信息集合
     */
    @Override
    public List<WeDepartment> selectDeptList(WeDepartment dept, LoginUser loginUser) {
        List<WeDepartment> list = deptMapper.selectDeptList(dept);
        return filterDataScope(list, loginUser);
    }

    /**
     * 判断是否有该部门的可视权限
     *
     * @param deptScope 数据部门权限范围
     * @param id        部门id
     * @return true or false
     */
    public boolean inDeptScope(List<String> deptScope, Long id) {
        return CollectionUtil.isNotEmpty(deptScope) && deptScope.contains(id.toString());
    }


    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<WeDepartment> buildDeptTree(List<WeDepartment> depts) {
        List<WeDepartment> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (WeDepartment dept : depts) {
            tempList.add(dept.getId());
        }
        for (Iterator<WeDepartment> iterator = depts.iterator(); iterator.hasNext(); ) {
            WeDepartment dept = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }


    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<WeDepartment> depts) {
        List<WeDepartment> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Integer> selectDeptListByRoleId(Long roleId) {
        return deptMapper.selectDeptListByRoleId(roleId);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept) {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new CustomException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept) {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatus(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(SysDept dept) {
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (CollUtil.isNotEmpty(children)) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 递归获取所有子部门
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<WeDepartment> list, WeDepartment t) {
        // 得到子节点列表
        List<WeDepartment> childList = getChildList(list, t);
        t.setChildren(childList);
        for (WeDepartment tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<WeDepartment> it = childList.iterator();
                while (it.hasNext()) {
                    WeDepartment n = it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     *
     * @param list 集合
     * @param t    部门
     * @return
     */
    private List<WeDepartment> getChildList(List<WeDepartment> list, WeDepartment t) {
        List<WeDepartment> tlist = new ArrayList<>();
        Iterator<WeDepartment> it = list.iterator();
        while (it.hasNext()) {
            WeDepartment n = it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     *
     * @param list
     * @param t
     * @return
     */
    private boolean hasChild(List<WeDepartment> list, WeDepartment t) {
        return CollUtil.isNotEmpty(getChildList(list, t));
    }

    @Override
    public List<WeDepartment> filterDataScope(List<WeDepartment> list, LoginUser loginUser) {
        String deptScope = loginUser.getDepartmentDataScope();
        if (StringUtils.isBlank(deptScope)) {
            return list;
        }
        List<String> deptScopeList = Arrays.asList(deptScope.split(","));
        list.forEach(department -> department.setEnable(inDeptScope(deptScopeList, department.getId())));
        return list;
    }

    /**
     * 过滤部门数据权限范围
     *
     * @param list      部门集合
     * @param loginUser 登录用户实体
     * @return 过滤后的部门集合
     */
    @Override
    public List<WeDepartment> filterDepartmentDataScope(List<WeDepartment> list, LoginUser loginUser) {
        String deptScope = loginUser.getDepartmentDataScope();
        if (StringUtils.isBlank(deptScope)) {
            return list;
        }
        List<String> deptScopeList = Arrays.asList(deptScope.split(","));
        return list.stream().filter(item -> inDeptScope(deptScopeList, item.getId())).collect(Collectors.toList());
    }
}
