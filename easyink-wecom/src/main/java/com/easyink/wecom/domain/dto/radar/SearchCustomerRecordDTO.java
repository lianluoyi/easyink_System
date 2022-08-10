package com.easyink.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName： SearchCustomerRecordDTO
 *
 * @author wx
 * @date 2022/7/20 21:30
 */
@Data
@ApiModel("客户点击记录DTO")
public class SearchCustomerRecordDTO {

    @ApiModelProperty(value = "雷达id")
    @NotNull(message = "雷达id不得为空")
    private Long radarId;

    @ApiModelProperty(value = "客户昵称")
    private String customerName;

    @ApiModelProperty("排序按钮 true:正序 ,false：逆序")
    private Boolean enableSort;

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
