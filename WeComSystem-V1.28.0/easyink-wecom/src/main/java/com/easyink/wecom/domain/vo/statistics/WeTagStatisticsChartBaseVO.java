package com.easyink.wecom.domain.vo.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据统计-标签统计-图表视图基础类
 *
 * @author lichaoyu
 * @date 2023/5/9 14:10
 */
@Data
@NoArgsConstructor
public class WeTagStatisticsChartBaseVO {

    /**
     * 标签组ID
     */

    private String tagGroupId;

    /**
     * 标签组名称
     */
    private String groupTagName;

    /**
     * 标签组创建时间
     */
    private String groupTagCreateTime;

    /**
     * 标签组下的客户总数
     */
    private Integer totalCustomerCnt;

    /**
     * 标签组下去重后的客户总数
     */
    private Integer tagGroupCustomerCnt = 0;
}
