package com.easyink.wecom.domain.dto.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 获取下级行政区划请求参数
 *
 * @author wx
 * @date 2023/8/1
 */
@Data
@ApiModel("获取下级行政区划请求参数")
public class DistrictChildrenDTO {

    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty(value = "企业id", required = true)
    private String corpId;

    @ApiModelProperty("父级行政区划ID（adcode），缺省时返回一级行政区划，也就是省级")
    private String id;

    @Min(value = 0, message = "轮廓点串取值范围为0-3")
    @Max(value = 3, message = "轮廓点串取值范围为0-3")
    @ApiModelProperty("返回行政区划轮廓点串（经纬度点串），取值：0 默认，不返回轮廓；1 固定3公里抽稀粒度的区划轮廓；2 支持多种抽稀粒度的区划轮廓；3 获取乡镇/街道（四级）轮廓边界")
    private Integer getPolygon;

    @ApiModelProperty("轮廓点串的抽稀精度（仅对get_polygon=2时支持），单位米，可选值：100/500/1000/3000")
    private Integer maxOffset;

    @ApiModelProperty("返回格式：支持JSON/JSONP，默认JSON")
    private String output;

    @ApiModelProperty("JSONP方式回调函数")
    private String callback;
} 