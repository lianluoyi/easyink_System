package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName： SearchCustomerRecordDetailDTO
 *
 * @author wx
 * @date 2022/7/20 21:43
 */
@Data
@ApiModel("查询客户点击记录详情DTO")
public class SearchCustomerRecordDetailDTO {
    @ApiModelProperty(value = "雷达id")
    @NotNull(message = "雷达id不得为空")
    private Long radarId;

    @ApiModelProperty(value = "externalId")
    @NotBlank(message = "客户externalId不得为空")
    private String externalId;

}
