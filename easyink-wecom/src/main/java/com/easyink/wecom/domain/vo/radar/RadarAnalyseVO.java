package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName： RadarAnalyseVO
 *
 * @author wx
 * @date 2022/7/20 15:27
 */
@Data
@ApiModel("雷达点击记录分析表")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RadarAnalyseVO {
    @ApiModelProperty("点击人数")
    private List<RadarAnalyseCountVO> list;

    @ApiModelProperty("总数")
    private Integer total;
}
