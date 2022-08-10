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
 * 群聊数据统计数据
 * 对象 we_group_statistic
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("we_group_statistic")
public class WeGroupStatistic implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "群ID")
    @TableField("chat_id")
    @Excel(name = "群ID")
    private String chatId;

    @ApiModelProperty(value = "数据日期")
    @TableField("stat_time")
    @Excel(name = "数据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statTime;

    @ApiModelProperty(value = "新增客户群数量")
    @TableField("new_chat_cnt")
    @Excel(name = "新增客户群数量")
    private Integer newChatCnt;

    @ApiModelProperty(value = "截至当天客户群总数量")
    @TableField("chat_total")
    @Excel(name = "截至当天客户群总数量")
    private Integer chatTotal;

    @ApiModelProperty(value = "截至当天有发过消息的客户群数量")
    @TableField("chat_has_msg")
    @Excel(name = "截至当天有发过消息的客户群数量")
    private Integer chatHasMsg;

    @ApiModelProperty(value = "客户群新增群人数")
    @TableField("new_member_cnt")
    @Excel(name = "客户群新增群人数")
    private Integer newMemberCnt;

    @ApiModelProperty(value = "截至当天客户群总人数")
    @TableField("member_total")
    @Excel(name = "截至当天客户群总人数")
    private Integer memberTotal;

    @ApiModelProperty(value = "截至当天有发过消息的群成员数")
    @TableField("member_has_msg")
    @Excel(name = "截至当天有发过消息的群成员数")
    private Integer memberHasMsg;

    @ApiModelProperty(value = "截至当天客户群消息总数")
    @TableField("msg_total")
    @Excel(name = "截至当天客户群消息总数")
    private Integer msgTotal;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
