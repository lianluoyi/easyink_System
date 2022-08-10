package com.easywecom.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeEmpleCodeDTO
 *
 * @author Society my sister Li
 * @date 2021-11-02 10:35
 */
@Data
@ApiModel("查询员工活码")
public class FindWeEmpleCodeDTO {

    @ApiModelProperty("使用员工")
    private String useUserName;

    @ApiModelProperty("活动场景")
    private String scenario;

    @ApiModelProperty("创建人姓名")
    private String createBy;

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty(value = "企业ID",hidden = true)
    private String corpId;

    @ApiModelProperty(value = "来源",hidden = true)
    private Integer source;
}
