package com.easyink.wecom.domain.dto;

import com.easyink.common.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 首页基础数据DTO
 *
 * @author admin
 * @description
 * @date 2021/2/25 11:21
 **/
@Data
public class WePageStaticDataDTO {

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 今日数据
     */
    private PageStaticData today;

    /**
     * 本周数据
     */
    private PageStaticData week;

    /**
     * 本月数据
     */
    private PageStaticData month;

    @Data
    public static class PageStaticData {
        /**
         * 发起申请数
         */
        private Integer newApplyCnt;

        /**
         * 发起申请数差值
         */
        private Integer newApplyCntDiff;

        /**
         * 新增客户数
         */
        private Integer newContactCnt;

        /**
         * 新增客户数差值
         */
        private Integer newContactCntDiff;

        /**
         * 群新增人数
         */
        private Integer newMemberCnt;

        /**
         * 群新增人数差值
         */
        private Integer newMemberCntDiff;

        /**
         * 流失客户数
         */
        private Integer negativeFeedbackCnt;

        /**
         * 流失客户数差值
         */
        private Integer negativeFeedbackCntDiff;
        /**
         * 新客流失率
         */
        private String newContactRetentionRate;
        /**
         * 新客流失率差值
         */
        private String newContactRetentionRateDiff;

        /**
         *  客户群总数
         */
        private Integer chatTotal ;
        /**
         * 客户群总数 增幅/涨幅
         */
        private Integer chatTotalDiff ;
        /**
         * 群成员总数
         */
        private Integer memberTotal ;
        /**
         * 群成员总数 增幅涨幅
         */
        private Integer memberTotalDiff ;

        /**
         * 图表数据
         */
        private List<WePageCountDTO> dataList;
        /**
         * 时间段内加入的新客流失数 , 此数据 企微官方统计接口没有返回,所以由系统定时任务自行统计
         */
        private Integer newContactLossCnt;
        /**
         * 新增客户群人数
         */
        private Integer newChatCnt;
        /**
         * 新增客户群人数（相比之前)
         */
        private Integer newChatCntDiff ;
        @ApiModelProperty("截止到现在的有效客户数，客户状态为正常、待继承、转接中（0、3、4）")
        private Integer currentNewCustomerCnt;


        /**
         * 获取新客留存率   截止当前的有效客户数 / 新增客户数
         * @return 新客留存率
         */
        public String getNewContactRetentionRate() {
            if (newContactCnt == 0 || newContactCnt == null || currentNewCustomerCnt == null) {
                return Constants.EMPTY_RETAIN_RATE_VALUE;
            }
            if(newContactCnt == 0) {
                return new BigDecimal(100).toPlainString();
            }
            // 百分比
            BigDecimal newCntDecimal = new BigDecimal(newContactCnt) ;
            BigDecimal currCustomerCntDecimal = new BigDecimal(currentNewCustomerCnt) ;
            int scale = 2 ;
            return currCustomerCntDecimal
                    .multiply(new BigDecimal(100))
                    .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                    .stripTrailingZeros().toPlainString();
        }

    }
}
