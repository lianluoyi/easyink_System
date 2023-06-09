package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 老客户标签建群客户统计
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_pres_tag_group_stat")
public class WePresTagGroupTaskStat extends BaseEntity {

    /**
     * 老客户标签建群任务id
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 客户昵称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 客户ID
     */
    @TableField("external_userid")
    private String externalUserid;
    /**
     * 员工ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 是否已送达
     */
    @TableField("is_sent")
    private boolean isSent;

    /**
     * 是否已经在群
     */
    @TableField("is_in_group")
    private boolean isInGroup;
}
