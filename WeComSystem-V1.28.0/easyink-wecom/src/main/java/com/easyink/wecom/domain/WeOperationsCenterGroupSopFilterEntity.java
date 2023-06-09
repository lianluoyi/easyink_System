package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名： 群SOP筛选群聊条件
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_group_sop_filter")
@ApiModel("群SOP筛选群聊条件实体")
public class WeOperationsCenterGroupSopFilterEntity {
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
     * we_operations_center_sop主键ID
     */
    @ApiModelProperty(value = "we_operations_center_sop主键ID")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 群主( 多个逗号隔开)
     */
    @ApiModelProperty(value = "群主( 多个逗号隔开)")
    @TableField("owner")
    private String owner;
    /**
     * 群标签ID（多个逗号隔开）
     */
    @ApiModelProperty(value = "群标签ID（多个逗号隔开）")
    @TableField("tag_id")
    private String tagId;
    /**
     * 群创建时间范围
     */
    @ApiModelProperty(value = "群创建时间范围")
    @TableField("create_time")
    private String createTime;
    /**
     * 群创建时间
     */
    @ApiModelProperty(value = "群创建时间")
    @TableField("end_time")
    private String endTime;

}
