package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeOperationsCenterSopScopeEntity;
import com.easywecom.wecom.mapper.WeOperationsCenterSopScopeMapper;
import com.easywecom.wecom.service.WeOperationsCenterSopScopeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Validated
public class WeOperationsCenterSopScopeServiceImpl extends ServiceImpl<WeOperationsCenterSopScopeMapper, WeOperationsCenterSopScopeEntity> implements WeOperationsCenterSopScopeService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterSopScopeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopScopeEntity::getCorpId, corpId)
                .in(WeOperationsCenterSopScopeEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSopScope(String corpId, Long sopId, List<String> targetList) {
        if (StringUtils.isBlank(corpId) || sopId == null || CollectionUtils.isEmpty(targetList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        Date date = new Date();
        WeOperationsCenterSopScopeEntity sopScopeEntity;
        List<WeOperationsCenterSopScopeEntity> addList = new ArrayList<>();
        for (String target : targetList) {
            sopScopeEntity = new WeOperationsCenterSopScopeEntity();
            sopScopeEntity.setCorpId(corpId);
            sopScopeEntity.setSopId(sopId);
            sopScopeEntity.setTargetId(target);
            sopScopeEntity.setCreateTime(date);
            addList.add(sopScopeEntity);
        }
        baseMapper.batchSave(addList);

        //删除不存在数据
        LambdaQueryWrapper<WeOperationsCenterSopScopeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopScopeEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopScopeEntity::getSopId, sopId)
                .notIn(WeOperationsCenterSopScopeEntity::getTargetId, targetList);
        baseMapper.delete(wrapper);
    }

    @Override
    public List<WeOperationsCenterSopScopeEntity> getScope(@NotNull Long sopId, @NotEmpty String corpId) {
        LambdaQueryWrapper<WeOperationsCenterSopScopeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopScopeEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopScopeEntity::getSopId, sopId);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<WeOperationsCenterSopScopeEntity> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.batchSave(list);
    }
}