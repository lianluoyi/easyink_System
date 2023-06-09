package com.easyink.wecom.domain.entity.redeemcode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


/**
 * ClassName： WeRedeemCode
 *
 * @author wx
 * @date 2022/7/5 16:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_redeem_code")
@ApiModel("兑换码库存")
public class WeRedeemCode extends BaseEntity {

    @ApiModelProperty(value = "code, 主键id", hidden = true)
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "activityId, 主键id", hidden = true)
    @TableField("activity_id")
    private String activityId;

    @ApiModelProperty(value = "兑换码领取状态, 0:未领取 1:已领取")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "有效期")
    @TableField("effective_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String effectiveTime;

    @ApiModelProperty(value = "兑换时间，发送给客户的时间")
    @TableField("redeem_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String redeemTime;

    @ApiModelProperty(value = "领取人userId")
    @TableField("receive_user_id")
    private String receiveUserId;

    @ApiModelProperty(value = "corpId", hidden = true)
    @TableField(exist = false)
    private String corpId;
}
