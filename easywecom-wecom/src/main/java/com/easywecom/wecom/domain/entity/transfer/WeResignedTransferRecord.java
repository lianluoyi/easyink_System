package com.easywecom.wecom.domain.entity.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 离职继承总记录实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:25
 */
@Data
@ApiModel("离职继承总记录实体")
@Builder
public class WeResignedTransferRecord {

    @TableField("id")
    @ApiModelProperty(value = "主键id ")
    private Long id;

    @TableField("corp_id")
    @ApiModelProperty(value = "企业id ")
    private String corpId;

    @TableField("handover_userid")
    @ApiModelProperty(value = "原跟进离职员工id ")
    private String handoverUserid;

    @TableField("takeover_userid")
    @ApiModelProperty(value = "接替员工id ")
    private String takeoverUserid;

    @TableField("dimission_time")
    @ApiModelProperty(value = "离职时间 ")
    private Date dimissionTime;

    @TableField("handover_username")
    @ApiModelProperty(value = "原跟进人用户名 ")
    private String handoverUsername;

    @TableField("takeover_username")
    @ApiModelProperty(value = "接替人名称 ")
    private String takeoverUsername;

    @TableField("handover_department_name")
    @ApiModelProperty(value = "原跟进人部门名称 ")
    private String handoverDepartmentName;

    @TableField("takeover_department_name")
    @ApiModelProperty(value = "接替人部门名称 ")
    private String takeoverDepartmentName;

    @TableField("transfer_time")
    @ApiModelProperty(value = "分配时间 ")
    private Date transferTime;

}
