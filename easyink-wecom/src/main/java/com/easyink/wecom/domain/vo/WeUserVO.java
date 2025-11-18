package com.easyink.wecom.domain.vo;

import com.easyink.common.annotation.EncryptField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 员工信息返回
 *
 * @author 佚名
 * @date 2021/8/24 11:21
 */
@Data
@ApiModel("企微成员回参数据")
public class WeUserVO {

    @ApiModelProperty(value = "员工id")
    private String userId;

    @ApiModelProperty(value = "头像地址")
    private String headImageUrl;


    @ApiModelProperty(value = "用户名称")
    private String userName;


    @ApiModelProperty(value = "用户昵称")
    private String alias;


    @ApiModelProperty(value = "性别。1表示男性，2表示女性")
    private Integer gender;


    @ApiModelProperty(value = "手机号")
    @EncryptField(EncryptField.FieldType.MOBILE)
    private String mobile;
    private String mobileEncrypt;


    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户所属部门id")
    private String department;


    @ApiModelProperty(value = "职务")
    private String position;

    @ApiModelProperty(value = "入职时间")
    private Date joinTime;

    @ApiModelProperty(value = "是否启用(1表示启用成员，0表示禁用成员)")
    private Integer enable;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "QQ号")
    private String qqAccount;

    @ApiModelProperty(value = "地址")
    @EncryptField(EncryptField.FieldType.ADDRESS)
    private String address;
    private String addressEncrypt;


    @ApiModelProperty(value = "生日")
    private Date birthday;


    @ApiModelProperty(value = "激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除")
    private Integer isActivate;


    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "主部门id")
    private Long mainDepartment;

    @ApiModelProperty(value = "主部门名")
    private String mainDepartmentName;
}
