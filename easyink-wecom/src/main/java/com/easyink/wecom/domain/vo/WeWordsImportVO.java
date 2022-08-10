package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： WeWordsImportVO
 *
 * @author 佚名
 * @date 2021/11/2 15:00
 */
@Data
@ApiModel("企业话术库导入结果VO")
public class WeWordsImportVO {
    @ApiModelProperty("成功数量")
    private Integer successNum;

    @ApiModelProperty("失败数量")
    private Integer failNum;

    @ApiModelProperty("失败报告")
    private String url;
}
