package com.easywecom.wecom.domain.vo.groupcode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 第一个实际码VO
 *
 * @author tigger
 * 2022/2/14 9:38
 **/
@Data
public class GroupCodeActivityFirstVO {

    @ApiModelProperty("活码名称")
    private String activityName;
    @ApiModelProperty("进群提示语")
    private String tipMsg;
    @ApiModelProperty("引导语")
    private String guide;
    @ApiModelProperty("实际群码")
    private String actualQRCode;
    @ApiModelProperty("是否开启提示")
    private String isOpenTip;
    @ApiModelProperty("客服二维码")
    private String serviceQrCode;
    @ApiModelProperty("群聊名称")
    private String groupName;


}
