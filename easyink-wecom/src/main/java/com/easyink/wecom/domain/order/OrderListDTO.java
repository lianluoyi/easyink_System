package com.easyink.wecom.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 类名： OrderCreateDTO
 *
 * @author 佚名
 * @date 2021/12/13 20:13
 */
@Data
@ApiModel("工单列表接口DTO")
public class OrderListDTO {
    @ApiModelProperty(value = "网点id", hidden = true)
    @JsonIgnore
    private String networkId;

    @ApiModelProperty(value = "工单系统的账号id")
    @NotBlank
    @JsonProperty("orderUserId")
    private String userId;

    @ApiModelProperty("客户群id")
    @NotBlank
    private String chatId;

    @ApiModelProperty(value = "企业id", hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "工单系统的客户id", hidden = true)
    private String customerId;

    @ApiModelProperty(value = "工单状态;固定值(未处理,处理中,已处理)", required = true)
    @NotBlank
    private String orderStatus;

    @ApiModelProperty("分页页码(每页查询最多20条数据)")
    @NotNull
    private Integer page;

}
