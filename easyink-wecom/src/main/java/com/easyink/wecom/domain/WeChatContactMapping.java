package com.easyink.wecom.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.core.domain.wecom.WeUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;


/**
 * 聊天关系映射对象 we_chat_contact_mapping
 *
 * @author 佚名
 * @date 2021-7-28
 */
@ApiModel("聊天关系映射对象")
@Data
public class WeChatContactMapping extends RootEntity implements Comparable<WeChatContactMapping> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "发送人id")
    @TableField("from_id")
    @Excel(name = "发送人id")
    private String fromId;

    @ApiModelProperty(value = "接收人id")
    @TableField("receive_id")
    @Excel(name = "接收人id")
    private String receiveId;

    @ApiModelProperty(value = "群聊id")
    @TableField("room_id")
    @Excel(name = "群聊id")
    private String roomId;

    @ApiModelProperty(value = "接收人是否为客户 0-成员 1-客户 2-机器人")
    @TableField("is_custom")
    @Excel(name = "是否为客户 0-成员 1-客户 2-机器人")
    private Integer isCustom;

    @ApiModelProperty(value = "聊天时间")
    @TableField("chat_time")
    private String chatTime;

    /**
     * 是否为客户 0-内部 1-外部 2-群聊
     */
    @ApiModelProperty("是否为客户 0-内部 1-外部 2-群聊")
    @TableField(exist = false)
    private Integer searchType;

    /**
     * 内部发送人信息
     */
    @ApiModelProperty("内部发送人信息")
    @TableField(exist = false)
    private WeUser fromWeUser;

    /**
     * 外部发送人信息
     */
    @ApiModelProperty("外部发送人信息")
    @TableField(exist = false)
    private WeCustomer fromWeCustomer;

    /**
     * 内部接收人信息
     */
    @ApiModelProperty("内部接收人信息")
    @TableField(exist = false)
    private WeUser receiveWeUser;

    /**
     * 外部接收人信息
     */
    @ApiModelProperty("外部接收人信息")
    @TableField(exist = false)
    private WeCustomer receiveWeCustomer;

    /**
     * 群信息
     */
    @ApiModelProperty("群信息")
    @TableField(exist = false)
    private WeGroup roomInfo;

    /**
     * 最后一条聊天数据
     */
    @ApiModelProperty("最后一条聊天数据")
    @TableField(exist = false)
    private JSONObject finalChatContext;

    /**
     * 是否是群聊
     */
    @ApiModelProperty("是否是群聊")
    @TableField(exist = false)
    private Integer isRoom;

    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;

    /**
     * 比较消息时间
     *
     * @param weChatContactMapping
     * @return -1 小于 0 等于 1 大于
     */
    @Override
    public int compareTo(WeChatContactMapping weChatContactMapping) {
        boolean thisIsEmpty = ObjectUtils.isEmpty(this.finalChatContext) || !this.finalChatContext.containsKey(WeConstans.MSG_TIME) || ObjectUtils.isEmpty(this.finalChatContext.getLong(WeConstans.MSG_TIME));
        boolean thatIsEmpty = ObjectUtils.isEmpty(weChatContactMapping.getFinalChatContext()) || !weChatContactMapping.getFinalChatContext().containsKey(WeConstans.MSG_TIME) || ObjectUtils.isEmpty(weChatContactMapping.getFinalChatContext().getLong(WeConstans.MSG_TIME));
        if (thisIsEmpty && thatIsEmpty) {
            return 0;
        } else if (thatIsEmpty) {
            return -1;
        } else if (thisIsEmpty) {
            return 1;
        } else {
            return weChatContactMapping.getFinalChatContext().getLong(WeConstans.MSG_TIME).compareTo(this.finalChatContext.getLong(WeConstans.MSG_TIME));
        }
    }
}
