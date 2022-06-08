package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: QueryPresTagGroupStatDTO
 *
 * @author: 1*+
 * @date: 2021-11-03 14:43
 */
@Data
public class QueryPresTagGroupStatDTO {


    @ApiModelProperty("客户名")
    private String customerName;
    @ApiModelProperty("过滤条件:是否在群[0:不在群,1:在群]")
    private Integer isInGroup;
    @ApiModelProperty("过滤条件:送达状态[0:未送达,1:已送达]")
    private Integer isSent;
    @ApiModelProperty("页码")
    private Integer pageNum = 1;
    @ApiModelProperty("页大小")
    private Integer pageSize = 10;

}
