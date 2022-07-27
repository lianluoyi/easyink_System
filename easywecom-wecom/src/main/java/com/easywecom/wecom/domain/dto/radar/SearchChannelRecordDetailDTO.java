package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName： SearchChannelRecordDetailDTO
 *
 * @author wx
 * @date 2022/7/20 21:10
 */
@Data
@ApiModel("查询渠道点击记录详情DTO")
public class SearchChannelRecordDetailDTO {

    @ApiModelProperty(value = "选择渠道名称")
    @NotBlank(message = "渠道名不得为空")
    private String channelName;

    @ApiModelProperty(value = "雷达id")
    @NotNull(message = "雷达id不得为空")
    private Long radarId;

    @ApiModelProperty(value = "客户昵称")
    private String customerName;

}
