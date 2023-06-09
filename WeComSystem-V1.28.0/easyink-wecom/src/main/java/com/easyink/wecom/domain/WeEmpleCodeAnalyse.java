package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名：WeEmpleCodeAnalyse
 *
 * @author Society my sister Li
 * @date 2021-11-04 14:15
 */
@Data
@ApiModel("员工活码数据统计分析")
public class WeEmpleCodeAnalyse {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "员工活码主键ID")
    @TableField("emple_code_id")
    private Long empleCodeId;

    @ApiModelProperty(value = "企业成员userId")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "客户ID")
    @TableField("external_userid")
    private String externalUserId;

    @ApiModelProperty(value = "添加时间")
    @TableField("time")
    private Date time;

    @ApiModelProperty(value = "1:新增，0:流失")
    @TableField("type")
    private Boolean type;
}
