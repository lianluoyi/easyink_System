package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： sop查询DTO
 *
 * @author 佚名
 * @date 2021/11/30 15:19
 */
@ApiModel("sop查询DTO《WeOperationsCenterSopSearchDTO》")
@Data
public class WeOperationsCenterSopSearchDTO {
    @ApiModelProperty("sop名称")
    private String name;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("启用状态 0：关闭，1：启用")
    private Integer isOpen;
    @ApiModelProperty(value = "企业id",hidden = true)
    private String corpId;
    @ApiModelProperty(value = "sop类型",required = true)
    private Integer sopType;
}
