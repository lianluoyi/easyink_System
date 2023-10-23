package com.easyink.common.core.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会话存档获取上下文DTO
 *
 * @author lichaoyu
 * @date 2023/9/18 16:40
 */
@Data
@ApiModel("会话存档获取上下文DTO")
public class ConversationArchiveViewContextDTO {

    @ApiModelProperty("企业id")
    private String corpId;

    @ApiModelProperty("群聊id")
    private String roomId;

    @ApiModelProperty("发送者id")
    private String fromId;

    @ApiModelProperty("接收者id")
    private String receiveId;

    @ApiModelProperty("查看上下文的消息id")
    private String msgId;

    @ApiModelProperty("查询上下文类型: null:前后十条, after:后20条, before:前20条")
    private String type;
}
