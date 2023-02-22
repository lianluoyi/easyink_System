package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.easyink.wecom.domain.vo.UserBaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 客户概况VO
 *
 * @author wx
 * 2023/2/13 18:20
 **/
@Data
@NoArgsConstructor
public class CustomerOverviewVO extends UserBaseVO {
    /**
     * 日期 , 格式 ：  '%Y-%m-%d'
     */
    private String xTime;

    /**
     * 客户总数
     */
    @Excel(name = "客户总数", sort = 3)
    private Integer totalContactCnt;

    /**
     * 新增客户数，成员新添加的客户数量
     */
    @Excel(name = "新增客户数", sort = 4)
    private Integer newContactCnt;

    /**
     * 当天加入的新客流失数量 , 因为官方没有返回由系统自行统计
     */
    @Excel(name = "流失客户数", sort = 5)
    private Integer contactLossCnt;

    /**
     * 新客流失客户数
     */
    private Integer newContactLossCnt;

    /**
     * 新客留存率
     */
    @Excel(name = "新客留存率", sort = 6)
    private String newContactRetentionRate;

    /**
     * 新客开口率
     */
    @Excel(name = "新客开口率", sort = 7)
    private String newContactStartTalkRate;

    /**
     * 服务响应率
     */
    @Excel(name = "服务响应率", sort = 8)
    private String serviceResponseRate;

    /**
     * 当天新增客户中与员工对话过的人数
     */
    private Integer newContactSpeakCnt;

    /**
     * 当天员工首次给客户发消息，客户在30分钟内回复的客户数
     */
    private Integer repliedWithinThirtyMinCustomerCnt;

    /**
     * 当天员工会话数-不区分是否为员工主动发起
     */
    private Integer allChatCnt;

    /**
     * 获取新客留存率   流失客户数/ 新增客户数
     *
     * @return 新客留存率
     */
    public String getNewContactRetentionRate() {
        if (newContactCnt == null || newContactLossCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(newContactCnt == 0) {
            return percent.toPlainString();
        }
        // 百分比
        BigDecimal newCntDecimal = new BigDecimal(newContactCnt);
        BigDecimal lossCntDecimal = new BigDecimal(newContactLossCnt);
        int scale = 2;
        // 计算留存率  新客数-流失数/新客数
        return  newCntDecimal.subtract(lossCntDecimal)
                        .multiply(percent)
                        .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取新客开口率   当天新增客户中与员工对话过的人数 / 新增客户数
     *
     * @return 新客开口率
     */
    public String getNewContactStartTalkRate() {
        if (newContactCnt == null || newContactSpeakCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(newContactCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal newCntDecimal = new BigDecimal(newContactCnt);
        BigDecimal newContactSpeakCntDecimal = new BigDecimal(newContactSpeakCnt);
        int scale = 2;
        // 计算新客开口率  开口人数/新客数
        return newContactSpeakCntDecimal
                        .multiply(percent)
                        .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取服务响应率   当天员工首次给客户发消息，客户在30分钟内回复的客户数 / 会话客户数
     *
     * @return 服务响应率
     */
    public String getServiceResponseRate() {
        if (allChatCnt == null || repliedWithinThirtyMinCustomerCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(allChatCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal allChatCntDecimal = new BigDecimal(allChatCnt);
        BigDecimal repliedWithinThirtyMinCustomerCntDecimal = new BigDecimal(repliedWithinThirtyMinCustomerCnt);
        int scale = 2;
        // 计算服务响应率  当天员工首次给客户发消息，客户在30分钟内回复的客户数 / 会话客户数
        return repliedWithinThirtyMinCustomerCntDecimal
                .multiply(percent)
                .divide(allChatCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }


}
