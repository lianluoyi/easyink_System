package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据统计-标签统计-表格视图VO
 *
 * @author lichaoyu
 * @date 2023/5/6 10:14
 */
@Data
@NoArgsConstructor
public class WeTagCustomerStatisticsVO extends WeTagStatisticsBaseVO {

    /**
     * 标签下客户数量
     */
    @Excel(name = "标签下客户数", sort = 3)
    private Integer customerCnt = 0;

    /**
     * 客户Id
     */
    private String externalUserId;

    public void addCustomerCnt() {
        this.customerCnt++;
    }

}
