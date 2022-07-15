package com.easywecom.wecom.domain.dto.redeemcode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ClassName： WeRedeemCodeActivityDeleteDTO
 *
 * @author wx
 * @date 2022/7/6 17:20
 */
@Data
@ApiModel("兑换码活动批量删除DTO")
public class WeRedeemCodeActivityDeleteDTO {
    @NotEmpty(message = "请至少选择一个删除的目标")
    @ApiModelProperty("兑换码活动Id")
    private List<Long> idList;
}
