package com.easywecom.wecom.mapper.radar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.dto.radar.SearchRadarChannelDTO;
import com.easywecom.wecom.domain.entity.radar.WeRadarChannel;
import com.easywecom.wecom.domain.vo.radar.WeRadarChannelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName： WeRadarChannelMapper
 *
 * @author wx
 * @date 2022/7/19 15:46
 */
@Mapper
public interface WeRadarChannelMapper extends BaseMapper<WeRadarChannel> {
    /**
     * 查询渠道列表
     *
     * @param radarChannelDTO
     * @param isSuperAdmin
     * @return
     */
    List<WeRadarChannelVO> list(@Param("channel") SearchRadarChannelDTO radarChannelDTO, @Param("isSuperAdmin") boolean isSuperAdmin);
}
