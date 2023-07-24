package com.easyink.wecom.domain.redis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活码统计-活码维度Redis实体
 *
 * @author lichaoyu
 * {@code @date} 2023/7/17 11:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活码统计-活码维度Redis实体")
public class RedisEmpleScopeModel {

    @ApiModelProperty("活码ID")
    private String empleCodeId;
}
