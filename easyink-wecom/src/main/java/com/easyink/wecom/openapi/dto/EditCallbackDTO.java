package com.easyink.wecom.openapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 编辑回调订阅地址请求参数
 *
 * @author : silver_chariot
 * @date : 2023/7/17 14:16
 **/
@Data
public class EditCallbackDTO extends AddCallbackDTO {

    @ApiModelProperty(value = "id")
    private Long id;
}
