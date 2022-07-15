package com.easywecom.wecom.domain;

import com.easywecom.wecom.domain.dto.AddWeMaterialDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * ClassName： WeRedeemCodeWelcomeMsgAndMaterial
 *
 * @author wx
 * @date 2022/7/8 19:24
 */
@Data
@ApiModel("兑换码欢迎语")
public class WeRedeemCodeWelcomeMsgAndMaterial {

    @ApiModelProperty(value = "兑换码发送该欢迎语")
    private String redeemCodeMsg;

    @ApiModelProperty(value = "兑换码使用附件排序")
    private String[] redeemCodeMaterialSort;

    @ApiModelProperty(value = "兑换码欢迎语类型,1: 成功发送的欢迎语, 2: 失败发送的欢迎语, 3: 限制发送的欢迎语")
    private Integer redeemCodeMsgType;

    @ApiModelProperty(value = "兑换码使用素材")
    private List<AddWeMaterialDTO> redeemCodeMaterialList;
}
