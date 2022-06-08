package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WePreLoginParamVO
 *
 * @author: 1*+
 * @date: 2021-09-08 16:24
 */
@Data
@ApiModel("预单点登录实体")
@NoArgsConstructor
@AllArgsConstructor
public class WePreLoginParamVO {

    @ApiModelProperty("服务商的CorpID")
    private String appid;

}
