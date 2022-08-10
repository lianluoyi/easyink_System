package com.easyink.wecom.domain.entity.moment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 类名：
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Data
@TableName("we_moment_task_result")
@ApiModel("实体")
public class WeMomentTaskResultEntity {
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

    @ApiModelProperty(value = "发布时间")
    @TableField("publish_time")
    private Date publishTime;
    /**
     * 员工id
     */
    @ApiModelProperty(value = "员工id")
    @TableField("user_id")
    private String userId;
    /**
     * 成员发表状态。0:未发表 1：已发表
     */
    @ApiModelProperty(value = "成员发表状态。0:未发表 1：已发表")
    @TableField("publish_status")
    private Integer publishStatus;

    @ApiModelProperty(value = "失败备注")
    @TableField("remark")
    private String remark;

}
