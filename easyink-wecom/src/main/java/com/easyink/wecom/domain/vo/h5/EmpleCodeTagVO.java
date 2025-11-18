package com.easyink.wecom.domain.vo.h5;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工活码标签VO
 * @author tigger
 * 2025/1/15 9:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleCodeTagVO {

    @ApiModelProperty(value = "标签id")
    private String tagId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;
}
