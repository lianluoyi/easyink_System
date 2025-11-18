package com.easyink.wecom.domain.dto.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 关键词搜索地区提示请求参数
 *
 * @author wx
 * @date 2023/8/1
 */
@Data
@ApiModel("关键词搜索地区提示请求参数")
public class SuggestionDTO {

    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty(value = "企业id", required = true)
    private String corpId;

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 96, message = "搜索关键词最多支持96个字符")
    @ApiModelProperty(value = "搜索关键词，最多支持96个字符（每个英文字符占1个，中文占3个）", required = true)
    private String keyword;

    @ApiModelProperty("限制城市范围：根据城市名称限制地域范围，如，仅获取'广州市'范围内的提示内容")
    private String region;

    @Min(value = 0, message = "regionFix取值为0或1")
    @Max(value = 1, message = "regionFix取值为0或1")
    @ApiModelProperty("0：不限制当前城市，会召回其他城市的poi；1：仅限制在当前城市")
    private Integer regionFix;

    @ApiModelProperty("定位坐标，传入后，若用户搜索关键词为类别词，与此坐标距离近的地点将靠前显示，格式： location=lat,lng")
    private String location;

    @Min(value = 0, message = "getSubpois取值为0或1")
    @Max(value = 1, message = "getSubpois取值为0或1")
    @ApiModelProperty("是否返回子地点，如大厦停车场、出入口等取值：0 [默认]不返回，1 返回")
    private Integer getSubpois;

    @Min(value = 0, message = "getAd取值为0或1")
    @Max(value = 1, message = "getAd取值为0或1")
    @ApiModelProperty("是否返回区划结果，0 [默认]不返回，1 返回")
    private Integer getAd;

    @Min(value = 0, message = "policy取值范围为0、1、10、11")
    @Max(value = 11, message = "policy取值范围为0、1、10、11")
    @ApiModelProperty("检索策略，目前支持：policy=0：默认，常规策略；policy=1：本策略主要用于收货地址、上门服务地址的填写；policy=10：出行场景（网约车）- 起点查询；policy=11：出行场景（网约车）- 终点查询")
    private Integer policy;

    @ApiModelProperty("筛选条件：基本语法：columnName<筛选列>=value<列值>")
    private String filter;

    @ApiModelProperty("返回指定标准附加字段，取值支持：category_code - poi分类编码")
    private String addedFields;

    @ApiModelProperty("地址格式，可选值：short，返回不带行政区划的短地址")
    private String addressFormat;

    @Min(value = 1, message = "页码最小为1")
    @ApiModelProperty("页码，从1开始，最大页码需通过count进行计算，必须与page_size同时使用")
    private Integer pageIndex;

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 20, message = "每页条数最大为20")
    @ApiModelProperty("每页条数，取值范围1-20，必须与page_index同时使用")
    private Integer pageSize;

    @ApiModelProperty("返回格式：支持JSON/JSONP，默认JSON")
    private String output;

    @ApiModelProperty("JSONP方式回调函数")
    private String callback;
} 