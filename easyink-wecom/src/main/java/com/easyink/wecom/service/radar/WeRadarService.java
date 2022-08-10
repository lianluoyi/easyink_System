package com.easyink.wecom.service.radar;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.radar.DeleteRadarDTO;
import com.easyink.wecom.domain.dto.radar.RadarDTO;
import com.easyink.wecom.domain.dto.radar.SearchRadarDTO;
import com.easyink.wecom.domain.entity.radar.WeRadar;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;

import java.util.List;

/**
 * ClassName： WeRadarService
 *
 * @author wx
 * @date 2022/7/18 15:33
 */
public interface WeRadarService extends IService<WeRadar> {

    /**
     * 新增雷达
     *
     * @param radarDTO
     * @return
     */
    void saveRadar(RadarDTO radarDTO);

    /**
     * 查询雷达列表
     *
     * @param radarDTO
     * @return
     */
    List<WeRadarVO> getRadarList(SearchRadarDTO radarDTO);

    /**
     * 获取详情
     *
     * @param corpId
     * @param id
     * @return
     */
    WeRadarVO getRadar(String corpId, Long id);

    /**
     * 更新雷达
     *
     * @param radarDTO
     */
    void updateRadar(RadarDTO radarDTO);

    /**
     * 批量删除雷达
     *
     * @param corpId
     * @param deleteDTO
     */
    int batchRemoveRadar(String corpId, DeleteRadarDTO deleteDTO);

    /**
     * 根据雷达id获取需要打上的标签
     *
     * @param id 雷达id
     * @return 需要打上的标签列表
     */
    List<WeTag> getTagListByRadarId(Long id);

    /**
     * 发送客户时生成雷达短链
     *
     * @param radarId
     * @param channelType
     * @param userId
     * @param corpId
     * @param scenario
     * @return
     */
    AttachmentParam getRadarShortUrl(Long radarId, Integer channelType, String userId, String corpId, String scenario);
}
