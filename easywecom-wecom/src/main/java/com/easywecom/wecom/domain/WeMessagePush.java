package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 消息发送的对象 we_message_push
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@TableName("we_message_push")
@ApiModel("消息发送的对象")
public class WeMessagePush extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("message_push_id")
    private Long messagePushId;

    @ApiModelProperty(value = "群发类型 0 发给客户 1 发给客户群")
    @TableField("push_type")
    private Integer pushType;

    @ApiModelProperty(value = "消息类型 0 文本消息  1 图片消息 2 语音消息  3 视频消息    4 文件消息 5 文本卡片消息 6 图文消息 7 图文消息（mpnews） 8 markdown消息 9 小程序通知消息 10 任务卡片消息")
    @TableField("message_type")
    private String messageType;

    @ApiModelProperty(value = "消息体")
    @TableField("message_json")
    private String messageJson;

    @ApiModelProperty(value = "消息范围 0 全部客户  1 指定客户")
    @TableField("push_range")
    private String pushRange;

    @ApiModelProperty(value = "0 未删除 1 已删除")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "无效用户")
    @TableField("invaliduser")
    private String invaliduser;

    @ApiModelProperty(value = "无效单位")
    @TableField("invalidparty")
    private String invalidparty;

    @ApiModelProperty(value = "无效标签")
    @TableField("invalidtag")
    private String invalidtag;

    @ApiModelProperty(value = "指定接收消息的成员")
    @TableField("to_user")
    private String toUser;

    @ApiModelProperty(value = "指定接收消息的部门")
    @TableField("to_party")
    private String toParty;

    @ApiModelProperty(value = "指定接收消息的标签")
    @TableField("to_tag")
    private String toTag;

    @ApiModelProperty(value = "群聊id")
    @TableField("chat_id")
    private String chatId;

    /**
     * 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口
     * <a href="https://work.weixin.qq.com/api/doc/10975#%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9A%E6%8E%88%E6%9D%83%E4%BF%A1%E6%81%AF">获取企业授权信息</a> 获取该参数值
     */
    @ApiModelProperty
    @TableField("agent_id")
    private Integer agentid;

}
