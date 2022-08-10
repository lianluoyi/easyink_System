package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeOperationsCenterGroupSopFilterCycleEntity;
import com.easyink.wecom.mapper.WeOperationsCenterGroupSopFilterCycleMapper;
import com.easyink.wecom.service.WeOperationsCenterGroupSopFilterCycleService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 类名： WeOperationsCenterGroupSopFilterCycleService 群SOP筛选群聊-起止时间
 *
 * @author Society my sister Li
 * @date 2021-11-30 14:05:23
 */
@Service
public class WeOperationsCenterGroupSopFilterCycleServiceImpl extends ServiceImpl<WeOperationsCenterGroupSopFilterCycleMapper, WeOperationsCenterGroupSopFilterCycleEntity> implements WeOperationsCenterGroupSopFilterCycleService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterGroupSopFilterCycleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterGroupSopFilterCycleEntity::getCorpId, corpId)
                .in(WeOperationsCenterGroupSopFilterCycleEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }

    @Override
    public void saveOrUpdateSopFilterCycle(String corpId, Long sopId, String cycleStart, String cycleEnd) {
        if (StringUtils.isBlank(corpId) || sopId == null || StringUtils.isBlank(cycleStart) || StringUtils.isBlank(cycleEnd)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.saveOrUpdate(corpId, sopId, cycleStart, cycleEnd);
    }

    @Override
    public WeOperationsCenterGroupSopFilterCycleEntity getDataBySopId(String corpId, Long sopId) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterGroupSopFilterCycleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterGroupSopFilterCycleEntity::getCorpId, corpId)
                .eq(WeOperationsCenterGroupSopFilterCycleEntity::getSopId, sopId).last(GenConstants.LIMIT_1);

        return baseMapper.selectOne(wrapper);
    }
}