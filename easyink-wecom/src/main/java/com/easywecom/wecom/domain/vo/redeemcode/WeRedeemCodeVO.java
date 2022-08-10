package com.easywecom.wecom.domain.vo.redeemcode;

import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： WeRedeemCodeVO
 *
 * @author wx
 * @date 2022/7/6 11:27
 */
@Data
@ApiModel("兑换码VO")
public class WeRedeemCodeVO extends WeRedeemCode {

    @ApiModelProperty("领取人名称")
    private String receiveName;
}
