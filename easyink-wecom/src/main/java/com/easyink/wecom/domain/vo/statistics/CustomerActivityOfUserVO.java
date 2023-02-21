package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户活跃度-数据详情-员工维度VO
 *
 * @author wx
 * 2023/2/14 15:01
 **/
@Data
@NoArgsConstructor
public class CustomerActivityOfUserVO extends ChatMessageCntVO{
    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty("员工名称")
    private String userName;

    @ApiModelProperty("员工头像地址url")
    private String userHeadImage;

    @ApiModelProperty("员工所属部门")
    private String departmentName;

}
