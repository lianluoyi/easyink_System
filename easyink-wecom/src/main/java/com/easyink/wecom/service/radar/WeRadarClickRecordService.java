package com.easyink.wecom.service.radar;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.shorturl.model.RadarShortUrlAppendInfo;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.radar.*;
import com.easyink.wecom.domain.entity.radar.WeRadarClickRecord;
import com.easyink.wecom.domain.vo.radar.*;

import java.util.List;

/**
 * ClassName： WeRadarClickRecordService
 *
 * @author wx
 * @date 2022/7/19 19:56
 */
public interface WeRadarClickRecordService extends IService<WeRadarClickRecord> {
    /**
     * 保存雷达记录
     *
     * @param clickRecordDTO
     */
    void saveClickRecord(RadarClickRecordDTO clickRecordDTO);

    /**
     * 获取雷达点击数据总览
     *
     * @param radarId
     * @return
     */
    RadarRecordTotalVO getTotal(Long radarId);

    /**
     * 查询雷达数据统计（折线图）
     *
     * @param radarAnalyseDTO
     * @return
     */
    RadarAnalyseVO getTimeRangeAnalyseCount(SearchRadarAnalyseDTO radarAnalyseDTO);

    /**
     * 查询渠道排序
     *
     * @param radarId   雷达ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD
     * @param endTime   结束时间，格式为YYYY-MM-DD
     * @return {@link RadarChannelSortVO}
     */
    List<RadarChannelSortVO> getChannelSort(Long radarId, String beginTime, String endTime);

    /**
     * 获取客户点击记录
     *
     * @param customerRecordDTO
     * @return
     */
    List<RadarCustomerRecordVO> getCustomerClickRecord(SearchCustomerRecordDTO customerRecordDTO);

    /**
     * 获取渠道点击记录
     *
     * @param channelRecordDTO
     * @return
     */
    List<RadarChannelRecordVO> getChannelClickRecord(SearchChannelRecordDTO channelRecordDTO);

    /**
     * 获取渠道点击记录详情
     *
     * @param channelRecordDetailDTO
     * @return
     */
    List<RadarCustomerRecordVO> getChannelClickRecordDetail(SearchChannelRecordDetailDTO channelRecordDetailDTO);

    /**
     * 获取客户点击记录详情
     *
     * @param customerRecordDTO
     * @return
     */
    List<RadarCustomerClickRecordDetailVO> getCustomerClickRecordDetail(SearchCustomerRecordDetailDTO customerRecordDTO);

    /**
     * 创建雷达点击记录
     *
     * @param appendInfo 附件信息  {@link RadarShortUrlAppendInfo }
     * @param customer   客户信息 {@link WeCustomer}
     * @param openId     公众号openId
     * @param user       使用雷达员工信息{@link WeUser}
     */
    void createRecord(RadarShortUrlAppendInfo appendInfo, WeCustomer customer, String openId, WeUser user);
}
