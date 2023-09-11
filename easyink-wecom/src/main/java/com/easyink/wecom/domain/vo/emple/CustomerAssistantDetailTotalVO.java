package com.easyink.wecom.domain.vo.emple;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.easyink.common.constant.emple.CustomerAssistantConstants.NULL_VALUE;

/**
 * 获客助手-详情统计数据VO
 *
 * @author lichaoyu
 * @date 2023/8/24 16:07
 */
@Data
public class CustomerAssistantDetailTotalVO {

    @ApiModelProperty("累计客户数")
    private Integer accumulateCustomerCnt;
    @ApiModelProperty("今日新增客户数")
    private Integer newCustomerCnt;
    @ApiModelProperty("今日流失客户数")
    private Integer lossCustomerCnt;
    @ApiModelProperty("今日新增且未流失的客户数")
    private Integer currentNewCustomerCnt;
    @ApiModelProperty("今日新客留存率")
    private String retainNewCustomerRate;

    public String getRetainNewCustomerRate() {
        if (currentNewCustomerCnt == null || newCustomerCnt == null) {
            return NULL_VALUE;
        }
        BigDecimal percent = new BigDecimal(100);
        if(newCustomerCnt == 0) {
            return NULL_VALUE;
        }
        // 百分比
        BigDecimal currCntDecimal = new BigDecimal(currentNewCustomerCnt);
        BigDecimal newCntDecimal = new BigDecimal(newCustomerCnt);
        int scale = 2;
        // 计算留存率  截止当前时间,新增客户数 / 新客数
        return currCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    /**
     * 当没有查询到获客总览信息时，构造默认值返回
     */
    public CustomerAssistantDetailTotalVO() {
        this.accumulateCustomerCnt = 0;
        this.newCustomerCnt = 0;
        this.lossCustomerCnt = 0;
        this.currentNewCustomerCnt = 0;
    }
}
