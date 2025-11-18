package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 地图API配置VO
 *
 * @author wx
 * @date 2023/8/3
 */
@Data
@ApiModel("地图API配置VO")
public class WeMapConfigVO {

    @ApiModelProperty(value = "是否有企业配置 true:有 false:无")
    private Boolean hasCorpConfig;

    @ApiModelProperty(value = "后端API密钥")
    private String apiKey;
    
    @ApiModelProperty(value = "iframe API密钥")
    private String iframeApiKey;
    
    @ApiModelProperty(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图")
    private Integer mapType;
    
    @ApiModelProperty(value = "地图类型名称")
    private String mapTypeName;

    @ApiModelProperty(value = "每日接口调用限制次数列表")
    private List<DailyLimitInfo> dailyLimits;


    @ApiModelProperty(value = "状态 0：停用 1：启用")
    private Integer status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyLimitInfo{
        /**
         * apicode
         */
        private Integer apiCode;

        /**
         * 调用限制次数
         */
        private Integer dailyLimit;

        /**
         * 当日已经调用次数
         */
        private Integer todayCallCount;
    }
} 