package com.easyink.wecom.domain.entity.moment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名： 朋友圈任务附件关联表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Data
@TableName("we_moment_detail_rel")
@ApiModel("朋友圈任务附件关联表实体")
public class WeMomentDetailRelEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 朋友圈任务id
     */
    @ApiModelProperty(value = "朋友圈任务id")
    @TableField("moment_task_id")
    private Long momentTaskId;
    /**
     * 附件id
     */
    @ApiModelProperty(value = "附件id")
    @TableField("detail_id")
    private Long detailId;

}
