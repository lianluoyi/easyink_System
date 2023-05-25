package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据统计-标签统计-群标签表格视图VO
 *
 * @author lichaoyu
 * @date 2023/5/6 10:14
 */
@Data
@NoArgsConstructor
public class WeTagGroupStatisticsVO extends WeTagStatisticsBaseVO {


    /**
     * 标签下客户群数量
     */
    @Excel(name = "标签下客户群",sort = 3)
    private Integer customerCnt;

    /**
     * 标签下客户群id
     */
    @JsonIgnore
    private List<String> chatList;

}
