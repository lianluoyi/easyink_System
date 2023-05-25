package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeGroupTagCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据统计-标签统计-群标签图表视图VO
 *
 * @author lichaoyu
 * @date 2023/5/8 9:32
 */
@Data
@NoArgsConstructor
public class WeTagGroupStatisticChartVO extends WeTagStatisticsChartBaseVO{

    /**
     * 标签组信息
     */
    private List<WeTagGroupStatisticsVO> groupTagList;
    public WeTagGroupStatisticChartVO(WeGroupTagCategory weGroupTagCategory,List<WeTagGroupStatisticsVO> tmpList,int totalCnt ,int tagGroupCustomerCnt){
        this.setGroupTagName(weGroupTagCategory.getName());
        this.setTagGroupId(String.valueOf(weGroupTagCategory.getId()));
        this.setGroupTagList(tmpList);
        this.setTotalCustomerCnt(totalCnt);
        this.setGroupTagCreateTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, weGroupTagCategory.getCreateTime()));
        this.setTagGroupCustomerCnt(tagGroupCustomerCnt);
    }


}
