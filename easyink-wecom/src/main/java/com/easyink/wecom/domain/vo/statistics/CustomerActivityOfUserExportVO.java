package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.vo.statistics.CustomerActivityOfUserVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * 客户活跃度-数据详情-员工维度导出VO
 *
 * @author wx
 * 2023/2/15 17:03
 **/
@Data
public class CustomerActivityOfUserExportVO {

    @ApiModelProperty("员工名称")
    @Excel(name = "员工姓名", sort = 1)
    private String userName;


    @ApiModelProperty("员工所属部门")
    @Excel(name = "员工所属部门", sort = 2)
    private String departmentName;

    @ApiModelProperty("员工发送消息数")
    @Excel(name = "员工发送消息数", sort = 3)
    private Integer userSendMessageCnt;

    @ApiModelProperty("客户发送消息数")
    @Excel(name = "客户发送消息数", sort = 4)
    private Integer customerSendMessageCnt;

    @ApiModelProperty("会话客户数")
    @Excel(name = "会话客户数", sort = 5)
    private Integer chatCustomerCnt;

    @ApiModelProperty("客户平均消息数")
    @Excel(name = "客户平均消息数",sort = 6)
    private String customerAverageMessageCnt;

    /**
     * 导出构造函数
     *
     * @param customerActivityOfUser    {@link CustomerActivityOfUserVO}
     */
    public CustomerActivityOfUserExportVO(CustomerActivityOfUserVO customerActivityOfUser) {
        if (customerActivityOfUser == null) {
            return;
        }
        BeanUtils.copyProperties(customerActivityOfUser, this);
        customerAverageMessageCnt = customerActivityOfUser.getCustomerAverageMessageCnt();
    }
}
