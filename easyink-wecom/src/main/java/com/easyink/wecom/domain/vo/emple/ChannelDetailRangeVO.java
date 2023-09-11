package com.easyink.wecom.domain.vo.emple;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 渠道新增客户数排行VO
 *
 * @author lichaoyu
 * @date 2023/8/24 17:48
 */
@Data
public class ChannelDetailRangeVO {

    @ApiModelProperty("渠道名称")
    private String name;
    @ApiModelProperty("新增客户数")
    private Integer newCustomerCnt;
}
