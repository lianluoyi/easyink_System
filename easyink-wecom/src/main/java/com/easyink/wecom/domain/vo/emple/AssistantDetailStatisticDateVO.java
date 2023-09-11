package com.easyink.wecom.domain.vo.emple;

import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.GenConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.easyink.common.constant.emple.CustomerAssistantConstants.NULL_VALUE;

/**
 * 获客链接详情-日期维度VO
 *
 * @author lichaoyu
 * @date 2023/8/25 16:59
 */
@Data
@NoArgsConstructor
public class AssistantDetailStatisticDateVO {

    @ApiModelProperty("日期，格式为YYYY-MM-DD")
    @Excel(name = "日期", sort = 1)
    private String date;

    @ApiModelProperty("累计添加客户数")
    @Excel(name = "累计添加客户数", sort = 2)
    private Integer accumulateCustomerCnt;

    @ApiModelProperty("新增客户数")
    @Excel(name = "新增客户数", sort = 3)
    private Integer newCustomerCnt;
    @ApiModelProperty("流失客户数")
    @Excel(name = "流失客户数", sort = 4)
    private Integer lossCustomerCnt;

    @ApiModelProperty("新增且未流失的客户数")
    private Integer currentNewCustomerCnt;

    @ApiModelProperty("新客留存率")
    @Excel(name = "新客留存率", sort = 5)
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

    public AssistantDetailStatisticDateVO(String date) {
        this.date = date;
        this.accumulateCustomerCnt = 0;
        this.lossCustomerCnt = 0;
        this.newCustomerCnt = 0;
        this.currentNewCustomerCnt = 0;
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

    /**
     * 详情-日期维度，处理累计客户数
     *
     * @param beforeDateAccumulateCnt 前一天的累计客户数
     */
    public void handelAccumulateCnt(Integer beforeDateAccumulateCnt) {
        this.accumulateCustomerCnt += beforeDateAccumulateCnt;
    }
}
