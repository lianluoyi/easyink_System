package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.WeTagStatistic;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;

/**
 * 数据统计-标签统计-图表视图VO
 *
 * @author lichaoyu
 * @date 2023/5/8 9:32
 */
@Data
public class WeTagCustomerStatisticsChartVO extends WeTagStatisticsChartBaseVO {

    /**
     * 客户标签组信息
     */
    private List<WeTagCustomerStatisticsVO> groupTagList;

    public WeTagCustomerStatisticsChartVO() {
    }

    /**
     * 组装返回的数据
     *
     * @param weTagGroup 标签组信息
     * @param singleGroupTagList 单个标签组下的所有标签和客户信息
     * @param weTagList 去重后的标签-客户关系列表
     */
    public WeTagCustomerStatisticsChartVO(WeTagGroup weTagGroup, List<WeTagCustomerStatisticsVO> singleGroupTagList, List<WeTagStatistic> weTagList) {
        this.groupTagList = singleGroupTagList;
        super.setTagGroupId(weTagGroup.getGroupId());
        super.setGroupTagName(weTagGroup.getGroupName());
        super.setGroupTagCreateTime(DateUtils.getDateTime(weTagGroup.getCreateTime()));
        super.setTotalCustomerCnt(singleGroupTagList.stream().mapToInt(WeTagCustomerStatisticsVO::getCustomerCnt).sum());
        handleTagGroupCustomerCnt(singleGroupTagList, weTagList);
    }


    /**
     * 处理标签组下重复的客户数
     *
     * @param singleGroupTagList 单个标签组下的所有标签和客户信息
     * @param weTagList 去重后的标签-客户关系列表
     */
    public void handleTagGroupCustomerCnt(List<WeTagCustomerStatisticsVO> singleGroupTagList, List<WeTagStatistic> weTagList) {
        if (CollectionUtils.isEmpty(singleGroupTagList)) {
            return;
        }
        // 客户ID信息
        HashSet externalUserIdSet = new HashSet();
        for (WeTagCustomerStatisticsVO singleList : singleGroupTagList) {
            for (WeTagStatistic allWeTagList: weTagList) {
                if (singleList.getTagId().equals(allWeTagList.getTagId())) {
                    externalUserIdSet.add(allWeTagList.getExternalUserid());
                }
            }
        }
       super.setTagGroupCustomerCnt(externalUserIdSet.size());
    }

}