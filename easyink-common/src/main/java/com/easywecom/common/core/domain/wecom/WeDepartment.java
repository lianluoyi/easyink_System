package com.easywecom.common.core.domain.wecom;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信组织架构相关对象 we_department
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_department")
@ApiModel("企业微信组织架构相关对象")
public class WeDepartment {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "主键id")
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "部门名称")
    @TableField("name")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 16, message = "部门名称长度已超出限制")
    @Pattern(regexp = "^[^\"\\\\:*?<>\\|]{0,16}$", message = "不能包含特殊字符\\:*?”<>｜")
    private String name;

    @ApiModelProperty(value = "父节点id")
    @TableField("parent_id")
    @NotNull(message = "父节点的id不可为空")
    private Long parentId;

    @ApiModelProperty(value = "父节点名称")
    @TableField(exist = false)
    private String mainDepartmentName;

    @ApiModelProperty(value = "子部门集合")
    @TableField(exist = false)
    private List<WeDepartment> children = new ArrayList<>();

    @ApiModelProperty(value = "是否有权限")
    @TableField(exist = false)
    private Boolean enable;

    @ApiModelProperty(value = "部门及下级部门总人数")
    @TableField(exist = false)
    private Integer totalUserCount;


}
