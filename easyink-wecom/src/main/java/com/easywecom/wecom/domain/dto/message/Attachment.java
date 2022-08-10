package com.easywecom.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 附件
 *
 * @author 佚名
 * @date 2021/10/13 11:16
 */
@ApiModel("消息附件《Attachment》")
@Data
public class Attachment {
    @ApiModelProperty("消息类型")
    private String msgtype;

    @ApiModelProperty("图片/海报消息")
    private ImageMessageDTO imageMessage;

    @ApiModelProperty("链接消息")
    private LinkMessageDTO linkMessage;

    @ApiModelProperty("小程序消息")
    private MiniprogramMessageDTO miniprogramMessage;

    @ApiModelProperty("文件")
    private FileDTO fileDTO;

    @ApiModelProperty("视频")
    private VideoDTO videoDTO;

    @ApiModelProperty("雷达")
    private RadarMessageDTO radarMessage;

}
