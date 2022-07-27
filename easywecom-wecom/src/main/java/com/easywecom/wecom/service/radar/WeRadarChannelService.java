package com.easywecom.wecom.service.radar;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.dto.radar.DeleteRadarChannelDTO;
import com.easywecom.wecom.domain.dto.radar.RadarChannelDTO;
import com.easywecom.wecom.domain.dto.radar.SearchRadarChannelDTO;
import com.easywecom.wecom.domain.entity.radar.WeRadarChannel;
import com.easywecom.wecom.domain.vo.radar.WeRadarChannelVO;

import java.util.List;

/**
 * ClassName： WeRadarChannelService
 *
 * @author wx
 * @date 2022/7/19 14:55
 */
public interface WeRadarChannelService extends IService<WeRadarChannel> {

    /**
     * 新增雷达渠道
     *
     * @param radarChannelDTO
     */
    void saveRadarChannel(RadarChannelDTO radarChannelDTO);

    /**
     * 查询雷达渠道列表
     *
     * @param radarChannelDTO
     * @return
     */
    List<WeRadarChannelVO> getRadarChannelList(SearchRadarChannelDTO radarChannelDTO);

    /**
     * 批量删除雷达渠道
     *
     * @param deleteDTO
     * @return
     */
    void batchRemoveRadarChannel(DeleteRadarChannelDTO deleteDTO);

    /**
     * 获取渠道详情
     *
     * @param corpId
     * @param id
     * @return
     */
    WeRadarChannelVO getRadarChannel(String corpId, Long id);

    /**
     * 修改渠道
     *
     * @param radarChannelDTO
     */
    void updateRadarChannel(RadarChannelDTO radarChannelDTO);

    /**
     * 生成短链
     *
     * @param corpId
     * @param radarId
     * @param userName
     * @param channelType
     * @param detail
     * @return
     */
    String createShortUrl(String corpId, Long radarId, String userName, Integer channelType, String detail);
}
