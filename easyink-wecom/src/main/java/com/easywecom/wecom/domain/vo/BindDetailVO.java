package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： BindDetailVO
 *
 * @author 佚名
 * @date 2021/12/14 17:04
 */
@Data
@ApiModel("绑定网点详情VO")
public class BindDetailVO {
    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty("员工名称")
    private String userName;
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("企业id")
    private String corpId;
    @ApiModelProperty("员工头像")
    private String headImageUrl;
    @ApiModelProperty("工单账号名称")
    private String orderUserName;
    @ApiModelProperty("工单账号id")
    private String orderUserId;
    @ApiModelProperty("绑定状态 0未绑定 1绑定")
    private Integer bindStatus;
}
