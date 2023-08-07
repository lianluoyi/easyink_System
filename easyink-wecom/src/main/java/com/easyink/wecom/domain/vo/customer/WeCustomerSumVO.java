package com.easyink.wecom.domain.vo.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 类名: 客户统计返回参数
 *
 * @author : silver_chariot
 * @date : 2021/11/22 21:07
 */
@Data
@ApiModel("客户统计数据")
@Builder
public class WeCustomerSumVO {

    @ApiModelProperty(value = "去重后的客户总数")
    private Integer ignoreDuplicateCount;

    public static WeCustomerSumVO empty() {
        return WeCustomerSumVO.builder().ignoreDuplicateCount(0).build();
    }
}
