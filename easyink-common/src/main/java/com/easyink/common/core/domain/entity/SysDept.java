package com.easyink.common.core.domain.entity;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 *
 * @author admin
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel("部门实体")
public class SysDept extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("父部门ID")
    private Long parentId;

    @ApiModelProperty("祖级列表")
    private String ancestors;

    @ApiModelProperty("部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    private String deptName;

    @ApiModelProperty("显示顺序")
    @NotBlank(message = "显示顺序不能为空")
    private String orderNum;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;

    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @ApiModelProperty("部门状态:0正常,1停用")
    private String status;

    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty("父部门名称")
    private String parentName;

    @ApiModelProperty("子部门集合")
    private List<SysDept> children = new ArrayList<>();

}
