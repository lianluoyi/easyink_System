package com.easywecom.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：FindWeGroupSopDTO
 *
 * @author Society my sister Li
 * @date 2021-12-01 10:10
 */
@Data
@ApiModel("查询SOP任务")
public class FindWeGroupSopDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("sop名称")
    private String name;

    @ApiModelProperty("使用人（模糊搜索）")
    private String userName;

    @ApiModelProperty("是否开启")
    private Integer isOpen;

    @ApiModelProperty(name = "sopType类型", required = true)
    @NotNull(message = "sopType不能为空")
    private Integer sopType;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
