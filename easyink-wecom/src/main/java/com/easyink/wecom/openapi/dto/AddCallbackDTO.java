package com.easyink.wecom.openapi.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 新增回调订阅地址请求参数
 *
 * @author : silver_chariot
 * @date : 2023/7/17 11:48
 **/
@Data
public class AddCallbackDTO {

    @TableField("corp_id")
    @ApiModelProperty(value = "企业id ")
    private String corpId;

    @TableField("callback_url")
    @ApiModelProperty(value = "回调地址 ")
    private String callbackUrl;

    @TableField("token")
    @ApiModelProperty(value = "token")
    private String token;

    @TableField("encoding_aes_key")
    @ApiModelProperty(value = "用于加密解密的aesKey ")
    private String encodingAesKey;
}
