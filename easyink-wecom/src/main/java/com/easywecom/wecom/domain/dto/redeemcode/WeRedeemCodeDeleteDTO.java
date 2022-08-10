package com.easywecom.wecom.domain.dto.redeemcode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ClassName： WeRedeemCodeDeleteDTO
 *
 * @author wx
 * @date 2022/7/6 19:12
 */
@Data
@ApiModel("兑换码批量删除DTO")
public class WeRedeemCodeDeleteDTO {

    @NotEmpty(message = "请至少选择一个删除的兑换码")
    @ApiModelProperty("兑换码活动Id")
    private List<String> codeList;

    @ApiModelProperty("兑换码活动id")
    private Long activityId;
}
