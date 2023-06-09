package com.easyink.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 类名： 视频DTO
 *
 * @author 佚名
 * @date 2021/10/13 11:59
 */
@Data
@ApiModel("视频DTO《VideoDTO》")
@NoArgsConstructor
public class VideoDTO {

    @ApiModelProperty("视频素材id")
    private String media_id;

    @ApiModelProperty("视频封面media_id，可以通过获取临时素材下载资源")
    private String thumb_media_id;

    @ApiModelProperty("视频url")
    private String videoUrl;

    @ApiModelProperty(value = "视频大小", required = true)
    private Long size;

    @ApiModelProperty("封面")
    private String coverUrl;

    @ApiModelProperty("标题")
    private String title;

    public VideoDTO(String mediaId, String coverMediaId, String title) {
        this.media_id = mediaId;
        this.thumb_media_id = coverMediaId;
        this.title = title;
    }
}
