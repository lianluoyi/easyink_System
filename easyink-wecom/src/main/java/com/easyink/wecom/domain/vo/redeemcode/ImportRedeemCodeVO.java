package com.easyink.wecom.domain.vo.redeemcode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： ImportRedeemCodeVO
 *
 * @author wx
 * @date 2022/7/5 17:04
 */
@Data
@ApiModel(value = "兑换码导入结果")
public class ImportRedeemCodeVO {

    @ApiModelProperty("成功数量")
    private Integer successNum;

    @ApiModelProperty("失败数量")
    private Integer failNum;

    @ApiModelProperty("失败报告")
    private String url;
}
