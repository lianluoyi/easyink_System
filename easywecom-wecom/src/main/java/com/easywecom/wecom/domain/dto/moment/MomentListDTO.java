package com.easywecom.wecom.domain.dto.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名： 获取企业全部的发表列表DTO
 *
 * @author 佚名
 * @date 2022/1/6 14:20
 */
@Data
@ApiModel("获取企业全部的发表列表DTO")
public class MomentListDTO {
    @ApiModelProperty(value = "朋友圈记录开始时间。Unix时间戳", required = true)
    @NotNull(message = "朋友圈记录开始时间，不能为空")
    private Long start_time;

    @ApiModelProperty(value = "朋友圈记录结束时间。Unix时间戳", required = true)
    @NotNull(message = "朋友圈记录结束时间，不能为空")
    private Long end_time;

    @ApiModelProperty("朋友圈创建人的userid")
    private String creator;

    @ApiModelProperty("用于分页查询的游标，字符串类型，由上一次调用返回，首次调用可不填")
    private String cursor;

    @ApiModelProperty("朋友圈类型。0：企业发表 1：个人发表 2：所有，包括个人创建以及企业创建，默认情况下为所有类型")
    private Integer filter_type;

    @ApiModelProperty("返回的最大记录数，整型，最大值20，默认值20，超过最大值时取默认")
    private Integer limit;
}
