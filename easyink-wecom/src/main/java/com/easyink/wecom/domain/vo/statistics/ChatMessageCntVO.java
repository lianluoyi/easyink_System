package com.easyink.wecom.domain.vo.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会话消息数VO
 *
 * @author wx
 * 2023/2/14 14:56
 **/
@Data
@NoArgsConstructor
public class ChatMessageCntVO extends SendMessageCntVO{

    @ApiModelProperty("会话客户数")
    private Integer chatCustomerCnt;

    @ApiModelProperty("客户平均消息数")
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



}
