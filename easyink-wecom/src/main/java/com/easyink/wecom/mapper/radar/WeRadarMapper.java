package com.easyink.wecom.mapper.radar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.radar.SearchRadarDTO;
import com.easyink.wecom.domain.entity.radar.WeRadar;
import com.easyink.wecom.domain.entity.radar.WeRadarTag;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName： WeRadarMapper
 *
 * @author wx
 * @date 2022/7/18 15:36
 */
@Mapper
public interface WeRadarMapper extends BaseMapper<WeRadar> {

    /**
     * 查询雷达列表
     *
     * @param radarDTO
     * @return
     */
    List<WeRadarVO> list(@Param("radar") SearchRadarDTO radarDTO);


    /**
     * 获取详情
     *
     * @param corpId
     * @param id     雷达id
     * @return
     */
    WeRadarVO getOne(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 保存雷达客户标签
     *
     * @param weRadarTagList
     */
    void saveRadarTags(@Param("list") List<WeRadarTag> weRadarTagList);

    /**
     * 批量删除雷达客户标签
     *
     * @param ids 雷达id序列
     */
    void batchDeleteRadarTags(@Param("ids") List<Long> ids);

    /**
     * 删除雷达客户标签
     *
     * @param radarId
     */
    void deleteRadarTags(@Param("radarId") Long radarId);

    /**
     * 获得长链
     *
     * @param radarId
     * @return
     */
    String getRadarUrl(@Param("id") Long radarId);
    /**
     * 根据雷达id获取需要打上的标签
     *
     * @param id 雷达id
     * @return 需要打上的标签列表
     */
    List<String> getTagListByRadarId(@Param("id") Long id);
}
