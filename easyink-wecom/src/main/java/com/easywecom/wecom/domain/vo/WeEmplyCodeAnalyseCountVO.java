package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeEmplyCodeAnalyseCountVO
 *
 * @author Society my sister Li
 * @date 2021-11-04 16:46
 */
@Data
@ApiModel("员工活码数据统计")
public class WeEmplyCodeAnalyseCountVO {

    @ApiModelProperty(value = "日期")
    private String time;

    @ApiModelProperty(value = "新增总数")
    private Integer addCount = 0;

    @ApiModelProperty(value = "删除总数")
    private Integer loseCount = 0;
}
