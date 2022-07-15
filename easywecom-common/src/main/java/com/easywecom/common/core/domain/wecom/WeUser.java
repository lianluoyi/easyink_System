package com.easywecom.common.core.domain.wecom;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.config.jackson.StringArrayDeserialize;
import com.easywecom.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;


/**
 * 通讯录相关客户对象 we_user
 *
 * @author 佚名
 * @date 2021-7-29
 */
@ApiModel("通讯录相关客户对象")
@Data
@TableName("we_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不可为空")
    @TableId
    @TableField("user_id")
    @Size(max = 64, message = "员工账号长度已超过限制")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_@.\\-]+$", message = "账号应该由数字,字母和_-@.组成,第一个字符必须是数字或字母")
    private String userId;

    @ApiModelProperty(value = "头像地址")
    @TableField("head_image_url")
    private String avatarMediaid;

    @ApiModelProperty(value = "用户名称")
    @TableField("user_name")
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 64, message = "员工姓名长度已超出限制")
    private String name;

    @ApiModelProperty(value = "用户昵称")
    @TableField("alias")
    @Size(max = 64, message = "员工别名长度已超出限制")
    private String alias;

    @ApiModelProperty(value = "性别。1表示男性，2表示女性")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "手机号")
    @TableField("mobile")
    @Size(max = 20, message = "员工手机长度已超出限制")
    @Pattern(regexp = "\\d{0,20}", message = "手机号只能是数字")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    @Email(message = "邮箱格式有误")
    @Size(max = 200, message = "邮箱长度已超出限制")
    private String email;

    @ApiModelProperty(value = "个人微信号")
    @TableField("wx_account")
    private String wxAccount;

    @ApiModelProperty(value = "用户所属部门,使用逗号隔开,字符串格式存储")
    @TableField("department")
    private String[] department;

    @ApiModelProperty(value = "职务")
    @TableField("position")
    @Size(max = 64, message = "员工职务长度已超出限制")
    private String position;

    @ApiModelProperty(value = "1表示为上级,0表示普通成员(非上级)。")
    @TableField("is_leader_in_dept")
    private String[] isLeaderInDept;

    @ApiModelProperty(value = "入职时间")
    @TableField("join_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinTime;

    @ApiModelProperty(value = "是否启用(1表示启用成员，0表示禁用成员)")
    @TableField("enable")
    private Integer enable;

    @ApiModelProperty(value = "身份证号")
    @TableField("id_card")
    @Size(max = 30, message = "身份证长度已超出限制")
    private String idCard;

    @ApiModelProperty(value = "QQ号")
    @TableField("qq_account")
    @Size(max = 20, message = "QQ号长度已超出限制")
    @Pattern(regexp = "\\d{0,20}", message = "QQ号只能是数字")
    private String qqAccount;

    @ApiModelProperty(value = "座机")
    @TableField("telephone")
    private String telephone;

    @ApiModelProperty(value = "地址")
    @TableField("address")
    @Size(max = 128, message = "员工地址长度已超出限制")
    private String address;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "客户标签,字符串使用逗号隔开")
    @TableField("customer_tags")
    private String customerTags;

    @ApiModelProperty(value = "离职时间")
    @TableField("dimission_time")
    private Date dimissionTime;

    @ApiModelProperty(value = "离职是否分配(1:已分配;0:未分配;)")
    @TableField("is_allocate")
    private Integer isAllocate;

    @ApiModelProperty(value = "激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除")
    @TableField("is_activate")
    private Integer isActivate;

    @ApiModelProperty(value = "是否开启会话存档 0：关闭 1：开启")
    @TableField("isOpenChat")
    private Integer isOpenChat;

    @TableField(exist = false)
    private String departmentStr;

    @TableField(exist = false)
    private Long roleId;

    @TableField("main_department")
    @ApiModelProperty(value = "主部门")
    private Long mainDepartment;
    /**
     * 部门名字 多个用,隔开
     */
    @TableField(exist = false)
    private String departmentName;

    @TableField("corp_id")
    @ApiModelProperty(value = "公司ID")
    private String corpId;

    @TableField("ui_color")
    @ApiModelProperty(value = "主题颜色")
    private String uiColor;

    @TableField(exist = false)
    private String externalCorpId;

    @TableField(exist = false)
    private String externalUserId;

    public WeUser(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }


    @JsonDeserialize(using = StringArrayDeserialize.class)
    public void setDepartment(String[] department) {
        this.department = department;
    }

    @JsonDeserialize(using = StringArrayDeserialize.class)
    public void setIsLeaderInDept(String[] isLeaderInDept) {
        this.isLeaderInDept = isLeaderInDept;
    }


    @Override
    public int hashCode() {
        return Objects.hash( userId);
    }

    /**
     * 重写eq方法，若两个user corpId和userId相同,则相等
     * ( update: 由于从API获取到WeUser对象都没有corpId,且本系统处理的时候应该corpId都是一样的，所以这里改成只通过userId)
     * @param  o weUser对象
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeUser weUser = (WeUser) o;
        return userId.equals(weUser.userId) ;
    }

    /**
     * 是否是三方应用员工
     * 即userId与externalUserId一致
     *
     * @return 一致为true，否则为false
     */
    public boolean isExternalUser() {
        return StringUtils.equals(this.getUserId(), this.getExternalUserId());
    }

}