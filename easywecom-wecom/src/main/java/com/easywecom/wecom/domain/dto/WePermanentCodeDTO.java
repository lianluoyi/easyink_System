package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 类名: WePermanentCodeDTO
 *
 * @author: 1*+
 * @date: 2021-09-08 16:30
 */
@Data
@ApiModel("获取永久授权码请求实体")
@NoArgsConstructor
@AllArgsConstructor
public class WePermanentCodeDTO {

    @ApiModelProperty("临时授权码")
    @NotBlank(message = "临时授权码不能为空")
    private String authCode;

    @ApiModelProperty("第三方应用的SuiteId")
    private String suiteId;


}
