package com.easyink.wecom.domain.dto.statistics;


import com.easyink.common.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 员工服务查询DTO
 *
 * @author wx
 * 2023/2/14 18:31
 **/
@Data
public class UserServiceDTO extends StatisticsDTO{

    @ApiModelProperty("开始时间/添加时间的开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间/添加时间的结束时间")
    private String endTime;

    @Override
    public String getBeginTime() {
        return DateUtils.parseBeginDay(beginTime);
    }

    @Override
    public String getEndTime() {
        return DateUtils.parseEndDay(endTime);
    }

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

    @ApiModelProperty("需要进行排序的字段")
    private String sortName;

    @ApiModelProperty("排序类型")
    private String sortType;

    public String getChatTotalSort() {
        if (chatTotalSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(chatTotalSort) ? "ASC" : "DESC";
    }

    public String getSendContactCntSort() {
        if (sendContactCntSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(sendContactCntSort) ? "ASC" : "DESC";
    }

    public String getAverageChatTotalSort() {
        if (averageChatTotalSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(averageChatTotalSort) ? "ASC" : "DESC";
    }

    public String getAverageFirstReplyDurationSort() {
        if (averageFirstReplyDurationSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(averageFirstReplyDurationSort) ? "ASC" : "DESC";
    }

    public String getReplyRateSort() {
        if (replyRateSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(replyRateSort) ? "ASC" : "DESC";
    }

    public String getEffectiveCommunicationCustomerCntSort() {
        if (effectiveCommunicationCustomerCntSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(effectiveCommunicationCustomerCntSort) ? "ASC" : "DESC";
    }

    public String getEffectiveCommunicationRateSort() {
        if (effectiveCommunicationRateSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(effectiveCommunicationRateSort) ? "ASC" : "DESC";
    }

    public String getCustomerPositiveCommentsRateSort() {
        if (customerPositiveCommentsRateSort == null) {
            return null;
        }
        return "asc".equalsIgnoreCase(customerPositiveCommentsRateSort) ? "ASC" : "DESC";
    }

    /**
     * 排除员工List 客服好评表使用 如果客服好评表中查询员工不足分页的数量，则查询好评率为0的用户来补充
     */
    private List<String> exceptUserIds;
}
