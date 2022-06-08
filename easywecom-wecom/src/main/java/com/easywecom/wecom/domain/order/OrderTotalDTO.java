package com.easywecom.wecom.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： OrderCreateDTO
 *
 * @author 佚名
 * @date 2021/12/13 20:13
 */
@Data
@ApiModel("工单统计DTO")
public class OrderTotalDTO {
    @ApiModelProperty(value = "网点id", hidden = true)
    @JsonIgnore
    private String networkId;

    @ApiModelProperty("客服id")
    @NotBlank
    private String userId;

    @ApiModelProperty("客户id")
    @NotBlank
    private String customerId;
}
