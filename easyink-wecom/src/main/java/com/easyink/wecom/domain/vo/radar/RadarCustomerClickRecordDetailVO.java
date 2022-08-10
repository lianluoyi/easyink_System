package com.easyink.wecom.domain.vo.radar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarCustomerClickRecordDetailVO
 *
 * @author wx
 * @date 2022/7/20 21:47
 */
@Data
@ApiModel("客户点击记录详情")
@AllArgsConstructor
@NoArgsConstructor
public class RadarCustomerClickRecordDetailVO {

    @ApiModelProperty(value = "客户id")
    private String externalId;

    @ApiModelProperty(value = "客户记录")
    private String recordText;

    @ApiModelProperty(value = "点击时间")
    private String clickTime;

    @ApiModelProperty(value = "客户名称")
    @JsonIgnore
    private String customerName;

    @ApiModelProperty(value = "渠道类型")
    @JsonIgnore
    private Integer channelType;

    @ApiModelProperty(value = "渠道名字")
    private String channelName;

    @ApiModelProperty(value = "员工名称")
    @JsonIgnore
    private String userName;

    @ApiModelProperty(value = "详情(如果是员工活码,则为员工活码使用场景，如果是新客进群则为新客进群的活码名称,如果是SOP则为SOP名称，如果是群日历，则为日历名称)")
    @JsonIgnore
    private String detail;
}
