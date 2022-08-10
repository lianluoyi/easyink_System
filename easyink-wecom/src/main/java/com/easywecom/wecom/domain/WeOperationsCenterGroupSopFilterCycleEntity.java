package com.easywecom.wecom.domain;

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
@TableName("we_operations_center_group_sop_filter_cycle")
@ApiModel("群SOP筛选群聊条件实体")
public class WeOperationsCenterGroupSopFilterCycleEntity {
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
    @ApiModelProperty(value = "企业ID", hidden = true)
    @TableField("corp_id")
    private String corpId;
    /**
     * we_operations_center_sop主键ID
     */
    @ApiModelProperty(value = "we_operations_center_sop主键ID")
    @TableField("sop_id")
    private Long sopId;

    @ApiModelProperty(value = "循环SOP的开始时间", required = true)
    @TableField("cycle_start")
    private String cycleStart;

    @ApiModelProperty(value = "循环SOP的结束时间", required = true)
    @TableField("cycle_end")
    private String cycleEnd;

}
