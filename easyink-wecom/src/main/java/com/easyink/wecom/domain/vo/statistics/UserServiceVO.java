package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.easyink.wecom.domain.vo.UserBaseVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 员工服务VO
 *
 * @author wx
 * 2023/2/14 14:37
 **/
@Data
public class UserServiceVO extends UserBaseVO {

    @ApiModelProperty("聊天总数")
    @Excel(name = "聊天总数", sort = 3)
    private Integer chatTotal;

    @ApiModelProperty("发送聊天数")
    @Excel(name = "发送聊天数", sort = 4)
    private Integer sendContactCnt;

    @ApiModelProperty("平均会话数")
    @Excel(name = "平均会话数", sort = 5)
    private String averageChatTotal;

    @ApiModelProperty("平均首次回复时长")
    @Excel(name = "平均首次回复时长", sort = 6)
    private String averageFirstReplyDuration;

    @ApiModelProperty("回复率")
    @Excel(name = "回复率", sort = 7)
    private String replyRate;

    @ApiModelProperty("有效沟通客户数")
    @Excel(name = "有效沟通客户数", sort = 8)
    private Integer effectiveCommunicationCustomerCnt;

    @ApiModelProperty("有效沟通率")
    @Excel(name = "有效沟通率", sort = 9)
    private Integer effectiveCommunicationRate;

    @ApiModelProperty("客户好评率")
    @Excel(name = "客户好评率", sort = 10)
    private Integer customerPositiveCommentsRate;

    /**
     * 客户主动发起聊天数
     */
    @JsonIgnore
    private Integer customerActiveStartContactCnt;

    /**
     *  客户主动发起聊天后，员工在当天有回复消息的聊天数
     */
    @JsonIgnore
    private Integer userReplyContactCnt;

    /**
     * 员工收到的得分
     */
    @JsonIgnore
    private Integer score;

    /**
     * 评价人数
     */
    @JsonIgnore
    private Integer num;

    /**
     * 获取平均会话数  发送聊天数 / 聊天总数
     *
     * @return 平均会话数
     */
    public String getAverageChatTotal(){
        if (chatTotal == null || sendContactCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        if(chatTotal == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal sendContactCntDecimal = new BigDecimal(sendContactCnt);
        BigDecimal chatTotalDecimal = new BigDecimal(chatTotal);
        int scale = 2;
        // 计算平均会话数  发送聊天数 / 聊天总数
        return sendContactCntDecimal
                .divide(chatTotalDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取好评率  员工收到的评价 / (评价人数 * 10)
     *
     * @return 好评率
     */
    public String getCustomerPositiveCommentsRate() {
        if (num == null || score == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(num == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal scoreDecimal = new BigDecimal(score);
        BigDecimal numDecimal = new BigDecimal(num);
        int scale = 2;
        // 计算好评率  员工收到的评价 / (评价人数 * 10)
        return scoreDecimal
                .multiply(percent)
                .divide(numDecimal.multiply(new BigDecimal(10)), scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取回复率  客户主动发起聊天后，员工在当天有回复消息的聊天数 / 客户主动发起聊天数
     *
     * @return 回复率
     */
    public String getReplyRate() {
        if (userReplyContactCnt == null || customerActiveStartContactCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(customerActiveStartContactCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal userReplyContactCntDecimal = new BigDecimal(userReplyContactCnt);
        BigDecimal customerActiveStartContactCntDecimal = new BigDecimal(customerActiveStartContactCnt);
        int scale = 2;
        // 计算回复率  客户主动发起聊天后，员工在当天有回复消息的聊天数 / 客户主动发起聊天数
        return userReplyContactCntDecimal
                .multiply(percent)
                .divide(customerActiveStartContactCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取有效沟通率  有效沟通客户数数 / 聊天总数
     *
     * @return 有效沟通率
     */
    public String getEffectiveCommunicationRate() {
        if (effectiveCommunicationCustomerCnt == null || chatTotal == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(effectiveCommunicationCustomerCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal chatTotalDecimal = new BigDecimal(chatTotal);
        BigDecimal effectiveCommunicationCustomerCntDecimal = new BigDecimal(effectiveCommunicationCustomerCnt);
        int scale = 2;
        // 计算有效沟通率  有效沟通客户数数 / 聊天总数
        return effectiveCommunicationCustomerCntDecimal
                .multiply(percent)
                .divide(chatTotalDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

}
