package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.annotation.Excel;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.vo.statistics.CustomerActivityOfDateVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 客户活跃度-日期维度报表
 *
 * @author wx
 * 2023/2/15 17:16
 **/
@Data
public class CustomerActivityOfDateExportVO {

    @ApiModelProperty("时间")
    @Excel(name = "时间", sort = 1)
    private String time;

    @ApiModelProperty("员工发送消息数")
    @Excel(name = "员工发送消息数", sort = 2)
    private Integer userSendMessageCnt;

    @ApiModelProperty("客户发送消息数")
    @Excel(name = "客户发送消息数", sort = 3)
    private Integer customerSendMessageCnt;

    @ApiModelProperty("会话客户数")
    @Excel(name = "会话客户数", sort = 4)
    private Integer chatCustomerCnt;

    @ApiModelProperty("客户平均消息数")
    @Excel(name = "客户平均消息数", sort = 5)
    private Integer customerAverageMessageCnt;

    /**
     * 导出构造函数
     *
     * @param customerActivityOfDate    {@link CustomerActivityOfDateVO}
     */
    public CustomerActivityOfDateExportVO(CustomerActivityOfDateVO customerActivityOfDate) {
        if (customerActivityOfDate == null) {
            return;
        }
        BeanUtils.copyPropertiesASM(customerActivityOfDate, this);
    }

}
