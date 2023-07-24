package com.easyink.wecom.openapi.domain.vo;

import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: appId生成结果
 *
 * @author : silver_chariot
 * @date : 2022/3/14 11:47
 */
@Data
@ApiModel("开发参数生成结果")
@AllArgsConstructor
@NoArgsConstructor
public class AppIdGenVO {

    @ApiModelProperty(value = "appId")
    private String appId;
    @ApiModelProperty(value = "appSecret")
    private String appSecret;

    public AppIdGenVO(AppIdInfo appIdInfo) {
        this.appId = appIdInfo.getAppId();
        this.appSecret = appIdInfo.getAppSecret();
    }
}
