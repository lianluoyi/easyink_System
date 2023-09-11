package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获客链接-自定义渠道VO
 *
 * @author lichaoyu
 * @date 2023/8/23 21:35
 */
@Data
@ApiModel("获客链接-自定义渠道VO")
public class WeEmpleCodeChannelVO {

    @ApiModelProperty("自定义渠道id")
    private Long channelId;
    @ApiModelProperty("自定义渠道名称")
    private String name;
    @ApiModelProperty("自定义渠道Url")
    private String channelUrl;
    @ApiModelProperty("自定义渠道创建人")
    private String createBy;
    @ApiModelProperty("自定义渠道创建时间")
    private String createTime;
    @ApiModelProperty("自定义渠道删除状态，true：已删除；false：正常")
    private Boolean delFlag;

}
