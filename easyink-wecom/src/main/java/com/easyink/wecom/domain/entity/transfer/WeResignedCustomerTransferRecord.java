package com.easyink.wecom.domain.entity.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 离职客户继承记录
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:23
 */
@Data
@ApiModel("离职客户继承记录表")
@Builder
public class WeResignedCustomerTransferRecord {
    @TableField("record_id")
    @ApiModelProperty(value = "分配记录id ")
    private Long recordId;

    @TableField("external_userid")
    @ApiModelProperty(value = "外部联系人userId ")
    private String externalUserid;

    @TableField("status")
    @ApiModelProperty(value = "接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 ")
    private Integer status;

    @TableField("takeover_time")
    @ApiModelProperty(value = "接替时间 ")
    private Date takeoverTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注 ")
    private String remark;

    @TableField(exist = false)
    private String corpId;

    @TableField(exist = false)
    private String handoverUserid;

    @TableField(exist = false)
    private String takeoverUserid;
}
