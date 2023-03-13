package com.easyink.wecom.domain.vo;

import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
    @Excel(name = "日期", sort = 1, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date time;

    @ApiModelProperty(value = "新增总数")
    @Excel(name = "新增人数", sort = 2)
    private Integer addCount = 0;

    @ApiModelProperty(value = "删除总数")
    @Excel(name = "流失人数", sort = 3)
    private Integer loseCount = 0;
}
