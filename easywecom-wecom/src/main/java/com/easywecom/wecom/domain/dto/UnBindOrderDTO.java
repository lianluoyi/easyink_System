package com.easywecom.wecom.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： UnBindOrderDTO
 *
 * @author 佚名
 * @date 2021/12/14 15:30
 */
@Data
@ApiModel("解绑工单账号DTO")
public class UnBindOrderDTO {
    /**
     * 企业员工ID
     */
    @ApiModelProperty(value = "企业员工ID", required = true)
    @NotBlank
    private String userId;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID", hidden = true)
    @JsonIgnore
    private String corpId;
}
