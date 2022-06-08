package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名: MyApplicationIntroductionVO
 *
 * @author: 1*+
 * @date: 2021-10-15 16:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("我的应用实体")
public class MyApplicationIntroductionVO extends ApplicationIntroductionVO {


    @ApiModelProperty(value = "应用配置")
    private String config;

    @ApiModelProperty(value = "启用(ON1Y)")
    private Boolean enable;

    @ApiModelProperty(value = "安装时间")
    private Date installTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;
    @ApiModelProperty(value = "企业名")
    private String companyName;

    @ApiModelProperty(value = "企业ID")
    private String corpId;

}
