package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WePreAuthCodeVO
 *
 * @author: 1*+
 * @date: 2021-09-08 16:24
 */
@Data
@ApiModel("预授权码实体")
@NoArgsConstructor
@AllArgsConstructor
public class WePreAuthCodeVO {

    @ApiModelProperty("预授权码")
    private String preAuthCode;
    @ApiModelProperty("三方应用ID")
    private String suiteId;

}
