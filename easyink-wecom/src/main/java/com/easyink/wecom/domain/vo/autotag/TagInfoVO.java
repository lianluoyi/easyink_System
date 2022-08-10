package com.easyink.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tigger
 * 2022/3/1 16:18
 **/
@Data
public class TagInfoVO {
    @ApiModelProperty("标签id")
    private String tagId;
    @ApiModelProperty("标签名称")
    private String tagName;
}
