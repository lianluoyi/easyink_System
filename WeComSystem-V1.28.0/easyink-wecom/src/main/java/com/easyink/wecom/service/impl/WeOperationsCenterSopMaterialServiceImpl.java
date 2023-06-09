package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeOperationsCenterSopMaterialEntity;
import com.easyink.wecom.mapper.WeOperationsCenterSopMaterialMapper;
import com.easyink.wecom.service.WeOperationsCenterSopMaterialService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class WeOperationsCenterSopMaterialServiceImpl extends ServiceImpl<WeOperationsCenterSopMaterialMapper, WeOperationsCenterSopMaterialEntity> implements WeOperationsCenterSopMaterialService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterSopMaterialEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopMaterialEntity::getCorpId, corpId)
                .in(WeOperationsCenterSopMaterialEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }
}