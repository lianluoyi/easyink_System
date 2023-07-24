package com.easyink.wecom.domain.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活码统计-Redis实体基类
 *
 * @author lichaoyu
 * @date 2023/7/17 11:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活码统计-Redis实体基类")
public class RedisEmpleStatisticBaseModel {

    @ApiModelProperty("新增客户数")
    private Integer newCustomerCnt;

    @ApiModelProperty("流失客户数")
    private Integer lossCustomerCnt;
}
