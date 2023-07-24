package com.easyink.wecom.domain.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 活码统计-员工维度Redis实体
 *
 * @author lichaoyu
 * @date 2023/7/17 11:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活码统计-员工维度Redis实体")
public class RedisEmpleUserScopeModel {

    @ApiModelProperty("员工ID")
    private String userId;
}
