package com.easyink.wecom.domain.entity.moment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 类名： 朋友圈客户员工关联表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Data
@TableName("we_moment_user_customer_rel")
@ApiModel("朋友圈客户员工关联表实体")
@NoArgsConstructor
public class WeMomentUserCustomerRelEntity {

    @ApiModelProperty(value = "朋友圈任务id")
    @TableField("moment_task_id")
    private Long momentTaskId;

    @ApiModelProperty(value = "员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "客户id")
    @TableField("external_userid")
    private String externalUserid;

    public WeMomentUserCustomerRelEntity(Long momentTaskId, String externalUserid, String userId) {
        this.momentTaskId = momentTaskId;
        this.userId = userId;
        this.externalUserid = externalUserid;
    }
}
