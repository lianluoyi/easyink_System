package com.easyink.wecom.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： OrderBindInfoDTO
 *
 * @author 佚名
 * @date 2021/12/15 15:05
 */
@Data
@ApiModel("员工客户绑定信息dto")
public class OrderBindInfoDTO {
    @ApiModelProperty("客户群id")
    private String chatId;

    @ApiModelProperty(value = "企微员工id", hidden = true)
    @JsonIgnore
    private String userId;

    @ApiModelProperty(value = "企业id", hidden = true)
    @JsonIgnore
    private String corpId;

}
