package com.easyink.wecom.domain.dto.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 逆地址解析请求参数
 *
 * @author wx
 * @date 2023/8/1
 */
@Data
@ApiModel("逆地址解析请求参数")
public class GeocoderDTO {


    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty(value = "企业id", required = true)
    private String corpId;

    @NotBlank(message = "经纬度不能为空")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?,[0-9]+(\\.[0-9]+)?$", message = "经纬度格式错误，正确格式为：纬度,经度")
    @ApiModelProperty(value = "经纬度（GCJ02坐标系）, 格式：lat<纬度>,lng<经度>", required = true)
    private String location;

    @Min(value = 0, message = "getPoi取值为0或1")
    @Max(value = 1, message = "getPoi取值为0或1")
    @ApiModelProperty("是否返回周边地点（POI）列表，可选值：0 不返回(默认)，1 返回")
    private Integer getPoi;

    @ApiModelProperty("周边POI（AOI）列表控制参数，多个参数使用英文分号间隔，例如：poi_options=address_format=short;radius=5000;policy=2")
    private String poiOptions;

    @ApiModelProperty("返回格式：支持JSON/JSONP，默认JSON")
    private String output;

    @ApiModelProperty("JSONP方式回调函数")
    private String callback;
} 