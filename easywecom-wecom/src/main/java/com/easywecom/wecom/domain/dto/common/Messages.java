package com.easywecom.wecom.domain.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tigger
 * 2022/1/18 16:56
 **/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Messages {

    @ApiModelProperty("文本")
    private Text text;
    @ApiModelProperty("图片/海报")
    private Image image;
    @ApiModelProperty("链接")
    private Link link;
    @ApiModelProperty("小程序")
    private MiniProgram miniprogram;
    @ApiModelProperty("文件")
    private File file;
    @ApiModelProperty("视频")
    private Video video;

}
