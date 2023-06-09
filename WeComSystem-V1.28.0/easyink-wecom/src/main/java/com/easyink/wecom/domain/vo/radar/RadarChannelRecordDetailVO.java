package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarChannelRecordDetailVO
 *
 * @author wx
 * @date 2022/7/20 21:04
 */
@Data
@ApiModel("渠道点击记录详情")
@AllArgsConstructor
@NoArgsConstructor
public class RadarChannelRecordDetailVO {
    @ApiModelProperty("客户externalId")
    private String externalId;

    @ApiModelProperty("客户名称")
    private String name;

    @ApiModelProperty("点击次数")
    private Integer clickNum;

    @ApiModelProperty("最近点击渠道")
    private String channelName;

    @ApiModelProperty("最近点击时间")
    private String clickTime;
}
