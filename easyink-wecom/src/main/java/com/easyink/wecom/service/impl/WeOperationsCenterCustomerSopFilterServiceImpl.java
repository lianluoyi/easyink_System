package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import com.easyink.wecom.mapper.WeOperationsCenterCustomerSopFilterMapper;
import com.easyink.wecom.service.WeOperationsCenterCustomerSopFilterService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * 类名： WeOperationsCenterCustomerSopFilterServiceImpl
 *
 * @author 佚名
 * @date 2021/11/30 14:19
 */
@Service
public class WeOperationsCenterCustomerSopFilterServiceImpl extends ServiceImpl<WeOperationsCenterCustomerSopFilterMapper, WeOperationsCenterCustomerSopFilterEntity> implements WeOperationsCenterCustomerSopFilterService {


    @Override
    public void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        //删除筛选条件
        LambdaQueryWrapper<WeOperationsCenterCustomerSopFilterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId, corpId);
        wrapper.in(WeOperationsCenterCustomerSopFilterEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }

    @Override
    public WeOperationsCenterCustomerSopFilterEntity getCustomerSopFilter(String corpId, Long sopId) {
        LambdaQueryWrapper<WeOperationsCenterCustomerSopFilterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId, corpId);
        wrapper.eq(WeOperationsCenterCustomerSopFilterEntity::getSopId, sopId).last(GenConstants.LIMIT_1);
        return Optional.ofNullable(baseMapper.selectOne(wrapper)).orElse(new WeOperationsCenterCustomerSopFilterEntity());
    }
}