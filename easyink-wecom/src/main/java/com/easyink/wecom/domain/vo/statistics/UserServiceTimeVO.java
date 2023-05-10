package com.easyink.wecom.domain.vo.statistics;



import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.GenConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 *员工服务时间维度VO
 *
 * @author zhaorui
 * 2023/4/20 16:03
 */
@Data
@Builder
public class UserServiceTimeVO {

    private final static String TIME_SUFFIX="分钟";

    @ApiModelProperty("消息时间")
    @Excel(name = "时间",sort = 2)
    private String time;

    @ApiModelProperty("聊天总数")
    @Excel(name = "聊天总数", sort = 3)
    @Builder.Default
    private Integer chatTotal=0;

    @ApiModelProperty("发送聊天数")
    @Excel(name = "发送聊天数", sort = 4)
    @Builder.Default
    private Integer sendContactCnt=0;

    @ApiModelProperty("平均会话数")
    @Excel(name = "平均会话数", sort = 5)
    @Builder.Default
    private String averageChatTotal="0";

    /**
     * 用于排序的平均会话数
     */
    @Builder.Default
    private BigDecimal averageChatTotalTmp=BigDecimal.valueOf(0);

    @ApiModelProperty("平均首次回复时长")
    @Excel(name = "平均首次回复时长", sort = 6)
    @Builder.Default
    private String averageFirstReplyDuration="0";
    /**
     * 用于排序的平均首次回复时长
     */
    @Builder.Default
    private BigDecimal averageFirstReplyDurationTmp=BigDecimal.valueOf(0);

    @ApiModelProperty("回复率")
    @Excel(name = "回复率", sort = 7)
    @Builder.Default
    private String replyRate="0";
    /**
     * 用于排序的回复率
     */
    @Builder.Default
    private BigDecimal replayRateTmp= BigDecimal.valueOf(0);

    @ApiModelProperty("有效沟通客户数")
    @Excel(name = "有效沟通客户数", sort = 8)
    @Builder.Default
    private Integer effectiveCommunicationCustomerCnt=0;

    @ApiModelProperty("有效沟通率")
    @Excel(name = "有效沟通率", sort = 9)
    @Builder.Default
    private String effectiveCommunicationRate="0";
    /**
     * 用于排序的有效沟通率
     */
    @Builder.Default
    private BigDecimal effectiveCommunicationRateTmp=BigDecimal.valueOf(0);

    @ApiModelProperty("客户好评率")
    @Excel(name = "客户好评率", sort = 10)
    @Builder.Default
    private String customerPositiveCommentsRate="0";
    /**
     * 用于排序的客户好评率
     */
    @Builder.Default
    private BigDecimal customerPositiveCommentsRateTmp=BigDecimal.valueOf(0);

    /**
     * 客户主动发起聊天数
     */
    @JsonIgnore
    @Builder.Default
    private Integer customerActiveStartContactCnt=0;

    /**
     *  客户主动发起聊天后，员工在当天有回复消息的聊天数
     */
    @JsonIgnore
    @Builder.Default
    private Integer userReplyContactCnt=0;

    /**
     * 员工收到的得分
     */
    @JsonIgnore
    @Builder.Default
    private Integer score=0;

    /**
     * 评价人数
     */
    @JsonIgnore
    @Builder.Default
    private Integer num=0;

    /**
     * 员工回复客户首次消息的间隔时间
     */
    @JsonIgnore
    @Builder.Default
    private Integer firstReplyTimeIntervalAlterReceive=0;


    /**
     * 导出数据进行后缀绑定
     */
    public void bindExportData(){
        effectiveCommunicationRate = getEffectiveCommunicationRate() + GenConstants.PERCENT;
        replyRate = getReplyRate() + GenConstants.PERCENT;
        customerPositiveCommentsRate = getCustomerPositiveCommentsRate() + GenConstants.PERCENT;
        averageChatTotal = getAverageChatTotal();
        averageFirstReplyDuration += TIME_SUFFIX;
    }

    /**
     * 增加聊天总数
     */
    public void addChatTotal(){
        chatTotal++;
    }

    /**
     * 增加发送消息数
     * @param number 发送消息数量
     */
    public void addSendContactCnt(int number){
        sendContactCnt+=number;
    }

    /**
     * 增加有效聊天数
     * @param number 有效聊天标识 1：有效 0：无效
     */
    public void addEffctiveCommunicationCustomerCnt(int number){
        effectiveCommunicationCustomerCnt+=number;
    }

    /**
     * 增加首次聊天回复时长
     * @param number 首次聊天回复时长
     */
    public void  addFirstReplyTimeIntervalAlterReceive(int number){
        firstReplyTimeIntervalAlterReceive+=number;
    }

    /**
     * 增加客户评价分数
     * @param number 客户评价分数
     */
    public void addScore(int number){
        score+=number;
    }

    /**
     * 增加参与聊天客户数
     */
    public void addNUm(){
        num++;
    }

    /**
     * 增加客户先开口聊天数
     */
    public void addCustomerActiveStartContactCnt(){
        customerActiveStartContactCnt++;
    }

    /**
     * 增加员工回复消息数
     */
    public void addUserReplyContactCnt(){
        userReplyContactCnt++;
    }

    /**
     * 对获取到的数据进行计算
     */
    public void calculateResult(){
        BigDecimal chatTotalDecimal=new BigDecimal(chatTotal);
        BigDecimal sendContactCntDecimal=new BigDecimal(sendContactCnt);
        BigDecimal effectiveCommunicationCustomerCntDecimal=new BigDecimal(effectiveCommunicationCustomerCnt);
        BigDecimal customerActiveStartContactCntDecimal=new BigDecimal(customerActiveStartContactCnt);
        BigDecimal userReplyContactCntDecimal=new BigDecimal(userReplyContactCnt);
        BigDecimal firstReplyTimeIntervalAlterReceiveDecimal=new BigDecimal(firstReplyTimeIntervalAlterReceive);
        BigDecimal scoreDecimal=new BigDecimal(score);
        BigDecimal numDecimal=new BigDecimal(num);

        //计算参数
        int scale = 2;
        BigDecimal percent = new BigDecimal(100);
        //计算平均会话数
        if (chatTotal==0||sendContactCnt==0){
            averageChatTotalTmp=BigDecimal.ZERO;
            averageChatTotal=averageChatTotalTmp.toPlainString();
        }else {
            averageChatTotalTmp=sendContactCntDecimal
                    .divide(chatTotalDecimal, scale, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            averageChatTotal=averageChatTotalTmp.toPlainString();
        }
        //计算平均首次回复时长
        if (chatTotal==0||firstReplyTimeIntervalAlterReceive==0){
            averageFirstReplyDurationTmp=BigDecimal.ZERO;
            averageFirstReplyDuration=averageFirstReplyDurationTmp.toPlainString();
        }else {
            averageFirstReplyDurationTmp=firstReplyTimeIntervalAlterReceiveDecimal
                    .divide(customerActiveStartContactCntDecimal,scale,RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            averageFirstReplyDuration=averageFirstReplyDurationTmp.toPlainString();
        }
        //计算回复率
        if (userReplyContactCnt==0||customerActiveStartContactCnt==0){
            replyRate=BigDecimal.ZERO.toPlainString();
            replayRateTmp=BigDecimal.ZERO;
        } else {
            replayRateTmp=userReplyContactCntDecimal
                    .multiply(percent)
                    .divide(customerActiveStartContactCntDecimal, scale, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            replyRate=replayRateTmp.toPlainString();
        }
        //计算有效沟通率
        if (chatTotal==0||effectiveCommunicationCustomerCnt==0){
            effectiveCommunicationRate=BigDecimal.ZERO.toPlainString();
        }else {
           effectiveCommunicationRateTmp=effectiveCommunicationCustomerCntDecimal
                   .multiply(percent)
                   .divide(chatTotalDecimal, scale, RoundingMode.HALF_UP)
                   .stripTrailingZeros();
           effectiveCommunicationRate=effectiveCommunicationRateTmp.toPlainString();
        }
        //计算用户好评率
        if (num==0||score==0){
            customerPositiveCommentsRate=BigDecimal.ZERO.toPlainString();
        }else {
            customerPositiveCommentsRateTmp=scoreDecimal
                    .multiply(new BigDecimal(10))
                    .divide(numDecimal, scale, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            customerPositiveCommentsRate=customerPositiveCommentsRateTmp.toPlainString();
        }
    }


}
