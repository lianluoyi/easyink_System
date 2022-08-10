package com.easyink.wecom.mapper.radar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.radar.*;
import com.easyink.wecom.domain.entity.radar.WeRadarClickRecord;
import com.easyink.wecom.domain.vo.radar.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName： WeRadarClickRecordMapper
 *
 * @author wx
 * @date 2022/7/19 19:57
 */
@Mapper
public interface WeRadarClickRecordMapper extends BaseMapper<WeRadarClickRecord> {

    /**
     * 获取雷达数据总览
     *
     * @param radarId
     * @param nowDate
     * @return
     */
    RadarRecordTotalVO getTotal(@Param("radarId") Long radarId, @Param("nowDate") String nowDate);

    /**
     * 查询雷达数据统计（折线图）
     *
     * @param radarAnalyseDTO
     * @return
     */
    List<RadarAnalyseCountVO> selectCountList(@Param("radarAnalyse") SearchRadarAnalyseDTO radarAnalyseDTO);

    /**
     * 查询雷达渠道点击数排序
     *
     * @param radarId
     * @return
     */
    List<RadarChannelSortVO> selectChannelSort(@Param("radarId") Long radarId);

    /**
     * 获得客户点击记录
     *
     * @param customerRecordDTO
     * @return
     */
    List<RadarCustomerRecordVO> getCustomerClickRecord(@Param("customerRecord") SearchCustomerRecordDTO customerRecordDTO);

    /**
     * 获取渠道点击记录
     *
     * @param channelRecordDTO
     * @return
     */
    List<RadarChannelRecordVO> getChannelClickRecord(@Param("channelRecord") SearchChannelRecordDTO channelRecordDTO);

    /**
     * 获取渠道点击记录详情
     *
     * @param channelRecordDetailDTO
     * @return
     */
    List<RadarCustomerRecordVO> getChannelClickRecordDetail(@Param("channelRecordDetail") SearchChannelRecordDetailDTO channelRecordDetailDTO);

    /**
     * 获取客户点击记录详情
     *
     * @param customerRecordDTO
     * @return
     */
    List<RadarCustomerClickRecordDetailVO> getCustomerClickRecordDetail(@Param("customerRecord") SearchCustomerRecordDetailDTO customerRecordDTO);
}
