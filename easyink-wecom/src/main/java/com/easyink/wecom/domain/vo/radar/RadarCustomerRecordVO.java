package com.easyink.wecom.domain.vo.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarCustomerRecordVO
 *
 * @author wx
 * @date 2022/7/20 20:21
 */
@Data
@ApiModel("客户点击记录")
@AllArgsConstructor
@NoArgsConstructor
public class RadarCustomerRecordVO {

    @ApiModelProperty("客户externalId")
    private String externalId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("客户头像")
    private String headImageUrl;

    @ApiModelProperty("点击次数")
    private Integer clickNum;

    @ApiModelProperty("最近点击渠道")
    private String channelName;

    @ApiModelProperty("最近点击时间")
    private String clickTime;

}
