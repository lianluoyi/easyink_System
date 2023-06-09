package com.easyink.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName： SearchChannelRecordDTO
 *
 * @author wx
 * @date 2022/7/20 21:36
 */
@Data
@ApiModel("查询渠道点击记录DTO")
public class SearchChannelRecordDTO {

    @ApiModelProperty(value = "雷达id")
    @NotNull(message = "雷达id不得为空")
    private Long radarId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;
}
