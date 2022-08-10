package com.easyink.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("小程序DTO《MiniprogramMessageDTO》")
public class MiniprogramMessageDTO {

    @ApiModelProperty("小程序消息标题，最多64个字节")
    private String title;

    @ApiModelProperty("小程序消息封面的mediaid，封面图建议尺寸为520*416")
    private String pic_media_id;

    @ApiModelProperty("小程序appid，必须是关联到企业的小程序应用")
    private String appid;

    @ApiModelProperty("小程序page路径")
    private String page;

    @ApiModelProperty("程序消息封面url")
    private String picUrl;

}
