package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarChannelRecordVO
 *
 * @author wx
 * @date 2022/7/20 20:53
 */
@Data
@ApiModel("渠道点击记录")
@AllArgsConstructor
@NoArgsConstructor
public class RadarChannelRecordVO {

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("点击次数")
    private Integer clickNum;

    @ApiModelProperty("点击人数")
    private String clickPersonNum;

}
