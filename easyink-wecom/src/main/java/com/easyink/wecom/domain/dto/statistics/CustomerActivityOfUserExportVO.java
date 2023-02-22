package com.easyink.wecom.domain.dto.statistics;

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
    private Integer customerAverageMessageCnt;



    /**
     * 获取客户平均消息数 客户发送消息数 / 客户平均消息数
     *
     * @return 客户平均消息数
     */
    public String getCustomerAverageMessageCnt() {
        if (this.getCustomerSendMessageCnt() == null || chatCustomerCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        if (chatCustomerCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal customerSendMessageCntDecimal = new BigDecimal(this.getCustomerSendMessageCnt());
        BigDecimal chatCustomerCntDecimal = new BigDecimal(chatCustomerCnt);
        int scale = 2;
        // 计算客户平均消息数  客户发送消息数 / 客户平均消息数
        return customerSendMessageCntDecimal
                .divide(chatCustomerCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 导出构造函数
     *
     * @param customerActivityOfUser    {@link CustomerActivityOfUserVO}
     */
    public CustomerActivityOfUserExportVO(CustomerActivityOfUserVO customerActivityOfUser) {
        if (customerActivityOfUser == null) {
            return;
        }
        BeanUtils.copyPropertiesASM(customerActivityOfUser, this);
    }
}
