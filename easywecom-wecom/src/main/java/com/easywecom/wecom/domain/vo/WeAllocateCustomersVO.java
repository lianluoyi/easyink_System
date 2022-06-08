package com.easywecom.wecom.domain.vo;

import com.easywecom.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： WeAllocateCustomersVO
 *
 * @author 佚名
 * @date 2021/8/31 10:03
 */
@Data
@ApiModel("离职继承历史成员数据实体")
public class WeAllocateCustomersVO extends BaseEntity {
    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("客户id")
    private String externalUserid;
    /**
     * 接替人名称
     */
    @ApiModelProperty("接替人名称")
    private String takeUserName;

    @ApiModelProperty("接替人id")
    private String takeOverUserId;

    /**
     * 公司id
     */
    private String corpId;

    /**
     * 分配客户的状态：
     */
    private Integer status;

    /**
     * 分配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("分配时间")
    private Date allocateTime;

    /**
     * 接替人所在主部门
     */
    @ApiModelProperty("接替人所在的主部门")
    private String mainDepartmentName;

    /**
     * 原拥有着
     */
    @ApiModelProperty("原跟进人id")
    private String handoverUserId;

    @ApiModelProperty("分配结果")
    private String allocateResult;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    /**
     * 员工离职时间
     */
    @ApiModelProperty("员工离职时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;

}
