package com.easyink.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tigger
 * 2022/3/1 16:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagInfoVO {
    @ApiModelProperty("标签id")
    private String tagId;
    @ApiModelProperty("标签名称")
    private String tagName;
}
