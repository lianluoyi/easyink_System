package com.easyink.wecom.domain.entity.radar;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ClassName： WeRadarUrl
 *
 * @author wx
 * @date 2022/7/18 15:13
 */
@Getter
@Setter
public class WeRadarUrl {

    @ApiModelProperty(value = "雷达原始路径url,真实链接")
    @Valid
    @NotBlank(message = "雷达链接访问地址不得为空")
    @Size(max = 3000, message = "雷达链接过长最多3000")
    private String url;

    @ApiModelProperty(value = "雷达链接封面图")
    @Size(max = 1200, message = "雷达封面图链接过长最多")
    private String coverUrl;

    @ApiModelProperty(value = "链接标题")
    @Valid
    @Size(max = 128, message = "链接标题长度超过限制,最长16个字符")
    private String title;

    @ApiModelProperty(value = "雷达链接摘要")
    @Valid
    @Size(max = 512, message = "摘要长度超过限制,最长64字符")
    private String content;

    @ApiModelProperty(value = "链接时使用：0 默认，1 自定义")
    private Boolean isDefined;
}
