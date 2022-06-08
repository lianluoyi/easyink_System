package com.easywecom.wecom.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 聊天侧边栏
 *
 * @author admin
 */
@Data
@ApiModel("聊天侧边栏实体")
public class FindCollectionsVO {

    @ApiModelProperty(value = "素材类型 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 5 海报", allowableValues = "range[0,5]")
    private String mediaType;

    @ApiModelProperty(value = "素材id")
    private Long materialId;

    @ApiModelProperty(value = "本地资源文件地址")
    private String materialUrl;

    @ApiModelProperty(value = "文本内容、图片文案")
    private String content;

    @ApiModelProperty(value = "图片名称")
    private String materialName;

    @ApiModelProperty(value = "是否收藏 0未收藏 1 已收藏")
    private String collection;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "封面地址")
    private String coverUrl;

}
