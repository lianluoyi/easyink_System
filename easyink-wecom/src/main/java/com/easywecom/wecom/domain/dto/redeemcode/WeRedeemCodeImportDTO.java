package com.easywecom.wecom.domain.dto.redeemcode;

import com.easywecom.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * ClassName： WeRedeemCodeImportDTO
 *
 * @author wx
 * @date 2022/7/6 20:30
 */

@Data
@Builder
@AllArgsConstructor
@ApiModel("兑换码导入DTO")
public class WeRedeemCodeImportDTO {

    @ApiModelProperty(value = "兑换码活动id")
    private Long activityId;

    @ApiModelProperty(value = "兑换码")
    @Excel(name = "提货码/兑换码")
    private String code;

    @ApiModelProperty(value = "有效期")
    @Excel(name = "有效期")
    private String effectiveTime;

}
