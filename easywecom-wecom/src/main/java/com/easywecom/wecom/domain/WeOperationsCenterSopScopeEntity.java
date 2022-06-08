package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 类名： SOP作用范围
 *
 * @author 佚名
 * @date 2021-11-30 14:05:22
 */
@Data
@TableName("we_operations_center_sop_scope")
@ApiModel("SOP作用范围实体")
public class WeOperationsCenterSopScopeEntity {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;
    /**
     * we_operations_center_sop 主键ID
     */
    @ApiModelProperty(value = "we_operations_center_sop 主键ID")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 当为群sop时，为chatId;当为客户sop时，为userId
     */
    @ApiModelProperty(value = "当为群sop时，为chatId;当为客户sop时，为userId,活动sop为客户id")
    @TableField("target_id")
    private String targetId;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
