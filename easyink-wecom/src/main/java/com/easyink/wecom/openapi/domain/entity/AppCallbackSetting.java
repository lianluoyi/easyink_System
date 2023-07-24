package com.easyink.wecom.openapi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * 类名: app回调消息订阅设置实体
 *
 * @author : silver_chariot
 * @date : 2023/7/17 11:34
 **/
@Data
public class AppCallbackSetting {

    @TableField("id")
    @ApiModelProperty(value = "主键id ")
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppCallbackSetting that = (AppCallbackSetting) o;
        return Objects.equals(corpId, that.corpId) && Objects.equals(callbackUrl, that.callbackUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corpId, callbackUrl);
    }
}
