package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 类名： 客户SOP筛选条件
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_customer_sop_filter")
@ApiModel("客户SOP筛选条件实体")
public class WeOperationsCenterCustomerSopFilterEntity {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性")
    @TableField("gender")
    private Integer gender;

    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;
    /**
     * we_operations_center_sop主键ID
     */
    @ApiModelProperty(value = "we_operations_center_sop主键ID")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 所属员工（多个逗号隔开 ）
     */
    @ApiModelProperty(value = "所属员工（多个逗号隔开 ）")
    @TableField("users")
    private String users;
    /**
     * 标签ID（多个逗号隔开 ）
     */
    @ApiModelProperty(value = "标签ID（多个逗号隔开 ）")
    @TableField("tag_id")
    private String tagId;
    /**
     * 客户属性名和值，json存储
     */
    @ApiModelProperty(value = "客户属性名和值，json存储")
    @TableField("cloumn_info")
    private String cloumnInfo;

    /**
     * 标签ID(多个逗号隔开)
     */
    @ApiModelProperty(value = "标签ID(多个逗号隔开) ")
    @TableField("filter_tag_id")
    private String filterTagId;

    @ApiModelProperty("客户添加开始时间")
    @TableField("start_time")
    private Date startTime;
    @ApiModelProperty("客户添加截止时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * 所属部门（多个逗号隔开 ）
     */
    @ApiModelProperty(value = "所属部门（多个逗号隔开 ）")
    @TableField("departments")
    private String departments;
}
