package com.easyink.common.core.domain.conversation.msgtype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视频号VO
 *
 * @author tigger
 * 2022/2/8 14:55
 **/
@Data
public class SphFeedVO {

    @ApiModelProperty("视频号消息类型。2 图片、4 视频、9 直播。")
    private Integer feedType;

    @ApiModelProperty("视频号账号名称")
    private String sphName;

    @ApiModelProperty("视频号消息描述")
    private String feedDesc;
}
