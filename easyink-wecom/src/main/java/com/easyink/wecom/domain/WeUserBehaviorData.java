package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 联系客户统计数据 对象 we_user_behavior_data
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("we_user_behavior_data")
public class WeUserBehaviorData implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "授权企业id")
    @TableField("corp_id")
    @Excel(name = "授权企业id")
    private String corpId;

    @ApiModelProperty(value = "客户id")
    @TableField("user_id")
    @Excel(name = "客户id")
    private String userId;

    @ApiModelProperty(value = "员工当前部门ID（快照，记录统计时员工所属的当前部门）")
    @TableField("current_department_id")
    private String currentDepartmentId;

    @ApiModelProperty(value = "员工当前部门名称（快照，记录统计时的部门名称）")
    @TableField("current_department_name")
    private String currentDepartmentName;

    @ApiModelProperty(value = "完整上级部门路径ID（快照，如：1,2,3 表示从根部门到当前部门的完整路径）")
    @TableField("parent_department_id")
    private String parentDepartmentId;

    @ApiModelProperty(value = "完整上级部门路径名称（快照，如：总公司/销售部/华南区 表示完整部门路径）")
    @TableField("parent_department_name")
    private String parentDepartmentName;

    @ApiModelProperty(value = "数据日期，为当日0点的时间戳")
    @TableField("stat_time")
    @Excel(name = "数据日期，为当日0点的时间戳", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statTime;

    @ApiModelProperty(value = "发起申请数")
    @TableField("new_apply_cnt")
    @Excel(name = "发起申请数")
    private Integer newApplyCnt;

    @ApiModelProperty(value = "新增客户数，成员新添加的客户数量")
    @TableField("new_contact_cnt")
    @Excel(name = "新增客户数，成员新添加的客户数量")
    private Integer newContactCnt;

    @ApiModelProperty(value = "聊天总数， 成员有主动发送过消息的单聊总数")
    @TableField("chat_cnt")
    @Excel(name = "聊天总数， 成员有主动发送过消息的单聊总数")
    private Integer chatCnt;

    @ApiModelProperty(value = "发送消息数，成员在单聊中发送的消息总数")
    @TableField("message_cnt")
    @Excel(name = "发送消息数，成员在单聊中发送的消息总数")
    private Integer messageCnt;

    @ApiModelProperty(value = "已回复聊天占比，浮点型，客户主动发起聊天后，成员在一个自然日内有回复过消息的聊天数/客户主动发起的聊天数比例，不包括群聊，仅在确有聊天时返回")
    @TableField("reply_percentage")
    @Excel(name = "已回复聊天占比，浮点型，客户主动发起聊天后，成员在一个自然日内有回复过消息的聊天数/客户主动发起的聊天数比例，不包括群聊，仅在确有聊天时返回")
    private Float replyPercentage;

    @ApiModelProperty(value = "平均首次回复时长")
    @TableField("avg_reply_time")
    @Excel(name = "平均首次回复时长")
    private Integer avgReplyTime;

    @ApiModelProperty(value = "删除/拉黑成员的客户数，即将成员删除或加入黑名单的客户数")
    @TableField(value = "negative_feedback_cnt")
    @Excel(name = "删除/拉黑成员的客户数，即将成员删除或加入黑名单的客户数")
    private Integer negativeFeedbackCnt;

    @ApiModelProperty(value = "客户总数(由每日定时任务统计，不去重，首页和数据统计共用)，【首页】：在职员工在we_flower_customer_rel表中，客户关系status != 2的客户数量 + 系统上记录的已离职的员工在we_flower_customer_rel表中，客户关系status = 3的客户数量。【数据统计】：在职员工在we_flower_customer_rel表中，客户关系status != 2的客户数量。")
    @TableField(value = "total_all_contact_cnt")
    @Excel(name = "客户总数")
    private Integer totalAllContactCnt;

    @ApiModelProperty(value = "留存客户总数,每日定时任务统计,去重")
    @TableField(value = "total_contact_cnt")
    @Excel(name = "留存客户总数")
    private Integer totalContactCnt ;

    @ApiModelProperty(value = "今日新客流失数")
    @TableField(value = "new_contact_loss_cnt")
    @Excel(name = "今日新客流失数")
    private Integer newContactLossCnt ;

    @ApiModelProperty(value = "当天新增客户中与员工对话过的人数")
    @TableField(value = "new_contact_speak_cnt")
    private Integer newContactSpeakCnt;

    @ApiModelProperty(value = "当天员工首次给客户发消息，客户在30分钟内回复的客户数")
    @TableField(value = "replied_within_thirty_min_customer_cnt")
    private Integer repliedWithinThirtyMinCustomerCnt;

    @ApiModelProperty(value = "当天会话数-不区分是否为员工主动发起")
    @TableField(value = "all_chat_cnt")
    private Integer allChatCnt;

    @ApiModelProperty("当天员工新客流失客户数 （we_flower表中查找 每日定时任务获取")
    @TableField("new_customer_loss_cnt")
    private Integer newCustomerLossCnt;

    @ApiModelProperty("当天员工客户总数（we_flower表中查找 每日定时任务获取")
    @TableField("contact_total_cnt")
    private Integer contactTotalCnt;

    @ApiModelProperty("当天员工主动发起的会话数量（DataStatisticsTask定时任务统计）")
    @TableField("user_active_chat_cnt")
    private Integer userActiveChatCnt = 0;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
