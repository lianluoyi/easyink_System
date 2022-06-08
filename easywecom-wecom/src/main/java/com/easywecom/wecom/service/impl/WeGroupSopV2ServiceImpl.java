package com.easywecom.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WeOperationsCenterSop;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.*;
import com.easywecom.wecom.domain.dto.customersop.AddWeCustomerSopDTO;
import com.easywecom.wecom.domain.dto.customersop.Column;
import com.easywecom.wecom.domain.dto.groupsop.*;
import com.easywecom.wecom.domain.vo.customer.WeCustomerVO;
import com.easywecom.wecom.service.*;
import joptsimple.internal.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 类名：WeGroupSopV2ServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-30 14:57
 */
@Service
public class WeGroupSopV2ServiceImpl implements WeGroupSopV2Service {


    private final WeOperationsCenterSopService sopService;
    private final WeOperationsCenterSopRulesService sopRulesService;
    private final WeOperationsCenterSopScopeService sopScopeService;
    private final WeOperationsCenterGroupSopFilterService sopFilterService;
    private final WeOperationsCenterGroupSopFilterCycleService sopFilterCycleService;
    private final WeOperationsCenterCustomerSopFilterService customerSopFilterService;
    private final WeCustomerExtendPropertyRelService propertyRelService;
    private final WeCustomerService weCustomerService;

    @Autowired
    public WeGroupSopV2ServiceImpl(WeOperationsCenterSopService sopService, WeOperationsCenterSopRulesService sopRulesService, WeOperationsCenterSopScopeService sopScopeService, WeOperationsCenterGroupSopFilterService sopFilterService, WeOperationsCenterGroupSopFilterCycleService sopFilterCycleService, WeOperationsCenterCustomerSopFilterService customerSopFilterService, WeCustomerExtendPropertyRelService propertyRelService, WeCustomerService weCustomerService) {
        this.sopService = sopService;
        this.sopRulesService = sopRulesService;
        this.sopScopeService = sopScopeService;
        this.sopFilterService = sopFilterService;
        this.sopFilterCycleService = sopFilterCycleService;
        this.customerSopFilterService = customerSopFilterService;
        this.propertyRelService = propertyRelService;
        this.weCustomerService = weCustomerService;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSop(AddWeGroupSopDTO addWeGroupSopDTO) {
        if (addWeGroupSopDTO == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        boolean isCustomer = (WeOperationsCenterSop.SopTypeEnum.NEW_CUSTOMER.getSopType().equals(addWeGroupSopDTO.getSopType()) || WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(addWeGroupSopDTO.getSopType()) || WeOperationsCenterSop.SopTypeEnum.BIRTH_DAT.getSopType().equals(addWeGroupSopDTO.getSopType()));
        if (isCustomer) {
            checkCustomerParam(addWeGroupSopDTO.getSopType(), addWeGroupSopDTO.getUserIdList());
        } else if (WeOperationsCenterSop.SopTypeEnum.GROUP_CALENDAR.getSopType().equals(addWeGroupSopDTO.getSopType())) {
            checkGroupCalendar(addWeGroupSopDTO.getChatIdList(), addWeGroupSopDTO.getName(), addWeGroupSopDTO.getRuleList());
        } else {
            //校验群sop
            checkParam(addWeGroupSopDTO.getName(), addWeGroupSopDTO.getSopType(), addWeGroupSopDTO.getFilterType(), addWeGroupSopDTO.getSopFilter(), addWeGroupSopDTO.getChatIdList());
        }
        //保存基础信息
        addWeGroupSopDTO.setCreateTime(new Date());
        WeOperationsCenterSopEntity sopEntity = new WeOperationsCenterSopEntity();
        BeanUtils.copyProperties(addWeGroupSopDTO, sopEntity);
        sopService.save(sopEntity);
        if (isCustomer) {
            //保存客户筛选条件
            saveCustomerSop(addWeGroupSopDTO, sopEntity.getId());
        } else {
            //保存群或群筛选条件
            saveSopScopeOrSopFilter(addWeGroupSopDTO, sopEntity.getId());
        }
        //保存规则
        if (CollectionUtils.isNotEmpty(addWeGroupSopDTO.getRuleList())) {
            sopRulesService.batchSaveRuleAndMaterialList(sopEntity.getId(), sopEntity.getCorpId(), addWeGroupSopDTO.getRuleList());
        }
    }


    /**
     * 校验群日历
     *  @param chatIdList 群
     * @param name       日历名称
     * @param ruleList
     */
    private void checkGroupCalendar(List<String> chatIdList, String name, List<AddWeOperationsCenterSopRuleDTO> ruleList) {
        if (CollectionUtils.isEmpty(chatIdList) || StringUtils.isBlank(name) || CollectionUtils.isEmpty(ruleList)) {
            throw new CustomException(ResultTip.TIP_GROUP_CALENDAR_ADD_PARAMETER_ERROR);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSop(DelWeGroupSopDTO delWeGroupSopDTO) {
        if (StringUtils.isBlank(delWeGroupSopDTO.getCorpId()) || CollectionUtils.isEmpty(delWeGroupSopDTO.getSopIdList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        sopService.delSopByCorpIdAndSopIdList(delWeGroupSopDTO.getCorpId(), delWeGroupSopDTO.getSopIdList(),delWeGroupSopDTO.getSopType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateWeSopDTO updateWeSopDTO) {
        //校验请求参数
        if (updateWeSopDTO == null || updateWeSopDTO.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        boolean isCustomer = (WeOperationsCenterSop.SopTypeEnum.NEW_CUSTOMER.getSopType().equals(updateWeSopDTO.getSopType()) || WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(updateWeSopDTO.getSopType()) || WeOperationsCenterSop.SopTypeEnum.BIRTH_DAT.getSopType().equals(updateWeSopDTO.getSopType()));
        if (isCustomer) {
            checkCustomerParam(updateWeSopDTO.getSopType(), updateWeSopDTO.getUserIdList());
        } else if (WeOperationsCenterSop.SopTypeEnum.GROUP_CALENDAR.getSopType().equals(updateWeSopDTO.getSopType())) {
            checkGroupCalendar(updateWeSopDTO.getChatIdList(), updateWeSopDTO.getName(), updateWeSopDTO.getRuleList());
        } else {
            checkParam(updateWeSopDTO.getName(), updateWeSopDTO.getSopType(), updateWeSopDTO.getFilterType(), updateWeSopDTO.getSopFilter(), updateWeSopDTO.getChatIdList());
        }
        String corpId = updateWeSopDTO.getCorpId();
        Long sopId = updateWeSopDTO.getId();
        Integer filterType = isCustomer ? 0 : Optional.ofNullable(updateWeSopDTO.getFilterType()).orElse(0);
        //更新SOP任务
        sopService.updateSop(corpId, sopId, updateWeSopDTO.getName(), filterType);
        //更新条件或作用范围
        if (isCustomer){
            //客户sop
            updateCustomerFilter(updateWeSopDTO.getCreateTime(),corpId, sopId,updateWeSopDTO.getSopType(),updateWeSopDTO.getUserIdList(),updateWeSopDTO.getSopCustomerFilter());
        }else {
            updateSopFilter(corpId, sopId, updateWeSopDTO.getSopType(), filterType, updateWeSopDTO.getSopFilter(), updateWeSopDTO.getChatIdList());
        }
        //更新规则
        sopRulesService.updateSopRules(corpId, sopId, updateWeSopDTO.getRuleList(), updateWeSopDTO.getDelRuleList());
    }

    private void updateCustomerFilter(Date createTime,String corpId, Long sopId, Integer sopType, List<String> userIdList, AddWeCustomerSopDTO sopCustomerFilter) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //指定员工
        if (!WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(sopType)) {
            sopScopeService.updateSopScope(corpId, sopId, userIdList);
        }else{
            WeOperationsCenterSopScopeEntity sopScopeEntity= new WeOperationsCenterSopScopeEntity();
            sopScopeEntity.setCorpId(corpId);
            sopScopeEntity.setCreateTime(createTime);
            sopScopeEntity.setSopId(sopId);
            List<WeOperationsCenterSopScopeEntity> sopScopeEntities = new ArrayList<>();
            sopScopeService.remove(new LambdaQueryWrapper<WeOperationsCenterSopScopeEntity>()
                    .eq(WeOperationsCenterSopScopeEntity::getCorpId,corpId)
                    .eq(WeOperationsCenterSopScopeEntity::getSopId,sopId));
            saveCustomerSopTarget(corpId,sopCustomerFilter,sopScopeEntities,sopScopeEntity);
        }
        //筛选条件
        customerSopFilterService.remove(new LambdaQueryWrapper<WeOperationsCenterCustomerSopFilterEntity>()
                .eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId,corpId)
                .eq(WeOperationsCenterCustomerSopFilterEntity::getSopId,sopId));
        sopCustomerFilter.setCorpId(corpId);
        saveCustomerFilter(userIdList,sopCustomerFilter,sopId);
    }

    /**
     * 更新筛选条件数据
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param sopType    sop类型
     * @param filterType 筛选类型
     * @param sopFilter  筛选数据
     * @param chatIdList 作用范围
     */
    private void updateSopFilter(String corpId, Long sopId, Integer sopType, Integer filterType, AddGroupSopFilterDTO sopFilter, List<String> chatIdList) {
        if (StringUtils.isBlank(corpId) || sopId == null || filterType == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //筛选条件
        WeOperationsCenterGroupSopFilterEntity groupSopFilterEntity = getGroupSopFilterEntity(corpId, sopId, sopFilter);
        sopFilterService.updateGroupSopFilter(groupSopFilterEntity, sopType, sopFilter.getCycleStart(), sopFilter.getCycleEnd());
        //指定群聊|指定员工|群日历
        if (WeOperationsCenterSop.FilterTypeEnum.SPECIFY.getFilterType().equals(filterType) || WeOperationsCenterSop.SopTypeEnum.GROUP_CALENDAR.getSopType().equals(sopType)) {
            sopScopeService.updateSopScope(corpId, sopId, chatIdList);
        }

    }

    /**
     * 保存群或群筛选条件
     *
     * @param addWeGroupSopDTO addWeGroupSopDTO
     * @param sopId            sopId
     */
    private void saveSopScopeOrSopFilter(AddWeGroupSopDTO addWeGroupSopDTO, Long sopId) {
        String corpId = addWeGroupSopDTO.getCorpId();
        //指定群聊|群日历
        if (WeOperationsCenterSop.FilterTypeEnum.SPECIFY.getFilterType().equals(addWeGroupSopDTO.getFilterType()) || WeOperationsCenterSop.SopTypeEnum.GROUP_CALENDAR.getSopType().equals(addWeGroupSopDTO.getSopType())) {
            //保存指定群聊信息
            List<WeOperationsCenterSopScopeEntity> sopScopeEntityList = new ArrayList<>();
            WeOperationsCenterSopScopeEntity sopScopeEntity = new WeOperationsCenterSopScopeEntity();
            sopScopeEntity.setCorpId(corpId);
            sopScopeEntity.setCreateTime(addWeGroupSopDTO.getCreateTime());
            sopScopeEntity.setSopId(sopId);
            addWeGroupSopDTO.getChatIdList().forEach(chatId -> {
                WeOperationsCenterSopScopeEntity sopScope = new WeOperationsCenterSopScopeEntity();
                BeanUtils.copyProperties(sopScopeEntity, sopScope);
                sopScope.setTargetId(chatId);
                sopScopeEntityList.add(sopScope);
            });
            sopScopeService.batchSave(sopScopeEntityList);
        } else {
            //筛选群聊
            WeOperationsCenterGroupSopFilterEntity sopFilter = getGroupSopFilterEntity(corpId, sopId, addWeGroupSopDTO.getSopFilter());
            sopFilterService.save(sopFilter);
        }
        //当为循环SOP时，需要保存起止时间
        if (WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(addWeGroupSopDTO.getSopType())) {
            saveSopFilterCycle(corpId, sopId, addWeGroupSopDTO.getSopFilter().getCycleStart(), addWeGroupSopDTO.getSopFilter().getCycleEnd());
        }
    }

    /**
     * 群筛选条件数据为空的增加默认值
     */
    private WeOperationsCenterGroupSopFilterEntity getGroupSopFilterEntity(String corpId, Long sopId, WeOperationsCenterGroupSopFilterEntity sopFilter) {
        //为空说明没有过滤条件
        if (sopFilter == null) {
            return new WeOperationsCenterGroupSopFilterEntity();
        }
        sopFilter.setCorpId(corpId);
        sopFilter.setSopId(sopId);
        if (StringUtils.isBlank(sopFilter.getCreateTime())) {
            sopFilter.setCreateTime(WeConstans.DEFAULT_SOP_START_TIME);
        }
        if (StringUtils.isBlank(sopFilter.getEndTime())) {
            sopFilter.setEndTime(WeConstans.DEFAULT_SOP_END_TIME);
        }
        if (StringUtils.isBlank(sopFilter.getOwner())) {
            sopFilter.setOwner(Strings.EMPTY);
        }
        if (StringUtils.isBlank(sopFilter.getTagId())) {
            sopFilter.setTagId(Strings.EMPTY);
        }
        return sopFilter;
    }

    /**
     * 保存循环SOP的起止时间
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param cycleStart 循环起始时间
     * @param cycleEnd   循环结束时间
     */
    private void saveSopFilterCycle(String corpId, Long sopId, String cycleStart, String cycleEnd) {
        if (StringUtils.isBlank(corpId) || sopId == null || StringUtils.isBlank(cycleStart) || StringUtils.isBlank(cycleEnd)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeOperationsCenterGroupSopFilterCycleEntity cycleEntity = new WeOperationsCenterGroupSopFilterCycleEntity();
        cycleEntity.setCorpId(corpId);
        cycleEntity.setSopId(sopId);
        cycleEntity.setCycleStart(cycleStart);
        cycleEntity.setCycleEnd(cycleEnd);
        sopFilterCycleService.save(cycleEntity);
    }


    /**
     * 校验请求参数
     */
    private void checkParam(String name, Integer sopType, Integer filterType, AddGroupSopFilterDTO sopFilter, List<String> chatIdList) {
        if (StringUtils.isBlank(name)
                || sopType == null
                || filterType == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //请求类型为0：定时sop，1：循环sop
        if (!WeOperationsCenterSop.SopTypeEnum.TIME_TASK.getSopType().equals(sopType)
                && !WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(sopType)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //循环SOP需要填写循环的起止时间
        if (WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(sopType)) {
            if (StringUtils.isBlank(sopFilter.getCycleStart())
                    || StringUtils.isBlank(sopFilter.getCycleEnd())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            //校验时间格式
            if (!DateUtils.isMatchFormat(sopFilter.getCycleStart(), DateUtils.YYYY_MM_DD_HH_MM_SS)
                    || !DateUtils.isMatchFormat(sopFilter.getCycleEnd(), DateUtils.YYYY_MM_DD_HH_MM_SS)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            Date cycleStart = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, sopFilter.getCycleStart());
            Date cycleEnd = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, sopFilter.getCycleEnd());
            if (cycleStart.after(cycleEnd)) {
                throw new CustomException(ResultTip.TIP_START_AFTER_END_TIME);
            }
            if (cycleEnd.getTime() < System.currentTimeMillis()) {
                throw new CustomException(ResultTip.TIP_EXPIRETIME_LESS_CURR);
            }
        }

        //校验群聊参数
        if (WeOperationsCenterSop.FilterTypeEnum.SPECIFY.getFilterType().equals(filterType)) {
            if (CollectionUtils.isEmpty(chatIdList)) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        } else {
            //当为筛选群聊时，所有条件不能同时为空
            if (sopFilter == null || StringUtils.isBlank(sopFilter.getOwner())
                    && StringUtils.isBlank(sopFilter.getTagId())
                    && StringUtils.isBlank(sopFilter.getEndTime())
                    && StringUtils.isBlank(sopFilter.getCreateTime())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

    /**
     * 校验客户sop参数
     *
     * @param sopType 客户sop参数
     * @param userIdList 员工id
     */
    private void checkCustomerParam(Integer sopType,List<String> userIdList) {
        //客户sop除了活动sop 员工为必填
        if (!WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(sopType) && CollectionUtils.isEmpty(userIdList)) {
            throw new CustomException(ResultTip.TIP_ADD_CUSTOMER_SOP_ERROR);
        }
    }

    /**
     * 保存客户sop过滤值或作用范围
     *
     * @param addWeGroupSopDTO 参数
     * @param sopId       sopId
     */
    private void saveCustomerSop(AddWeGroupSopDTO addWeGroupSopDTO, Long sopId) {
        WeOperationsCenterSopScopeEntity sopScopeEntity = new WeOperationsCenterSopScopeEntity();
        sopScopeEntity.setCorpId(addWeGroupSopDTO.getCorpId());
        sopScopeEntity.setCreateTime(addWeGroupSopDTO.getCreateTime());
        sopScopeEntity.setSopId(sopId);
        List<WeOperationsCenterSopScopeEntity> weOperationsCenterSopScopeEntities = new ArrayList<>();
        AddWeCustomerSopDTO sopCustomerFilter = addWeGroupSopDTO.getSopCustomerFilter();
        //除了活动sop都要保存员工id
        if (!WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(addWeGroupSopDTO.getSopType())) {
            //保存sop作用员工
            addWeGroupSopDTO.getUserIdList().forEach(userId -> {
                WeOperationsCenterSopScopeEntity sopScope = new WeOperationsCenterSopScopeEntity();
                BeanUtils.copyProperties(sopScopeEntity, sopScope);
                sopScope.setTargetId(userId);
                weOperationsCenterSopScopeEntities.add(sopScope);
            });
            sopScopeService.saveBatch(weOperationsCenterSopScopeEntities);
        } else {
            saveCustomerSopTarget(addWeGroupSopDTO.getCorpId(),sopCustomerFilter,weOperationsCenterSopScopeEntities,sopScopeEntity);
        }
        //保存过滤条件
        sopCustomerFilter.setCorpId(addWeGroupSopDTO.getCorpId());
        saveCustomerFilter(addWeGroupSopDTO.getUserIdList(),sopCustomerFilter,sopId);

    }

    /**
     * 保存过滤条件
     * @param userIdList 员工id
     * @param sopCustomerFilter 过滤条件
     */
    private void saveCustomerFilter(List<String> userIdList, AddWeCustomerSopDTO sopCustomerFilter,Long sopId){
        WeOperationsCenterCustomerSopFilterEntity customerFilter = new WeOperationsCenterCustomerSopFilterEntity();
        String columnInfo = StringUtils.EMPTY;
        if (sopCustomerFilter != null) {
            List<Column> columnList = sopCustomerFilter.getColumnList();
            BeanUtils.copyProperties(sopCustomerFilter, customerFilter);
            //未选择性别则保存为 -1
            customerFilter.setGender(sopCustomerFilter.getGender() == null ? -1 : sopCustomerFilter.getGender());
            columnInfo = JSON.toJSONString(Optional.ofNullable(columnList).orElse(new ArrayList<>()));
        }
        customerFilter.setSopId(sopId);
        if (StringUtils.isBlank(customerFilter.getUsers())) {
            customerFilter.setUsers(CollectionUtils.isEmpty(userIdList) ? Strings.EMPTY : String.join(StrUtil.COMMA, userIdList));
        }
        customerFilter.setCloumnInfo(columnInfo);
        setDefaultCustomerValue(customerFilter);
        customerSopFilterService.save(customerFilter);
    }

    /**
     * 活动sop保存客户id
     * @param corpId 企业id
     * @param sopCustomerFilter 过滤条件
     * @param weOperationsCenterSopScopeEntities 列表
     * @param sopScopeEntity 作用范围
     */
    private void saveCustomerSopTarget(String corpId, AddWeCustomerSopDTO sopCustomerFilter, List<WeOperationsCenterSopScopeEntity> weOperationsCenterSopScopeEntities, WeOperationsCenterSopScopeEntity sopScopeEntity){
        //保存活动sop的客户id
        List<String> customerIdList;
        sopCustomerFilter.setCorpId(corpId);
        List<Column> columnList = sopCustomerFilter.getColumnList();
        if (CollectionUtils.isNotEmpty(columnList)) {
            List<String> customerIds = propertyRelService.listOfPropertyIdAndValue(columnList);
            //最终过滤的客户
            customerIdList = filterCustomer(sopCustomerFilter, customerIds);
        } else {
            customerIdList = filterCustomer(sopCustomerFilter, new ArrayList<>());
        }
        customerIdList.forEach(userId -> {
            WeOperationsCenterSopScopeEntity sopScope = new WeOperationsCenterSopScopeEntity();
            BeanUtils.copyProperties(sopScopeEntity, sopScope);
            sopScope.setTargetId(userId);
            weOperationsCenterSopScopeEntities.add(sopScope);
        });
        //批量保存作用范围
        sopScopeService.batchSave(weOperationsCenterSopScopeEntities);
    }

    private List<String> filterCustomer(AddWeCustomerSopDTO weCustomerSopDTO, List<String> customerIds) {
        WeCustomer weCustomer = new WeCustomer();
        BeanUtils.copyProperties(weCustomerSopDTO, weCustomer);
        weCustomer.setUserIds(weCustomerSopDTO.getUsers());
        List<WeCustomerVO> weCustomers = weCustomerService.selectWeCustomerListV2(weCustomer);
        List<String> customerList = CollectionUtils.isNotEmpty(customerIds)
                ? weCustomers.stream().filter(customer -> customerIds.contains(customer.getExternalUserid())).map(WeCustomerVO::getExternalUserid).collect(Collectors.toList())
                : weCustomers.stream().map(WeCustomerVO::getExternalUserid).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(customerList) ? customerList : new ArrayList<>();
    }

    /**
     * 设置默认值
     * @param customerFilter 客户sop过滤值
     */
    private void setDefaultCustomerValue(WeOperationsCenterCustomerSopFilterEntity customerFilter) {
        if (StringUtils.isBlank(customerFilter.getUsers())) {
            customerFilter.setUsers(Strings.EMPTY);
        }
        if (StringUtils.isBlank(customerFilter.getTagId())) {
            customerFilter.setTagId(Strings.EMPTY);
        }
        if (StringUtils.isBlank(customerFilter.getCloumnInfo())) {
            customerFilter.setCloumnInfo(Strings.EMPTY);
        }
        if (StringUtils.isBlank(customerFilter.getFilterTagId())) {
            customerFilter.setFilterTagId(Strings.EMPTY);
        }
    }
}
