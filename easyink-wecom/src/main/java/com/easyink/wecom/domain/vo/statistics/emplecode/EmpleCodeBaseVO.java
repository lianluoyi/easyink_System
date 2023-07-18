package com.easyink.wecom.domain.vo.statistics.emplecode;

import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.GenConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 活码统计-基类VO
 *
 * @author lichaoyu
 * @date 2023/7/4 10:08
 */
@Data
@NoArgsConstructor
public class EmpleCodeBaseVO {

    @ApiModelProperty("累计添加客户")
    @Excel(name = "累计添加客户", sort = 2)
    private Integer accumulateCustomerCnt = 0;

    @ApiModelProperty("留存客户总数")
    @Excel(name = "留存客户总数", sort = 3)
    private Integer retainCustomerCnt = 0;

    @ApiModelProperty("新增客户数")
    @Excel(name = "新增客户数", sort = 4)
    private Integer newCustomerCnt = 0;

    @ApiModelProperty("流失客户数")
    @Excel(name = "流失客户数", sort = 5)
    private Integer lossCustomerCnt = 0;

    @ApiModelProperty("截止当前时间，新增客户数")
    private Integer currentNewCustomerCnt = 0;

    @ApiModelProperty("新客留存率")
    @Excel(name = "新客留存率", sort = 6)
    private String retainNewCustomerRate;

    /**
     * 留存率为空时返回的值
     */
    private final String NULL_VALUE = "-";


    /**
     * 新客留存率 公式：截止当前时间，新增客户数 / 新增客户数
     */
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
     * 绑定导出数据
     * 导出框架不能直接使用get方法获取属性值
     */
    public void bindExportData() {
        if (newCustomerCnt == 0) {
            retainNewCustomerRate = NULL_VALUE;
        } else {
            retainNewCustomerRate = getRetainNewCustomerRate() + GenConstants.PERCENT;
        }
    }

    public EmpleCodeBaseVO(Integer accumulateCustomerCnt, Integer retainCustomerCnt, Integer newCustomerCnt, Integer lossCustomerCnt, Integer currentNewCustomerCnt) {
        this.accumulateCustomerCnt = accumulateCustomerCnt;
        this.retainCustomerCnt = retainCustomerCnt;
        this.newCustomerCnt = newCustomerCnt;
        this.lossCustomerCnt = lossCustomerCnt;
        this.currentNewCustomerCnt = currentNewCustomerCnt;
        this.retainNewCustomerRate = getRetainNewCustomerRate();
    }
}
