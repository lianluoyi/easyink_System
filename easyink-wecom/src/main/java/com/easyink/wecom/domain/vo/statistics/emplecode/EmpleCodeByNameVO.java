package com.easyink.wecom.domain.vo.statistics.emplecode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 活码统计-根据活动场景获取活码信息VO
 *
 * @author lichaoyu
 * @date 2023/7/4 17:48
 */
@Data
public class EmpleCodeByNameVO {

    @ApiModelProperty("活码id")
    private Long id;

    @ApiModelProperty("活动场景")
    private String scenario;
}
