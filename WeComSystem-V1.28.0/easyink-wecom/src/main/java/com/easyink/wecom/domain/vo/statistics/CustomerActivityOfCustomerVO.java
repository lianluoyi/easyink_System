package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户活跃度-数据详情-客户维度
 *
 * @author wx
 * 2023/2/14 15:06
 **/
@NoArgsConstructor
@Data
public class CustomerActivityOfCustomerVO extends SendMessageCntVO{
    @ApiModelProperty("客户id")
    private String externalUserId;

    @ApiModelProperty("客户昵称")
    @Excel(name = "客户", sort = 1)
    private String externalUserName;

    @ApiModelProperty("客户头像url")
    private String externalUserHeadImage;

    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty("所属员工姓名")
    @Excel(name = "所属员工姓名", sort = 2)
    private String userName;

    @ApiModelProperty("员工所属部门")
    @Excel(name = "员工所属部门", sort = 3)
    private String departmentName;

}
