package com.easyink.wecom.domain.entity.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 离职客户群继承记录表实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:24
 */
@Data
@ApiModel("离职客户群继承记录表")
@Builder
public class WeResignedGroupTransferRecord {

    @TableField("record_id")
    @ApiModelProperty(value = "分配记录id ")
    private Long recordId;

    @TableField("chat_id")
    @ApiModelProperty(value = "群聊id ")
    private String chatId;

    @TableField("status")
    @ApiModelProperty(value = "接替状态,只有继承成功才会有值（true成功false失败) ")
    private Boolean status;

    @TableField("takeover_time")
    @ApiModelProperty(value = "接替时间 ")
    private Date takeoverTime;

    @TableField("remark")
    @ApiModelProperty(value = "失败原因 ")
    private String remark;
}
