package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeOperationsCenterSopScopeEntity;
import com.easyink.wecom.mapper.WeOperationsCenterSopScopeMapper;
import com.easyink.wecom.service.WeOperationsCenterSopScopeService;
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
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeOperationsCenterSopScopeEntity> addList = new ArrayList<>();
        List<WeOperationsCenterSopScopeEntity> departmentRelInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIdList)){
            //插入更新员工
            addList = buildSopScopeDate(corpId, sopId, userIdList, WeConstans.SOP_USE_EMPLOYEE);
        }
        if (CollectionUtils.isNotEmpty(departmentIdList)) {
            //插入更新部门
            departmentRelInfoList = buildSopScopeDate(corpId, sopId, departmentIdList, WeConstans.SOP_USE_DEPARTMENT);
        }
        if(CollectionUtils.isNotEmpty(departmentRelInfoList)){
            addList.addAll(departmentRelInfoList);
        }
        if (CollectionUtils.isNotEmpty(addList)) {
            // 分批批量插入或更新
            BatchInsertUtil.doInsert(addList, list -> baseMapper.batchSaveOrUpdate(list));
        }
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
    public void batchSaveOrUpdate(List<WeOperationsCenterSopScopeEntity> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new CustomException(WeExceptionTip.WE_EXCEPTION_TIP_41035);
        }
        baseMapper.batchSaveOrUpdate(list);
    }
}