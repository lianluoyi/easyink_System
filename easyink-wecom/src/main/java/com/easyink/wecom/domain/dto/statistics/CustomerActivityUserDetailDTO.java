package com.easyink.wecom.domain.dto.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 客户活跃度-员工维度-客户活跃度详情DTO
 *
 * @author wx
 * 2023/2/15 16:03
 **/
@Data
public class CustomerActivityUserDetailDTO extends CustomerActivityDTO{
    @ApiModelProperty("员工id")
    @NotBlank(message = "员工id不得为空")
    private String userId;

    @ApiModelProperty("客户昵称")
    private String customerName;

}
