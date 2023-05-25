package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeGroupTag;
import com.easyink.wecom.domain.WeGroupTagCategory;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupListVO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupStatisticChartVO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupStatisticsVO;

import java.util.List;

/**
 * 类名：WeGroupTagService
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
public interface WeGroupTagService extends IService<WeGroupTag> {

    /**
     * 批量插入标签列表
     *
     * @param corpId     企业ID
     * @param groupTagId 群标签组ID
     * @param list       list
     * @return int
     */
    int batchInsert(String corpId, Long groupTagId, List<WeGroupTag> list);

    /**
     * 批量删除标签
     *
     * @param corpId 企业ID
     * @param idList idList
     * @return int
     */
    int delTag(String corpId, List<Long> idList);

    /**
     * 根据groupId批量删除标签
     *
     * @param corpId      企业ID
     * @param groupIdList 标签组ID集合
     * @return int
     */
    int delByGroupId(String corpId, List<Long> groupIdList);

    /**
     * 获取群标签表格视图的数据
     *
     * @param weTagStatisticsDTO 查询条件
     * @return List<WeTagStatisticsVO>
     */
    List<WeTagGroupStatisticsVO> groupTagTableView(WeTagStatisticsDTO weTagStatisticsDTO);

    /**
     * 获取群标签图表视图数据
     *
     * @param weTagStatisticsDTO 查询条件
     * @return List<WeTagStatisticChartVO>
     */
    List<WeTagGroupStatisticChartVO> groupTagChartView(WeTagStatisticsDTO weTagStatisticsDTO);

    /**
     * 导出群标签数据
     *
     * @param weTagStatisticsDTO 查询条件
     * @return AjaxResult
     */
    AjaxResult exportGroupTags(WeTagStatisticsDTO weTagStatisticsDTO);

    /**
     * 获取所有群标签和标签id
     *
     * @param weTagStatisticsDTO 查询条件
     * @return List<WeTagGroupListVO>
     */
    List<WeTagGroupListVO> groupTagList(WeTagStatisticsDTO weTagStatisticsDTO);
}
