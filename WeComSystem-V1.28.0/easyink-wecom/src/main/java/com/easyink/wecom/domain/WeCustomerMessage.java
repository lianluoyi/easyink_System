package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 群发消息  微信消息表 we_customer_message
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer_message")
public class WeCustomerMessage extends BaseEntity {

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("message_id")
    private Long messageId;

    @ApiModelProperty(value = "原始数据表id")
    @TableField("original_id")
    private Long originalId;

    @ApiModelProperty(value = "群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群")
    @TableField("chat_type")
    private String chatType;

    @ApiModelProperty(value = "发送企业群发消息的成员userid，当类型为发送给客户群时必填(和企微客户沟通后确认是群主id)")
    @TableField("sender")
    private String sender;

    @ApiModelProperty(value = "消息发送状态 0 未发送  1 已发送")
    @TableField("check_status")
    private String checkStatus;

    @ApiModelProperty(value = "企业群发消息的id，可用于<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/92136\">获取群发消息发送结果</a>")
    @TableField("msgid")
    private String msgid;

    @ApiModelProperty(value = "消息内容")
    @TableField(value = "content", insertStrategy = FieldStrategy.NOT_NULL)
    private String content;

    @ApiModelProperty(value = "")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "发送时间")
    @TableField("setting_time")
    private String settingTime;

    @ApiModelProperty(value = "预计发送消息数（客户对应多少人 客户群对应多个群）")
    @TableField("expect_send")
    private Integer expectSend;

    @ApiModelProperty(value = "实际发送消息数（客户对应多少人 客户群对应多个群）")
    @TableField("actual_send")
    private Integer actualSend;

    @ApiModelProperty(value = "是否定时任务 0 常规 1 定时发送")
    @TableField("timed_task")
    private Integer timedTask;
}
