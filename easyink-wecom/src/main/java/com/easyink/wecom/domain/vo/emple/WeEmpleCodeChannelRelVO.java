package com.easyink.wecom.domain.vo.emple;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获客链接-渠道-客户关系VO
 *
 * @author lichaoyu
 * @date 2023/8/25 15:56
 */
@Data
public class WeEmpleCodeChannelRelVO {

    @ApiModelProperty("客户数")
    private Integer customerCnt;
    @ApiModelProperty("客户的state")
    private String state;
    @ApiModelProperty("日期，格式为YYYY-MM-DD")
    private String date;
}
