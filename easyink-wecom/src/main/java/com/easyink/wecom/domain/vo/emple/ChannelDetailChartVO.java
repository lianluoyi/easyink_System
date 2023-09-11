package com.easyink.wecom.domain.vo.emple;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 渠道趋势图VO
 *
 * @author lichaoyu
 * @date 2023/8/24 17:51
 */
@Data
@NoArgsConstructor
public class ChannelDetailChartVO {

    @ApiModelProperty("日期，格式为YYYY-MM-DD")
    private String time;
    @ApiModelProperty("新增客户数")
    private Integer newCustomerCnt;
    @ApiModelProperty("流失客户数")
    private Integer lossCustomerCnt;


    public ChannelDetailChartVO(String time) {
        this.time = time;
        this.newCustomerCnt = 0;
        this.lossCustomerCnt = 0;
    }
}
