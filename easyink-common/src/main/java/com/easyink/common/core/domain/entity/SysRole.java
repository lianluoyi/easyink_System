package com.easyink.common.core.domain.entity;

import com.easyink.common.annotation.Excel;
import com.easyink.common.annotation.Excel.ColumnType;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 角色表 sys_role
 *
 * @author admin
 */
@Builder
@AllArgsConstructor
@ApiModel("角色表")
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @ApiModelProperty("角色ID")
    @Excel(name = "角色序号", cellType = ColumnType.NUMERIC)
    private Long roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @Size(max = 16, message = "角色名称长度已超出限制")
    @Excel(name = "角色名称")
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    /**
     * 角色权限
     */
    @ApiModelProperty("角色权限")
    @Excel(name = "角色权限")
    private String roleKey;

    /**
     * 角色排序
     */
    @ApiModelProperty("角色排序")
    @Excel(name = "角色排序")
    private String roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）
     */
    @ApiModelProperty("数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）")
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限")
    private String dataScope;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty("角色状态（0正常 1停用）")
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @ApiModelProperty("备注")
    @Size(max = 64, message = "角色备注长度已超出限制")
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @ApiModelProperty("用户是否存在此角色标识 默认不存在")
    private boolean flag = false;

    /**
     * 菜单组
     */
    @ApiModelProperty("菜单组")
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @ApiModelProperty("部门组（数据权限）")
    private Long[] deptIds;
    /**
     * 公司ID
     */
    @ApiModelProperty("公司ID")
    private String corpId;

    /**
     * 角色类型（1：系统默认超级管理员角色, 2:系统默认角色,3:自定义角色)
     */
    @ApiModelProperty("角色类型（1：系统默认超级管理员角色, 2:系统默认角色,3:自定义角色)")
    private Integer roleType;

    public SysRole() {

    }

    /**
     * 构造初始化角色实体 (用户初始化)
     *
     * @param corpId    公司ID
     * @param roleName  角色名
     * @param roleKey   角色KEY
     * @param createBy  创建者
     * @param dataScope 数据权限
     * @param roleType  角色类型
     */
    public SysRole(String corpId, String roleName, String roleKey, String createBy, String dataScope, Integer roleType) {
        this.corpId = corpId;
        this.roleName = roleName;
        this.remark = roleName;
        this.roleKey = roleKey;
        this.setCreateBy(createBy);
        this.setUpdateBy(createBy);
        //默认排序都为1
        this.roleSort = Constants.DEFAULT_SORT;
        this.dataScope = dataScope;
        this.status = Constants.NORMAL_CODE;
        this.delFlag = Constants.NORMAL_CODE;
        Date now = new Date();
        this.setCreateTime(now);
        this.setUpdateTime(now);
        this.roleType = roleType;
    }

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(String roleSort) {
        this.roleSort = roleSort;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Long[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }

    public Long[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(Long[] deptIds) {
        this.deptIds = deptIds;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }


    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getRoleId())
                .append("roleName", getRoleName())
                .append("roleKey", getRoleKey())
                .append("roleSort", getRoleSort())
                .append("dataScope", getDataScope())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("corpId",getCorpId())
                .toString();
    }
}
