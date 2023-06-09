package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: SuiteAuthStatusVO
 *
 * @author: 1*+
 * @date: 2021-12-30 14:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("应用授权状态")
public class SuiteAuthStatusVO {

    @ApiModelProperty("授权成功")
    private boolean authSuccess;

    @ApiModelProperty("应用ID")
    private String suiteId;

    @ApiModelProperty("企业ID")
    private String corpId;


}
