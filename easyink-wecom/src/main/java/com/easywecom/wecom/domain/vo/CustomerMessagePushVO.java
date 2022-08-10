package com.easywecom.wecom.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类名： 群发消息任务
 *
 * @author 佚名
 * @date 2021/10/18 14:19
 */
@Data
@ApiModel("群发消息任务VO CustomerMessagePushVO")
public class CustomerMessagePushVO {

    /**
     * 消息id
     */
    @ApiModelProperty("消息id")
    private Long messageId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("消息类型 0 图片消息 3文件 4 文本消息   5 链接消息   6 小程序消息 用逗号隔开")
    private String messageType;

    /**
     * 群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群
     * 0 发送给客户 1 发送给客户群
     */
    @ApiModelProperty("0 发送给客户 1 发送给客户群")
    private String chatType;

    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    private String content;

    /**
     * 群发类型 0 发给客户 1 发给客户群
     */
    @ApiModelProperty("群发类型 0 发给客户 1 发给客户群")
    private String pushType;

    /**
     * 消息范围 0 全部客户  1 指定客户
     */
    @ApiModelProperty("消息范围 0 全部客户  1 指定客户")
    private String pushRange;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String sender;

    @ApiModelProperty("创建人主部门名称")
    private String departmentName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("创建时间")
    private Date sendTime;

    /**
     * msgid 可以用于获取发送结果
     */
    @ApiModelProperty("msgid 可以用于获取发送结果")
    private String msgid;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    private String settingTime;

    /**
     * 预计发送消息数（客户对应多少人 客户群对应多个群）
     */
    @ApiModelProperty("预计发送消息数（客户对应多少人 客户群对应多个群）")
    private Integer expectSend;

    /**
     * 实际发送消息数（客户对应多少人 客户群对应多个群）
     */
    @ApiModelProperty("实际发送消息数（客户对应多少人 客户群对应多个群）")
    private Integer actualSend;

    /**
     * 是否定时任务 0 常规 1 定时发送
     */
    @ApiModelProperty("是否定时任务 0 常规 1 定时发送")
    private Integer timedTask;

    /**
     * 消息发送状态 0 未发送  1 已发送
     */
    @ApiModelProperty("消息发送状态 0 未发送  1 已发送")
    private String checkStatus;


    @ApiModelProperty("消息体")
    private List<WeCustomerSeedMessageVO> seedMessageList;

}
