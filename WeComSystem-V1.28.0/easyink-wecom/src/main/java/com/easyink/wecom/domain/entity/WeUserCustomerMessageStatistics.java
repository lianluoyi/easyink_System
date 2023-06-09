package com.easyink.wecom.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）(WeUserCustomerMessageStatistics)实体类
 *
 * @author wx
 * @since 2023-02-13 09:32:51
 */
@Data
@NoArgsConstructor
public class WeUserCustomerMessageStatistics implements Serializable {
    private static final long serialVersionUID = 317694325649188257L;
    /**
     * 主键id
     */
    private Long id = SnowFlakeUtil.nextId();
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 员工id 会话存档 ES中员工
     */
    private String userId;
    /**
     * 客户id 会话存档 ES中与user_id对话的客户
     */
    private String externalUserid;
    /**
     * 员工发送消息数量 会话存档 ES 中 user_id对external_userid 发送的消息数
     */
    private Integer userSendMessageCnt;
    /**
     * 客户发送消息数量 会话存档 ES 中 external_userid对user_id发送的消息数
     */
    private Integer externalUserSendMessageCnt;
    /**
     * 添加客户时间 user_id与external_userId成为联系人的时间 we_flower_customer_rel 表中查找
     */
    private Date addTime;
    /**
     * 发送消息时间 统计的时间，当天
     */
    private Date sendTime;
    /**
     * 当天收到客户消息到首次回复客户时间间隔（单位分钟） ES中查询并计算
     */
    private Long firstReplyTimeIntervalAlterReceive;
    /**
     * 是否有三个轮次对话，1：有，0：无 ES中查询并统计
     */
    private Boolean threeRoundsDialogueFlag;
    /**
     * 对话是否由员工主动发起，1：是，0：否 ES中查询并统计
     */
    private Boolean userActiveDialogue;

    @ApiModelProperty(value = "当天员工首次给客户发消息，客户在30分钟内回复")
    @TableField(exist = false)
    private Boolean repliedWithinThirtyMinCustomerFlag;

    @ApiModelProperty(value = "当天员工主动发起会话次数")
    private Integer userActiveChatCnt;
}

