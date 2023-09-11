package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 编辑自定义链接DTO
 *
 * @author lichaoyu
 * @date 2023/8/23 21:22
 */
@Data
public class EditCustomerChannelDTO {

    @ApiModelProperty("自定义渠道ID")
    private Long channelId;
    @ApiModelProperty("修改的自定义渠道名称")
    private String name;
    @ApiModelProperty("企业ID")
    private String corpId;
}
