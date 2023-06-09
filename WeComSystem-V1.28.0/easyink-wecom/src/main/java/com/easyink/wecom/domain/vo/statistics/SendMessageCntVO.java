package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 发送消息数VO
 *
 * @author wx
 * 2023/2/14 14:46
 **/
@Data
@NoArgsConstructor
public class SendMessageCntVO {
    @ApiModelProperty("员工发送消息数")
    @Excel(name = "员工发送消息数",sort = 4)
    private Integer userSendMessageCnt;

    @ApiModelProperty("客户发送消息数")
    @Excel(name = "客户发送消息数", sort = 5)
    private Integer customerSendMessageCnt;

    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date time;
}
