package com.easyink.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： MomentCustomer
 *
 * @author 佚名
 * @date 2022/1/6 15:32
 */

@ApiModel("朋友圈客户可见范围")
@Data
public class MomentCustomer {
    @ApiModelProperty("发表成员用户userid")
    private String userid;
    @ApiModelProperty("发送成功的外部联系人userid")
    private String external_userid;
}
