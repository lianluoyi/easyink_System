package com.easyink.wecom.domain.dto.moment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 类名： 朋友圈员工客户DTO
 *
 * @author 佚名
 * @date 2022/1/13 14:06
 */
@Data
@ApiModel("朋友圈员工客户DTO")
public class MomentUserCustomerDTO {
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("成员发表状态。0:待发布 1：已发布 2：已过期 3：不可发布")
    @Max(3)
    @Min(0)
    private Integer publishStatus;
    @ApiModelProperty(value = "企业id",hidden = true)
    @JsonIgnore
    private String corpId;
    @ApiModelProperty(value = "朋友圈任务id",required = true)
    @NotNull(message = "朋友圈任务id不能为空")
    private Long momentTaskId;
}
