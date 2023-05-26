package com.easyink.wecom.domain.dto.statistics;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据统计-标签统计查询
 *
 * @author lichaoyu
 * @date 2023/5/6 10:07
 */
@Data
public class WeTagStatisticsDTO extends StatisticsDTO{

    /**
     * 标签组ID
     */
    private List<String> tagGroupIds;

    /**
     * 排序类型名称
     */
    private String sortName;

    /**
     * 排序方式 正序ASC 倒叙DESC
     */
    private String sortType;

    /**
     * 客户关系ID列表
     */
    private List<String> flowerCustomerRelIdList;

    /**
     * 标签ID列表
     */
    private List<String> tagIdList = new ArrayList<>();
}
