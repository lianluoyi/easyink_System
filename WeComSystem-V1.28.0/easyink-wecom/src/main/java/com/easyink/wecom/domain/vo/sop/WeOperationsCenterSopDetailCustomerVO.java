package com.easyink.wecom.domain.vo.sop;

import com.easyink.wecom.domain.vo.sop.abs.AbstractWeOperationsCenterSopDetailVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sop客户执行详情VO
 *
 * @author tigger
 * 2021/12/10 14:19
 **/
@Data
public class WeOperationsCenterSopDetailCustomerVO extends AbstractWeOperationsCenterSopDetailVO {


    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("客户头像")
    private String avatar;
    @ApiModelProperty("客户类型 1表示该外部联系人是微信用户，2表示该外部联系")
    private Integer customerType;
    @ApiModelProperty("企业名称")
    private String corpName;

    @ApiModelProperty("是否已执行 0：未执行，1：已执行")
    private Integer isFinish;
    @ApiModelProperty("提醒时间")
    private String alertTime;
    @ApiModelProperty("完成时间")
    private String finishTime;

    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("群主")
    private String chatCreatorName;

}
