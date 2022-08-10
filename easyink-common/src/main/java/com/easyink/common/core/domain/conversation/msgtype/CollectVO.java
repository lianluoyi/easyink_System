package com.easyink.common.core.domain.conversation.msgtype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 填表VO
 *
 * @author tigger
 * 2022/2/8 14:38
 **/
@Data
public class CollectVO extends AttachmentBaseVO {
    @ApiModelProperty("填表消息所在的群名称")
    private String roomName;

    @ApiModelProperty("创建者在群中的名字")
    private String creator;

    @ApiModelProperty("创建的时间")
    private Long createTime;

    @ApiModelProperty("表名")
    private String title;

    @ApiModelProperty("表内容")
    private String details;

    @ApiModelProperty("表项id")
    private Long id;

    @ApiModelProperty("表项名称")
    private String ques;

    @ApiModelProperty("表项类型，有Text(文本),Number(数字),Date(日期),Time(时间)")
    private String type;
}
