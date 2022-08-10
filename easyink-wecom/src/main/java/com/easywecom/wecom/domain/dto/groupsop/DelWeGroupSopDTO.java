package com.easywecom.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名：DelWeGroupSopDTO
 *
 * @author Society my sister Li
 * @date 2021-12-01 10:10
 */
@Data
@ApiModel("删除SOP任务")
public class DelWeGroupSopDTO {

    @ApiModelProperty(value = "sopIdList", required = true)
    @Size(min = 1, message = "sopIdList不能为空")
    @NotNull(message = "sopIdList不能为空")
    private List<Long> sopIdList;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历",required = true)
    @Max(value = 5,message = "sopType值非法")
    @NotNull(message = "sopType不能为空")
    private Integer sopType;
}
