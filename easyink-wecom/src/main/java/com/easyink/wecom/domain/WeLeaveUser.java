package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 类名： WeLeaveUser
 *
 * @author 佚名
 * @date 2021/8/30 10:49
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("we_leave_user")
@ApiModel("员工离职表实体类")
public class WeLeaveUser {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "员工id", allowEmptyValue = true)
    @TableField("user_id")
    @Excel(name = "员工id")
    @NotBlank(message = "员工id不能为空")
    private String userId;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("头像")
    @TableField("head_image_url")
    private String headImageUrl;

    @ApiModelProperty("用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("用户昵称")
    @TableField("alias")
    private String alias;

    @ApiModelProperty("主部门名")
    @TableField("main_department_name")
    private String mainDepartmentName;

    @ApiModelProperty("主部门名")
    @TableField("main_department")
    private Long mainDepartment;

    @ApiModelProperty("离职是否分配")
    @TableField("is_allocate")
    private Integer isAllocate;

    @ApiModelProperty("员工离职时间")
    @TableField("dimission_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "离职时间不能为空")
    private Date dimissionTime;


    @ApiModelProperty("待分配客户数量")
    @TableField(exist = false)
    private Integer allocateCustomerNum;
    @ApiModelProperty("待分配群聊数量")
    @TableField(exist = false)
    private Integer allocateGroupNum;


}
