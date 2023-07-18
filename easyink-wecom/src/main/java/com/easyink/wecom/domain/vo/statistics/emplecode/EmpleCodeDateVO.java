package com.easyink.wecom.domain.vo.statistics.emplecode;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活码统计-日期维度VO
 *
 * @author lichaoyu
 * @date 2023/7/5 20:56
 */
@Data
@NoArgsConstructor
public class EmpleCodeDateVO extends EmpleCodeBaseVO{

    @ApiModelProperty("日期")
    @Excel(name = "日期", sort = 1)
    private String time;

    public EmpleCodeDateVO(String time) {
        this.time = time;
        super.setAccumulateCustomerCnt(0);
        super.setRetainCustomerCnt(0);
        super.setNewCustomerCnt(0);
        super.setLossCustomerCnt(0);
        super.setCurrentNewCustomerCnt(0);
    }

    /**
     * 处理数据
     *
     * @param accumulateCustomerCnt 累计客户数
     * @param retainCustomerCnt 留存客户总数
     * @param newCustomerCnt 新增客户数
     * @param lossCustomerCnt 流失客户数
     * @param currentNewCustomerCnt 截至当前时间，新增客户数
     */
    public void handleData(int accumulateCustomerCnt, int retainCustomerCnt, int newCustomerCnt, int lossCustomerCnt, int currentNewCustomerCnt) {
        super.setAccumulateCustomerCnt(accumulateCustomerCnt);
        super.setRetainCustomerCnt(retainCustomerCnt);
        super.setNewCustomerCnt(newCustomerCnt);
        super.setLossCustomerCnt(lossCustomerCnt);
        super.setCurrentNewCustomerCnt(currentNewCustomerCnt);
    }
}
