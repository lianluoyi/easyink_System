package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
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

    /**
     * 插入更新员工/部门
     * @param corpId        cropId
     * @param sopId         SOPId
     * @param targetList    userIdList/departmentIdList
     * @param type          员工/部门
     * @return
     */
    public List<WeOperationsCenterSopScopeEntity> buildSopScopeDate(String corpId, Long sopId, List<String> targetList, Integer type){
        List<WeOperationsCenterSopScopeEntity> addList = new ArrayList<>();
        if(CollectionUtils.isEmpty(targetList)){
            return addList;
        }
        Date date = new Date();
        WeOperationsCenterSopScopeEntity sopScopeEntity;
        for (String target : targetList) {
            sopScopeEntity = new WeOperationsCenterSopScopeEntity();
            sopScopeEntity.setCorpId(corpId);
            sopScopeEntity.setSopId(sopId);
            sopScopeEntity.setTargetId(target);
            sopScopeEntity.setType(type);
            sopScopeEntity.setCreateTime(date);
            addList.add(sopScopeEntity);
        }
        return addList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSopScope(String corpId, Long sopId, List<String> userIdList, List<String> departmentIdList) {
        boolean isHaveNotTargetList = CollectionUtils.isEmpty(userIdList) && CollectionUtils.isEmpty(departmentIdList);
        if (StringUtils.isBlank(corpId) || sopId == null || isHaveNotTargetList) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //先删除数据 再重新插入
        LambdaQueryWrapper<WeOperationsCenterSopScopeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopScopeEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopScopeEntity::getSopId, sopId);
        baseMapper.delete(wrapper);
        //插入更新员工
        List<WeOperationsCenterSopScopeEntity> addList = buildSopScopeDate(corpId, sopId, userIdList, WeConstans.SOP_USE_EMPLOYEE);
        //插入更新部门
        List<WeOperationsCenterSopScopeEntity> departmentRelInfoList = buildSopScopeDate(corpId, sopId, departmentIdList, WeConstans.SOP_USE_DEPARTMENT);
        if(CollectionUtils.isNotEmpty(departmentRelInfoList)){
            addList.addAll(departmentRelInfoList);
        }
        baseMapper.batchSave(addList);
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