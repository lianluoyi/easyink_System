package com.easywecom.wecom.domain.dto.transfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 类名: 分配客户参数
 *
 * @author : silver_chariot
 * @date : 2021/11/29 18:15
 */
@Data
@ApiModel(value = "分配客户参数")
public class TransferCustomerDTO {

    @NotEmpty(message = "请完善分配详情")
    @ApiModelProperty(value = "需要分配的详情列表")
    private HandoverCustomer[] customerList;

    @ApiModelProperty(value = "接替成员的userid", required = true)
    @NotBlank(message = "请填写接替成员")
    private String takeoverUserid;

    @ApiModelProperty(value = "转移成功后发给客户的消息，最多200个字符，不填则使用默认文案")
    private String transferSuccessMsg;

    @Data
    public static class HandoverCustomer {
        @ApiModelProperty(value = "原跟进成员的userid", required = true)
        @NotBlank(message = "请填写跟进成员")
        private String handoverUserid;

        @ApiModelProperty(value = "客户的external_userid列表，每次最多分配100个客户", required = true)
        @NotBlank(message = "缺少需要继承的客户")
        private String externalUserid;
    }


}
