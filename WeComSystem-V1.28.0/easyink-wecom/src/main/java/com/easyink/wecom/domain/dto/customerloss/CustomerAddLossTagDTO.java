package com.easyink.wecom.domain.dto.customerloss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户继承 流失提醒和打客户标签
 * 类名：CustomerAddLossTagDTO
 *
 * @author lichaoyu
 * @date 2023/3/23 21:03
 */
@Data
public class CustomerAddLossTagDTO {

    @ApiModelProperty("标签列表")
    private List<String> lossTagIdList;

    @ApiModelProperty("客户流失提醒开关")
    private String customerChurnNoticeSwitch;

    @ApiModelProperty("客户流失标签开关")
    private String customerLossTagSwitch;
}
