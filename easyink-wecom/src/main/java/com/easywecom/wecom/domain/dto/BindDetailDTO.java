package com.easywecom.wecom.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： BindDetailDTO
 *
 * @author 佚名
 * @date 2021/12/14 16:44
 */
@Data
@ApiModel("绑定网点详情dto")
public class BindDetailDTO {
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("工单账号名称")
    private String orderUserName;
    @ApiModelProperty(value = "网点ID", hidden = true)
    @JsonIgnore
    private String networkId;
    @ApiModelProperty("绑定状态 0未绑定 1绑定")
    private Integer bindStatus;
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;
}
