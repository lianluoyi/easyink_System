package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名:二维码返回实体
 *
 * @author: 1*+
 * @date: 2021-08-23$ 11:03$
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("二维码返回实体")
public class WeAdminQrcodeVO {

    @ApiModelProperty("二维码链接")
    private String qrcodeUrl;

    @ApiModelProperty("二维码Key")
    private String qrcodeKey;


}
