package com.easyink.wecom.domain.vo.emple;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获客链接详情-员工维度VO
 *
 * @author lichaoyu
 * @date 2023/8/25 9:15
 */
@Data
public class AssistantDetailStatisticCustomerVO {

    @ApiModelProperty("客户id")
    private String externalUserId;
    @ApiModelProperty("客户名称")
    @Excel(name = "客户", sort = 1)
    private String externalUserName;
    @ApiModelProperty("客户头像")
    private String externalUserHeadImage;
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("员工姓名")
    @Excel(name = "添加员工", sort = 2)
    private String userName;
    @ApiModelProperty("部门名称")
    @Excel(name = "所属部门", sort = 3)
    private String departmentName;
    @ApiModelProperty("添加的来源state")
    private String state;
    @ApiModelProperty("添加渠道名称")
    @Excel(name = "添加渠道", sort = 4)
    private String channelName;
    @ApiModelProperty("添加时间，格式YYYY:MM:DD HH:MM")
    @Excel(name = "添加时间", sort = 5)
    private String addTime;

}
