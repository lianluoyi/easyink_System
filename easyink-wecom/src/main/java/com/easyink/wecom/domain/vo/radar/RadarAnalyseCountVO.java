package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarAnalyseCountVO
 *
 * @author wx
 * @date 2022/7/20 15:35
 */
@Data
@ApiModel("员工活码数据统计")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RadarAnalyseCountVO {
    @ApiModelProperty(value = "日期")
    private String createDate;

    @ApiModelProperty(value = "点击人数")
    private Integer clickPersonNum;

    @ApiModelProperty(value = "点击次数")
    private Integer sumClickNum;

}
