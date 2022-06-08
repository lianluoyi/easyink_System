package com.easywecom.wecom.domain.vo.autotag.record;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户统计VO
 *
 * @author tigger
 * 2022/3/7 9:30
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerCountVO {

    @ApiModelProperty("客户总数")
    private Integer count;
    @ApiModelProperty("去重客户数量")
    private Integer distinctCount;
}
