package com.easyink.wecom.domain.dto.statistics;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工服务查询DTO
 *
 * @author wx
 * 2023/2/14 18:31
 **/
@Data
public class UserServiceDTO extends StatisticsDTO{
    @ApiModelProperty("聊天总数排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String chatTotalSort;

    @ApiModelProperty("发送聊天数排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String sendContactCntSort;

    @ApiModelProperty("平均会话数排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String averageChatTotalSort;

    @ApiModelProperty("平均首次回复时长排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String averageFirstReplyDurationSort;

    @ApiModelProperty("回复率排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String replyRateSort;

    @ApiModelProperty("有效沟通客户数排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String effectiveCommunicationCustomerCntSort;

    @ApiModelProperty("有效沟通率排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String effectiveCommunicationRateSort;

    @ApiModelProperty("客户好评率排序 正序 ASC 倒叙 DESC 不需要排序 null")
    private String customerPositiveCommentsRateSort;


}
