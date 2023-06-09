package com.easyink.wecom.domain.vo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 获取已分配客户/客户群详情返回参数
 *
 * @author : silver_chariot
 * @date : 2021/12/9 9:52
 */
@Data
public class GetResignedTransferCustomerDetailVO {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("客户id")
    private String externalUserid;

    @ApiModelProperty("接替人名称")
    private String takeUserName;

    @ApiModelProperty("接替人userid")
    private String takeOverUserId;

    @ApiModelProperty(value = "接替状态")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("分配时间")
    private Date allocateTime;

    @ApiModelProperty("接替人所在的主部门")
    private String mainDepartmentName;

    @ApiModelProperty("原跟进人userid")
    private String handoverUserId;

    @ApiModelProperty("分配结果")
    private String allocateResult;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty("员工离职时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;
}
