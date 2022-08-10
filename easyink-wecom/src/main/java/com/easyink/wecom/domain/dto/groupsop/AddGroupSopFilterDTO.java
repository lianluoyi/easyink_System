package com.easyink.wecom.domain.dto.groupsop;

import com.easyink.wecom.domain.WeOperationsCenterGroupSopFilterEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：AddGroupSopFilterDTO
 *
 * @author Society my sister Li
 * @date 2021-11-30 21:56
 */
@Data
@ApiModel("群SOP过滤条件")
public class AddGroupSopFilterDTO extends WeOperationsCenterGroupSopFilterEntity {

    @ApiModelProperty(value = "循环SOP的开始时间", required = true)
    private String cycleStart;

    @ApiModelProperty(value = "循环SOP的结束时间", required = true)
    private String cycleEnd;
}
