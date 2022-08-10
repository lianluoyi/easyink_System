package com.easyink.wecom.domain.entity.moment;

import com.easyink.wecom.domain.dto.message.ImageMessageDTO;
import com.easyink.wecom.domain.dto.message.LinkMessageDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.dto.message.VideoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 朋友圈
 *
 * @author 佚名
 * @date 2022/1/6 14:38
 */
@ApiModel("朋友圈实体")
@Data
public class Moment {
    @ApiModelProperty("朋友圈id")
    private String moment_id;
    @ApiModelProperty("朋友圈创建者userid，企业发表内容到客户的朋友圈接口创建的朋友圈不再返回该字段")
    private String creator;
    @ApiModelProperty("创建时间")
    private String create_time;
    @ApiModelProperty("朋友圈创建来源。0：企业 1：个人")
    private Integer create_type;
    @ApiModelProperty("可见范围类型。0：部分可见 1：公开")
    private Integer visible_type;
    @ApiModelProperty("文本")
    private TextMessageDTO text;
    @ApiModelProperty("图片")
    private ImageMessageDTO image;
    @ApiModelProperty("视频")
    private VideoDTO video;
    @ApiModelProperty("网页")
    private LinkMessageDTO link;
    @ApiModelProperty("地理位置")
    private Location location;
}
