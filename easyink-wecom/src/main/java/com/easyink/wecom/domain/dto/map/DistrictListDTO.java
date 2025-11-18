package com.easyink.wecom.domain.dto.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取省市区列表请求参数
 *
 * @author wx
 * @date 2023/8/1
 */
@Data
@ApiModel("获取省市区列表请求参数")
public class DistrictListDTO {
    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty(value = "企业id", required = true)
    private String corpId;

    @ApiModelProperty(value = "区划列表新结构，取值: 1 新结构，是以省市区实际归属进行嵌套的结构返回")
    private Integer structType;

    @ApiModelProperty(value = "区划列表新结构，取值: 1 新结构，是以省市区实际归属进行嵌套的结构返回")
    private Integer struct_type;

    @ApiModelProperty("返回格式：支持JSON/JSONP，默认JSON")
    private String output;

    @ApiModelProperty("JSONP方式回调函数")
    private String callback;
} 