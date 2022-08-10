package com.easyink.wecom.domain.vo.radar;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： WeRadarChannelVO
 *
 * @author wx
 * @date 2022/7/19 16:33
 */
@Data
@ApiModel("雷达渠道VO")
public class WeRadarChannelVO {

    @ApiModelProperty(value = "渠道id")
    private Long id;

    @ApiModelProperty(value = "雷达id")
    private Long radarId;

    @ApiModelProperty(value = "渠道名称")
    private String name;

    @ApiModelProperty(value = "渠道的短链url")
    private String shortUrl;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty("创建人id")
    private String createId;

    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("创建人所属部门id")
    private String departmentId;

    @ApiModelProperty("创建人所属部门姓名")
    private String departmentName;

}
