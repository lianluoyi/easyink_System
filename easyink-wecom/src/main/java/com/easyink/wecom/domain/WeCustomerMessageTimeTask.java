package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 群发消息定时任务表
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@NoArgsConstructor
@TableName("we_customer_messageTimeTask")
public class WeCustomerMessageTimeTask extends BaseEntity {

    public WeCustomerMessageTimeTask(Long messageId, CustomerMessagePushDTO messageInfo, List<WeCustomer> customersInfo, Long settingTime) {
        this.messageId = messageId;
        this.messageInfo = messageInfo;
        this.customersInfo = customersInfo;
        this.settingTime = settingTime;
    }

    @ApiModelProperty(value = "任务id")
    @TableId(type = IdType.AUTO)
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "消息id")
    @TableField("message_id")
    private Long messageId;

    @ApiModelProperty(value = "消息原始信息")
    @TableField(value = "message_info")
    private CustomerMessagePushDTO messageInfo;

    @ApiModelProperty(value = "客户信息列表")
    @TableField(value = "customers_info")
    private List<WeCustomer> customersInfo;

    @ApiModelProperty(value = "客户群组信息列表")
    @TableField(value = "groups_info")
    private List<WeGroup> groupsInfo;

    @ApiModelProperty(value = "定时时间的毫秒数")
    @TableField("setting_time")
    private Long settingTime;

    @ApiModelProperty(value = "0 未解决 1 已解决")
    @TableField("solved")
    private Integer solved;

    @ApiModelProperty(value = "0 未删除 1 已删除")
    @TableField("del_flag")
    private Integer delFlag;

}
