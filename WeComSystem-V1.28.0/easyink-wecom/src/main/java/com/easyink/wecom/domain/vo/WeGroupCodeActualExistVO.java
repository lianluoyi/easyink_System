package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： 存在实际群码返回VO
 *
 * @author 佚名
 * @date 2021/11/4 15:25
 */
@Data
@NoArgsConstructor
@ApiModel("存在实际群码返回VO")
public class WeGroupCodeActualExistVO {
    @ApiModelProperty("存在实际群码的条数")
    private Integer count;
    @ApiModelProperty("已存在的群聊id 用逗号隔开")
    private String chatIds;

}
