package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 话术URL默认返回VO
 *
 * @author 佚名
 * @date 2021/11/3 21:17
 */
@Data
@ApiModel("话术URL默认返回VO")
public class WeWordsUrlVO {
    @ApiModelProperty("封面")
    private String image;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("摘要")
    private String desc;
    @ApiModelProperty("图片是否是url true是 false否")
    private Boolean isUrl;
}
