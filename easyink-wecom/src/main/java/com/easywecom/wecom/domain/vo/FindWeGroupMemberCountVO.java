package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeGroupMemberCountVO
 *
 * @author Society my sister Li
 * @date 2021-11-16 10:39
 */
@Data
@ApiModel("查询客户群的员工人数和客户人数统计")
public class FindWeGroupMemberCountVO {

    @ApiModelProperty("客户人数")
    private Integer customerCount = 0;

    @ApiModelProperty("员工人数")
    private Integer staffCount = 0;
}
