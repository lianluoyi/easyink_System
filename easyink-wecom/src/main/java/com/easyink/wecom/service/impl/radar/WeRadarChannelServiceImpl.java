package com.easyink.wecom.service.impl.radar;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.RadarShortUrlAppendInfo;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.dto.radar.DeleteRadarChannelDTO;
import com.easyink.wecom.domain.dto.radar.RadarChannelDTO;
import com.easyink.wecom.domain.dto.radar.SearchRadarChannelDTO;
import com.easyink.wecom.domain.entity.radar.WeRadarChannel;
import com.easyink.wecom.domain.vo.radar.WeRadarChannelVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.radar.WeRadarChannelMapper;
import com.easyink.wecom.mapper.radar.WeRadarMapper;
import com.easyink.wecom.service.radar.RadarUrlHandler;
import com.easyink.wecom.service.radar.WeRadarChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName： WeRadarChannelServiceImpl
 *
 * @author wx
 * @date 2022/7/19 15:44
 */
@Slf4j
@Service
public class WeRadarChannelServiceImpl extends ServiceImpl<WeRadarChannelMapper, WeRadarChannel> implements WeRadarChannelService {


    private final RadarUrlHandler radarUrlHandler;
    private final WeRadarMapper weRadarMapper;

    @Autowired
    @Lazy
    public WeRadarChannelServiceImpl(RadarUrlHandler radarUrlHandler, WeRadarMapper weRadarMapper) {
        this.radarUrlHandler = radarUrlHandler;
        this.weRadarMapper = weRadarMapper;
    }

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
    @Override
    public String createShortUrl(String corpId, Long radarId, String userName, Integer channelType, String detail) {
        //长链
        String url = weRadarMapper.getRadarUrl(radarId);
        final RadarShortUrlAppendInfo shortUrlAppendInfo = radarUrlHandler.buildAppendInfo(radarId, userName, channelType, detail);
        return radarUrlHandler.createRadarUrl(corpId, url, userName, shortUrlAppendInfo);
    }

    /**
     * 新增雷达渠道
     *
     * @param radarChannelDTO
     */
    @Override
    public void saveRadarChannel(RadarChannelDTO radarChannelDTO) {
        final WeRadarChannel selectRadarChannel = this.baseMapper.selectOne(new LambdaQueryWrapper<WeRadarChannel>()
                .eq(WeRadarChannel::getRadarId, radarChannelDTO.getRadarId())
                .eq(WeRadarChannel::getName, radarChannelDTO.getName()));
        if (ObjectUtils.isNotEmpty(selectRadarChannel)) {
            throw new CustomException(RadarConstants.PromptCus.RADAR_CHANNEL_REPEAT);
        }
        WeRadarChannel radarChannel = radarChannelDTO.buildWeRadarChannel();
        radarChannel.setCreateTime(DateUtils.getTime());
        final boolean superAdmin = LoginTokenService.getLoginUser().isSuperAdmin();
        if (superAdmin) {
            radarChannel.setCreateBy(LoginTokenService.getUsername());
        } else {
            radarChannel.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }

        //长链
        //设置短链
        radarChannel.setShortUrl(createShortUrl(LoginTokenService.getLoginUser().getCorpId(), radarChannel.getRadarId(), radarChannel.getCreateBy(), RadarChannelEnum.CUSTOMIZE.getTYPE(), radarChannel.getName()));
        this.baseMapper.insert(radarChannel);
    }

    /**
     * 查询雷达渠道列表
     *
     * @param radarChannelDTO
     * @return
     */
    @Override
    public List<WeRadarChannelVO> getRadarChannelList(SearchRadarChannelDTO radarChannelDTO) {
        radarChannelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return this.baseMapper.list(radarChannelDTO);
    }

    /**
     * 批量删除雷达渠道
     *
     * @param deleteDTO
     * @return
     */
    @Override
    public void batchRemoveRadarChannel(DeleteRadarChannelDTO deleteDTO) {
        this.removeByIds(deleteDTO.getIdList());
    }

    /**
     * 获取渠道详情
     *
     * @param corpId
     * @param id
     * @return
     */
    @Override
    public WeRadarChannelVO getRadarChannel(String corpId, Long id) {
        final WeRadarChannel radarChannel = this.baseMapper.selectById(id);
        WeRadarChannelVO weRadarChannelVO = new WeRadarChannelVO();
        BeanUtils.copyProperties(radarChannel, weRadarChannelVO);
        return weRadarChannelVO;
    }

    /**
     * 修改渠道
     *
     * @param radarChannelDTO
     */
    @Override
    public void updateRadarChannel(RadarChannelDTO radarChannelDTO) {
        final WeRadarChannel radarChannel = radarChannelDTO.buildWeRadarChannel();
        this.baseMapper.updateById(radarChannel);
    }
}
