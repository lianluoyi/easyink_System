package com.easyink.wecom.domain.vo.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户活跃度-日期维度VO
 *
 * @author wx
 * 2023/2/14 14:51
 **/
@Data
@NoArgsConstructor
public class CustomerActivityOfDateVO extends ChatMessageCntVO {

    @ApiModelProperty("时间")
    private Date time;

    /**
     * 日期维度-根据日期初始化数据
     *
     * @param time 日期
     */
    public CustomerActivityOfDateVO(Date time) {
        this.time = time;
        super.setChatCustomerCnt(0);
        super.setUserSendMessageCnt(0);
        super.setCustomerSendMessageCnt(0);
    }
}
