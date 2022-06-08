package com.easywecom.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名：SopBatchSwitchDTO
 *
 * @author Society my sister Li
 * @date 2021-12-02 10:47
 */
@Data
@ApiModel("sop批量开关")
public class SopBatchSwitchDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "需要修改的sopIdList", required = true)
    @NotNull(message = "sopIdList不能为空")
    @Size(min = 1, message = "sopIdList不能为空")
    private List<Long> sopIdList;

    @ApiModelProperty(value = "更新开关状态0/1", required = true)
    @NotNull(message = "isOpen不能为空")
    private Boolean isOpen;
}
