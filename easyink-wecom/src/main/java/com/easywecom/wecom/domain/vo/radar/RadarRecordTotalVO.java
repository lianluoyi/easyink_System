package com.easywecom.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： RadarRecordTotalVO
 *
 * @author wx
 * @date 2022/7/20 13:48
 */
@Data
@ApiModel("雷达点击记录数据总览")
public class RadarRecordTotalVO {

    @ApiModelProperty("总点击人数")
    private Integer sumClickPersonNum;

    @ApiModelProperty("总点击次数")
    private Integer sumClickNum;

    @ApiModelProperty("今日点击人数")
    private Integer nowadaysClickPersonNum;

    @ApiModelProperty("今日总点击次数")
    private Integer nowadaysClickNum;

}
