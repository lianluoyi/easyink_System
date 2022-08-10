package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： RadarSumClickRecordVO
 *
 * @author wx
 * @date 2022/7/21 14:01
 */
@Data
@ApiModel("雷达总的点击记录")
public class RadarSumClickRecordVO {
    @ApiModelProperty("总点击人数")
    private Integer sumClickPersonNum;

    @ApiModelProperty("总点击次数")
    private Integer sumClickNum;
}
