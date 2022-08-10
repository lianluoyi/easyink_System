package com.easyink.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.annotation.Excel;
import com.easyink.common.annotation.Excel.ColumnType;
import com.easyink.common.annotation.Excel.Type;
import com.easyink.common.annotation.Excels;
import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author admin
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 用户账号
     */
    @ApiModelProperty("用户账号")
    @Excel(name = "登录名称")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    @Excel(name = "用户名称")
    private String nickName;

    /**
     * 用户类型（00系统用户）(11:企业微信用户)
     */
    @ApiModelProperty("用户类型（00系统用户）(11:企业微信用户)")
    private String userType;

    /**
     * 当前微信用户对应微信端的id
     */
    @ApiModelProperty("当前微信用户对应微信端的id")
    private String weUserId;

    /**
     * 用户邮箱
     */
    @ApiModelProperty("用户邮箱")
    @Excel(name = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    @ApiModelProperty("手机号码")
    private String phonenumber;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    @ApiModelProperty("用户性别")
    private String sex;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户头像")
    private String avatar;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 盐加密
     */
    @ApiModelProperty("盐加密")
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty("帐号状态（0正常 1停用）")
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty("帐号状态（0正常 1停用）")
    private String delFlag;


    @ApiModelProperty("最后登录IP")
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @ApiModelProperty("最后登录时间")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /**
     * 部门对象
     */
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    @ApiModelProperty("部门对象")
    private SysDept dept;

    /**
     * 角色对象
     */
    @ApiModelProperty("角色对象")
    private List<SysRole> roles;

    /**
     * 角色组
     */
    @ApiModelProperty("角色组")
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @ApiModelProperty("岗位组")
    private Long[] postIds;

    /**
     * 是否具有有效得cropId
     */
    @ApiModelProperty("是否具有有效得cropId")
    private boolean validCropId = false;

    @ApiModelProperty("企业账号")
    private WeCorpAccount weCorpAccount;

    /**
     * 公司ID
     */
    @ApiModelProperty("公司ID")
    private String corpId;


    @TableField("ui_color")
    @ApiModelProperty(value = "主题颜色")
    private String uiColor;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonIgnore
    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getUiColor() {
        return uiColor;
    }

    public void setUiColor(String uiColor) {
        this.uiColor = uiColor;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("phonenumber", getPhonenumber())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("salt", getSalt())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("loginIp", getLoginIp())
                .append("loginDate", getLoginDate())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("dept", getDept())
                .toString();
    }

    /**
     * 重写eq方法，若两个user corpId和userId相同,则相等
     *
     * @param obj sysUser
     * @return corpId和userId相等则返回true
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SysUser) {
            SysUser sysUser = (SysUser) obj;
            return this.corpId.equals(sysUser.getCorpId()) && this.userId.equals(sysUser.getUserId());
        }
        return false;
    }

}
