package com.easyink.wecom.domain.vo.statistics.emplecode;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活码统计-活码维度VO
 *
 * @author lichaoyu
 * @date 2023/7/5 20:03
 */
@Data
@NoArgsConstructor
public class EmpleCodeVO extends EmpleCodeBaseVO {

    @ApiModelProperty("活码名称")
    @Excel(name = "活码", sort = 1)
    private String empleName;

    @ApiModelProperty("活码id")
    private String empleCodeId;
}
