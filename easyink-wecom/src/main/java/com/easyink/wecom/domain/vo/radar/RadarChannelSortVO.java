package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarChannelSortVO
 *
 * @author wx
 * @date 2022/7/20 17:21
 */
@Data
@ApiModel("雷达渠道点击人数排行")
@AllArgsConstructor
@NoArgsConstructor
public class RadarChannelSortVO {

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("点击数量")
    private Integer clickNum;

}
