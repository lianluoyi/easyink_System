package com.easyink.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：GetTaskDetailByUserIdVO
 *
 * @author Society my sister Li
 * @date 2021-12-07 14:00
 */
@Data
@ApiModel("SOP执行详情")
public class GetTaskDetailByUserIdVO {

    @ApiModelProperty("sopId")
    private Long sopId;

    @ApiModelProperty("提醒时间 yyyy-MM-dd HH:mm:ss")
    private String alertTime;

    @ApiModelProperty("员工userId")
    private String userId;

    @ApiModelProperty("目标ID 客户userId/群chatId")
    private String targetId;

    @ApiModelProperty("是否已执行")
    private Boolean isFinish;

    @ApiModelProperty("执行计划ID")
    private Long detailId;

    @ApiModelProperty("客户名称/群名称")
    private String targetUserName;

    @ApiModelProperty("客户头像")
    private String headImageUrl;

    @ApiModelProperty("是否为客户 0:客户,1:群")
    private Integer isCustomer;

    @ApiModelProperty("外部联系人的类型")
    private Integer type;

    @ApiModelProperty("外部联系人公司全称")
    private String corpFullName;

    @ApiModelProperty("客户备注")
    private String targetRemark;
}
