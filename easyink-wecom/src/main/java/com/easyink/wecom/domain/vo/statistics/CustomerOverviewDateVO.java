package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.GenConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 客户概况-日期维度VO
 *
 * @author lichaoyu
 * @date 2023/4/18 17:44
 */
@Data
@NoArgsConstructor
public class CustomerOverviewDateVO {

    /**
     * 日期 , 格式 ：  '%Y-%m-%d'
     */
    @Excel(name = "日期", sort = 1)
    private String xTime;

    /**
     * 客户总数
     */
    @Excel(name = "客户总数", sort = 2)
    private Integer totalContactCnt;

    /**
     * 新增客户数，成员新添加的客户数量
     */
    @Excel(name = "新增客户数", sort = 4)
    private Integer newContactCnt;

    /**
     * 当天加入的新客流失数量 , 因为官方没有返回由系统自行统计
     */
    @Excel(name = "流失客户数", sort = 3)
    private Integer contactLossCnt;

    /**
     * 新客流失客户数
     */
    private Integer newContactLossCnt;

    /**
     * 新客留存率
     */
    @Excel(name = "新客留存率", sort = 5)
    private String newContactRetentionRate;

    /**
     * 用于排序的新客留存率字段
     */
    private BigDecimal newContactRetentionRateBySort;

    /**
     * 新客开口率
     */
    @Excel(name = "新客开口率", sort = 6)
    private String newContactStartTalkRate;

    /**
     * 用于排序的新客开口率字段
     */
    private BigDecimal newContactStartTalkRateBySort;

    /**
     * 服务响应率
     */
    @Excel(name = "服务响应率", sort = 7)
    private String serviceResponseRate;

    /**
     * 用于排序的服务响应率字段
     */
    private BigDecimal serviceResponseRateBySort;

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
     * 当天员工主动发送消息数
     */
    private Integer userActiveChatCnt;

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
        newContactRetentionRateBySort = newCntDecimal.subtract(lossCntDecimal)
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return  newContactRetentionRateBySort.toPlainString();
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
        newContactStartTalkRateBySort = newContactSpeakCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return newContactStartTalkRateBySort.toPlainString();
    }

    /**
     * 获取服务响应率   当天员工首次给客户发消息，客户在30分钟内回复的客户数 / 会话客户数
     *
     * @return 服务响应率
     */
    public String getServiceResponseRate() {
        if (userActiveChatCnt == null || repliedWithinThirtyMinCustomerCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(userActiveChatCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        // 百分比
        BigDecimal userActiveChatCntDecimal = new BigDecimal(userActiveChatCnt);
        BigDecimal repliedWithinThirtyMinCustomerCntDecimal = new BigDecimal(repliedWithinThirtyMinCustomerCnt);
        int scale = 2;
        // 计算服务响应率  当天员工首次给客户发消息，客户在30分钟内回复的客户数 / 会话客户数
        serviceResponseRateBySort = repliedWithinThirtyMinCustomerCntDecimal
                .multiply(percent)
                .divide(userActiveChatCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return serviceResponseRateBySort.toPlainString();
    }

    /**
     * 绑定导出数据
     * 导出框架不能直接使用get方法获取属性值
     */
    public void bindExportData() {
        newContactRetentionRate = getNewContactRetentionRate() + GenConstants.PERCENT;
        newContactStartTalkRate = getNewContactStartTalkRate() + GenConstants.PERCENT;
        serviceResponseRate = getServiceResponseRate() + GenConstants.PERCENT;
    }

    public CustomerOverviewDateVO(String xTime) {
        this.xTime = xTime;
        this.allChatCnt = 0;
        this.totalContactCnt = 0;
        this.contactLossCnt = 0;
        this.newContactLossCnt = 0;
        this.newContactCnt = 0;
        this.newContactSpeakCnt = 0;
        this.repliedWithinThirtyMinCustomerCnt = 0;
        this.newContactRetentionRateBySort = BigDecimal.valueOf(100);
        this.newContactStartTalkRateBySort = BigDecimal.valueOf(0);
        this.serviceResponseRateBySort = BigDecimal.valueOf(0);
        this.userActiveChatCnt=0;
    }

    /**
     * 原始数据处理
     *
     * @param allChatCnt 当天会话数
     * @param contactTotalCnt  当天员工客户总数
     * @param negativeFeedbackCnt 删除/拉黑成员的客户数
     * @param newCustomerLossCnt 当天员工新客流失客户数
     * @param newContactCnt 新增客户数
     * @param newContactSpeakCnt 当天新增客户中与员工对话过的人数
     * @param repliedWithinThirtyMinCustomerCnt 当天员工首次给客户发消息，客户在30分钟内回复的客户数
     */
    public void handleAddData(Integer allChatCnt, Integer contactTotalCnt, Integer negativeFeedbackCnt, Integer newCustomerLossCnt, Integer newContactCnt, Integer newContactSpeakCnt, Integer repliedWithinThirtyMinCustomerCnt,Integer userActiveChatCnt){
        this.allChatCnt += allChatCnt;
        this.totalContactCnt += contactTotalCnt;
        this.contactLossCnt += negativeFeedbackCnt;
        this.newContactLossCnt += newCustomerLossCnt;
        this.newContactCnt += newContactCnt;
        this.newContactSpeakCnt += newContactSpeakCnt;
        this.repliedWithinThirtyMinCustomerCnt += repliedWithinThirtyMinCustomerCnt;
        this.userActiveChatCnt+=userActiveChatCnt;
        this.newContactRetentionRate = getNewContactRetentionRate();
        this.newContactStartTalkRate = getNewContactStartTalkRate();
        this.serviceResponseRate = getServiceResponseRate();
    }
}
