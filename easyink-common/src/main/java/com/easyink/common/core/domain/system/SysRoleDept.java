package com.easyink.common.core.domain.system;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 角色和部门关联 sys_role_dept
 *
 * @author admin
 */
public class SysRoleDept {
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 公司id
     */
    private String corpId;

    /**
     * 部门ID
     */
    private Long deptId;

    public SysRoleDept() {
    }

    public SysRoleDept(Long roleId, Long deptId) {
        this.roleId = roleId;
        this.deptId = deptId;
    }

    public SysRoleDept(Long roleId,String corpId, Long deptId) {
        this.roleId = roleId;
        this.corpId = corpId;
        this.deptId = deptId;
    }


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getRoleId())
                .append("deptId", getDeptId())
                .toString();
    }
}
