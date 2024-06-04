package com.easyink.wecom.openapi.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * open_api根据unionId获取externalUserId的请求参数
 *
 * @author : limeizi
 * @date : 2024/2/4 17:36
 */
@Data
@Validated
public class GetWeCustomerByUnionIdDTO {

    /**
     * 外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。
     */
    @NotBlank(message = "unionId不能为空")
    private String unionId;
}
