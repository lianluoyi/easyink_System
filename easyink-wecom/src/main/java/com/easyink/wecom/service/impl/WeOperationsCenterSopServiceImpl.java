package com.easyink.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeOperationsCenterSop;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.customersop.EditUserDTO;
import com.easyink.wecom.domain.dto.groupsop.SopBatchSwitchDTO;
import com.easyink.wecom.domain.vo.WeOperationsCenterSopVo;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.domain.vo.sop.*;
import com.easyink.wecom.mapper.WeOperationsCenterSopMapper;
import com.easyink.wecom.service.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名： WeOperationsCenterSopServiceImpl
 *
 * @author 佚名
 * @date 2021/11/30 14:20
 */
@Service
@Validated
public class WeOperationsCenterSopServiceImpl extends ServiceImpl<WeOperationsCenterSopMapper, WeOperationsCenterSopEntity> implements WeOperationsCenterSopService {

    private final WeOperationsCenterGroupSopFilterService sopFilterService;
    private final WeOperationsCenterSopScopeService sopScopeService;
    private final WeOperationsCenterSopRulesService sopRulesService;
    private final WeOperationsCenterCustomerSopFilterService customerSopFilterService;
    private final WeUserService weUserService;
    private final WeCustomerService weCustomerService;
    private final WeGroupService weGroupService;
    private final WeTagService weTagService;
    private final WeDepartmentService weDepartmentService;

    @Autowired
    public WeOperationsCenterSopServiceImpl(WeOperationsCenterGroupSopFilterService sopFilterService, WeOperationsCenterSopScopeService sopScopeService, WeOperationsCenterSopRulesService sopRulesService, WeOperationsCenterCustomerSopFilterService customerSopFilterService, WeUserService weUserService, WeCustomerService weCustomerService, WeGroupService weGroupService, WeTagService weTagService, WeDepartmentService weDepartmentService) {
        this.sopFilterService = sopFilterService;
        this.sopScopeService = sopScopeService;
        this.sopRulesService = sopRulesService;
        this.customerSopFilterService = customerSopFilterService;
        this.weUserService = weUserService;
        this.weCustomerService = weCustomerService;
        this.weGroupService = weGroupService;
        this.weTagService = weTagService;
        this.weDepartmentService = weDepartmentService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList, Integer sopType) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //sop基本信息
        baseMapper.delSopByCorpIdAndSopIdList(corpId, sopIdList);
        //sop过滤条件
        if (WeOperationsCenterSop.SopTypeEnum.isCustomerSop(sopType)) {
            //客户sop过滤条件
            customerSopFilterService.delByCorpIdAndSopIdList(corpId, sopIdList);
        } else {
            //客户群sop过滤条件
            sopFilterService.delByCorpIdAndSopIdList(corpId, sopIdList);
        }
        //sop作用范围
        sopScopeService.delSopByCorpIdAndSopIdList(corpId, sopIdList);
        //sop规则
        sopRulesService.delSopByCorpIdAndSopIdList(corpId, sopIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSwitch(SopBatchSwitchDTO switchDTO) {
        if (switchDTO == null || CollectionUtils.isEmpty(switchDTO.getSopIdList()) || switchDTO.getIsOpen() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.batchSwitch(switchDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSop(String corpId, Long sopId, String name, Integer filterType) {
        if (StringUtils.isBlank(corpId) || sopId == null || StringUtils.isBlank(name) || filterType == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.updateSop(corpId, sopId, name, filterType);
    }

    @Override
    public List<WeOperationsCenterSopEntity> findSwitchOpenSop() {
        LambdaQueryWrapper<WeOperationsCenterSopEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopEntity::getIsOpen, true);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SopDetailVO listOfDetail(Long sopId, String corpId) {
        //基础信息查询
        WeOperationsCenterSopVo sopVo = getSop(sopId, corpId);
        if (sopVo == null) {
            return new SopDetailVO();
        }
        //查询作用范围
        List<WeOperationsCenterSopScopeEntity> scopeList = sopScopeService.getScope(sopId, corpId);

        //组合返回值
        SopDetailVO sopDetailVO = new SopDetailVO(sopVo);
        List<String> targetList = scopeList.stream().map(WeOperationsCenterSopScopeEntity::getTargetId).collect(Collectors.toList());
        //加入部门
        List<String> departmentIdList = scopeList.stream().filter(a->a.getType().equals(WeConstans.SOP_USE_DEPARTMENT)).map(WeOperationsCenterSopScopeEntity::getTargetId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(departmentIdList)){
            List<WeDepartment> weDepartments = weDepartmentService.listByIds(departmentIdList);
            List<DepartmentVO> weDepartmentVO = weDepartmentService.getDeparmentDetailByIds(corpId, departmentIdList);
            if(CollectionUtils.isNotEmpty(weDepartments)){
                sopDetailVO.setDepartmentList(weDepartmentVO);
            }
        }
        //客户sop 作用范围
        if (WeOperationsCenterSop.SopTypeEnum.isCustomerSop(sopVo.getSopType())) {
            buildCustomerScope(corpId, sopId, sopVo.getSopType(), sopDetailVO, targetList);
        } else { //群sop
            buildGroupScope(corpId, sopId, sopVo.getSopType(), sopDetailVO, targetList, scopeList);
        }
        //组规则
        buildRule(corpId, sopId, sopDetailVO);
        setDefaultList(sopDetailVO);
        return sopDetailVO;
    }

    /**
     * 构造客户sop Scope
     */
    private void buildCustomerScope(String corpId, Long sopId, Integer sopType, SopDetailVO sopDetailVO, List<String> targetList) {
        if (WeOperationsCenterSop.SopTypeEnum.ACTIVITY.getSopType().equals(sopType)) {
            WeOperationsCenterCustomerSopFilterEntity sopFilter = customerSopFilterService.getCustomerSopFilter(corpId, sopId);
            List<WeCustomer> weCustomers = weCustomerService.listOfUseCustomer(corpId, sopFilter);
            //因为前端没有区分活动SOP需要使用customerSopVOList,所以将活动SOPde客户数据也放入userList
            buildCustomerVoAndUserList(weCustomers, sopDetailVO);
        } else {
            List<WeUserVO> weUsers = weUserService.listOfUser(corpId, targetList);
            List<SopUserVO> sopUserVOList = new ArrayList<>();
            setSopUserVO(weUsers, sopUserVOList);
            sopDetailVO.setUserList(sopUserVOList);
        }
        //查询客户过滤条件
        buildCustomerSopFilter(corpId, sopId, sopDetailVO);
    }

    /**
     * 构造客户群sop Scope
     */
    private void buildGroupScope(String corpId, Long sopId, Integer sopType, SopDetailVO sopDetailVO, List<String> targetList, List<WeOperationsCenterSopScopeEntity> scopeList) {
        //Map<chatId,addTime> 关联数据,查询群基本信息后，需要组装该字段
        Map<String, Date> dateMap = scopeList.stream().collect(Collectors.toMap(WeOperationsCenterSopScopeEntity::getTargetId, WeOperationsCenterSopScopeEntity::getCreateTime));
        //查询作用范围下的群的基本信息
        List<GroupSopVO> groupSopVOList = CollectionUtils.isNotEmpty(targetList) ? weGroupService.listOfChat(StrUtil.EMPTY, targetList) : new ArrayList<>();
        for (GroupSopVO groupSopVO : groupSopVOList) {
            groupSopVO.setAddTime(dateMap.get(groupSopVO.getChatId()));
        }
        //获取群筛选数据
        FindGroupSopFilterVO groupSopFilter = sopFilterService.getDataBySopId(corpId, sopId, sopType);
        sopDetailVO.setSopFilter(groupSopFilter);
        //当查无数据时,需要重新筛选一次数据
        if (CollectionUtils.isEmpty(groupSopVOList) && groupSopFilter != null) {
            List<WeGroup> groupList = weGroupService.listNoRelTag(corpId, groupSopFilter.getTagId(), groupSopFilter.getOwner(), groupSopFilter.getCreateTime(), groupSopFilter.getEndTime());
            buildGroupSopList(groupSopVOList, groupList);
        }
        sopDetailVO.setGroupSopList(groupSopVOList);
    }

    /**
     * 构造sop规则
     */
    private void buildRule(String corpId, Long sopId, SopDetailVO sopDetailVO) {
        //查询规则
        LambdaQueryWrapper<WeOperationsCenterSopRulesEntity> ruleWrapper = new LambdaQueryWrapper<>();
        ruleWrapper.eq(WeOperationsCenterSopRulesEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopRulesEntity::getSopId, sopId);
        List<WeOperationsCenterSopRulesEntity> ruleList = sopRulesService.list(ruleWrapper);
        List<SopRuleVO> sopRuleVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ruleList)) {
            ruleList.forEach(rule -> {
                SopRuleVO sopRuleVO = sopRulesService.getSopRule(corpId, sopId, rule.getId());
                sopRuleVOList.add(sopRuleVO);
            });
        }
        sopDetailVO.setRuleList(sopRuleVOList);
    }

    /**
     * 构建客户sop条件数据
     *
     * @param corpId      企业ID
     * @param sopId       sopId
     * @param sopDetailVO sopDetailVO
     */
    private void buildCustomerSopFilter(String corpId, Long sopId, SopDetailVO sopDetailVO) {
        if (StringUtils.isBlank(corpId) || sopId == null || sopDetailVO == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //查询客户过滤条件
        WeOperationsCenterCustomerSopFilterEntity customerSopFilter = customerSopFilterService.getCustomerSopFilter(corpId, sopId);
        GetCustomerSopFilterVO customerSopFilterVO = new GetCustomerSopFilterVO(customerSopFilter);
        if (StringUtils.isNotBlank(customerSopFilter.getCloumnInfo())) {
            JSONArray arr = JSON.parseArray(customerSopFilter.getCloumnInfo());
            customerSopFilterVO.setColumnList(arr);
            //所属员工信息
            List<SopUserVO> userInfoList = new ArrayList<>();
            List<WeUserVO> weUsers = weUserService.listOfUser(corpId, Arrays.asList(customerSopFilterVO.getUsers().split(StrUtil.COMMA)));
            setSopUserVO(weUsers, userInfoList);
            customerSopFilterVO.setUserInfoList(userInfoList);
            final List<String> departmentIdList = Arrays.asList(StrUtil.split(customerSopFilter.getDepartments(), StrUtil.COMMA));
            if (CollectionUtils.isNotEmpty(departmentIdList)) {
                List<DepartmentVO> weDepartmentVO = weDepartmentService.getDeparmentDetailByIds(corpId, departmentIdList);
                if (CollectionUtils.isNotEmpty(weDepartmentVO)) {
                    customerSopFilterVO.setDepartmentInfoList(weDepartmentVO);
                }
            }
        }
        //获取已打标签的标签详情
        customerSopFilterVO.setTagList(buildCustomerTagList(corpId, customerSopFilter.getTagId()));
        //获取过滤标签的标签详情
        customerSopFilterVO.setFilterTagList(buildCustomerTagList(corpId, customerSopFilter.getFilterTagId()));
        sopDetailVO.setSopCustomerFilter(customerSopFilterVO);
    }

    /**
     * 查询标签详情
     *
     * @param corpId 企业ID
     * @param tagIds 客户标签(多个逗号隔开)
     * @return List<BaseCustomerSopTagVO>
     */
    private List<BaseCustomerSopTagVO> buildCustomerTagList(String corpId, String tagIds) {
        List<BaseCustomerSopTagVO> tagList = new ArrayList<>();
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(tagIds)) {
            return tagList;
        }
        List<String> tagIdList = Arrays.asList(tagIds.split(","));
        LambdaQueryWrapper<WeTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeTag::getCorpId, corpId)
                .in(WeTag::getTagId, tagIdList);
        List<WeTag> list = weTagService.list(wrapper);
        for (WeTag weTag : list) {
            tagList.add(new BaseCustomerSopTagVO(weTag.getTagId(), weTag.getName()));
        }
        return tagList;
    }


    /**
     * 构建使用群聊数据
     *
     * @param groupSopVOList groupSopVOList
     * @param groupList      groupList
     */
    private void buildGroupSopList(List<GroupSopVO> groupSopVOList, List<WeGroup> groupList) {
        GroupSopVO groupSopVO;
        Date date = new Date();
        for (WeGroup weGroup : groupList) {
            groupSopVO = new GroupSopVO();
            groupSopVO.setGroupName(weGroup.getGroupName());
            groupSopVO.setOwner(weGroup.getOwner());
            groupSopVO.setUserName(weGroup.getGroupLeaderName());
            groupSopVO.setMainDepartmentName(weGroup.getMainDepartmentName());
            groupSopVO.setCreateTime(weGroup.getCreateTime());
            groupSopVO.setAddTime(date);
            groupSopVO.setChatId(weGroup.getChatId());
            groupSopVOList.add(groupSopVO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUser(EditUserDTO updateWeSopDTO) {
        boolean exitEditUserOrDepartment = CollectionUtils.isNotEmpty(updateWeSopDTO.getUserIdList()) || CollectionUtils.isNotEmpty(updateWeSopDTO.getDepartmentIdList());
        if (exitEditUserOrDepartment && updateWeSopDTO.getId() != null) {
            if (StringUtils.isBlank(updateWeSopDTO.getCorpId())) {
                throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
            }
            List<Long> sopIds = new ArrayList<>();
            sopIds.add(updateWeSopDTO.getId());
            sopScopeService.delSopByCorpIdAndSopIdList(updateWeSopDTO.getCorpId(), sopIds);
            sopScopeService.saveBatch(buildScope(updateWeSopDTO.getUserIdList(), updateWeSopDTO.getDepartmentIdList(), updateWeSopDTO.getId(), updateWeSopDTO.getCorpId()));
            customerSopFilterService.update(new LambdaUpdateWrapper<WeOperationsCenterCustomerSopFilterEntity>()
                    .eq(WeOperationsCenterCustomerSopFilterEntity::getSopId, updateWeSopDTO.getId())
                    .eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId, updateWeSopDTO.getCorpId())
                    .set(WeOperationsCenterCustomerSopFilterEntity::getUsers, CollectionUtils.isNotEmpty(updateWeSopDTO.getUserIdList()) ? String.join(StrUtil.COMMA, updateWeSopDTO.getUserIdList()) : StringUtils.EMPTY)
                    .set(WeOperationsCenterCustomerSopFilterEntity::getDepartments, CollectionUtils.isNotEmpty(updateWeSopDTO.getDepartmentIdList()) ? String.join(StrUtil.COMMA, updateWeSopDTO.getDepartmentIdList()) : StringUtils.EMPTY));
        }
    }

    /**
     * 构建作用范围
     *
     * @param userIdList 员工id
     * @param sopId      sopid
     * @param corpId     企业id
     * @return {@link List<WeOperationsCenterSopScopeEntity>}
     */
    private List<WeOperationsCenterSopScopeEntity> buildScope(List<String> userIdList, List<String> departmentIdList, Long sopId, String corpId) {
        List<WeOperationsCenterSopScopeEntity> scopeEntityList = new ArrayList<>();
        Optional.ofNullable(userIdList).orElseGet(Lists::newArrayList);
        for (String userId : userIdList) {
            WeOperationsCenterSopScopeEntity scopeEntity = new WeOperationsCenterSopScopeEntity();
            scopeEntity.setTargetId(userId);
            scopeEntity.setType(WeConstans.SOP_USE_EMPLOYEE);
            scopeEntity.setCorpId(corpId);
            scopeEntity.setSopId(sopId);
            scopeEntityList.add(scopeEntity);
        }
        Optional.ofNullable(departmentIdList).orElseGet(Lists::newArrayList);
        for (String departmentId : departmentIdList) {
            WeOperationsCenterSopScopeEntity scopeEntity = new WeOperationsCenterSopScopeEntity();
            scopeEntity.setTargetId(departmentId);
            scopeEntity.setType(WeConstans.SOP_USE_DEPARTMENT);
            scopeEntity.setCorpId(corpId);
            scopeEntity.setSopId(sopId);
            scopeEntityList.add(scopeEntity);
        }
        return scopeEntityList;
    }

    /**
     * 构造返回客户条件
     *
     * @param weCustomers 客户
     * @param sopDetailVO 客户vo
     */
    private void buildCustomerVoAndUserList(List<WeCustomer> weCustomers, SopDetailVO sopDetailVO) {
        List<CustomerSopVO> customerSopVOList = new ArrayList<>();
        SopUserVO sopUserVO;
        List<SopUserVO> userList = new ArrayList<>();
        for (WeCustomer customer : weCustomers) {
            CustomerSopVO customerSopVO = new CustomerSopVO();
            BeanUtils.copyProperties(customer, customerSopVO);
            customerSopVO.setHeadImageUrl(customer.getAvatar());
            customerSopVO.setMainDepartmentName(customer.getDepartmentName());
            customerSopVO.setCorpFullName(StringUtils.isBlank(customer.getCorpFullName()) ? customer.getCorpName() : customer.getCorpFullName());
            customerSopVOList.add(customerSopVO);

            sopUserVO = new SopUserVO();
            BeanUtils.copyProperties(customerSopVO, sopUserVO);
            sopUserVO.setName(customer.getName());
            sopUserVO.setUserId(customer.getUserId());
            userList.add(sopUserVO);
        }
        sopDetailVO.setCustomerSopVOList(customerSopVOList);
        sopDetailVO.setUserList(userList);
    }

    /**
     * 为空的列表赋默认值
     *
     * @param sopDetailVO sop详情
     */
    private void setDefaultList(SopDetailVO sopDetailVO) {
        sopDetailVO.setGroupSopList(CollectionUtils.isNotEmpty(sopDetailVO.getGroupSopList()) ? sopDetailVO.getGroupSopList() : new ArrayList<>());
        sopDetailVO.setCustomerSopVOList(CollectionUtils.isNotEmpty(sopDetailVO.getCustomerSopVOList()) ? sopDetailVO.getCustomerSopVOList() : new ArrayList<>());
        sopDetailVO.setUserList(CollectionUtils.isNotEmpty(sopDetailVO.getUserList()) ? sopDetailVO.getUserList() : new ArrayList<>());
    }


    @Override
    public List<BaseWeOperationsCenterSopVo> list(String corpId, Integer sopType, String name, String userName, Integer isOpen) {
        return baseMapper.list(corpId, sopType, name, userName, isOpen);
    }

    /**
     * 获取sop
     *
     * @param sopId  id
     * @param corpId 企业id
     * @return {@link WeOperationsCenterSopVo}
     */
    private WeOperationsCenterSopVo getSop(Long sopId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return baseMapper.getSop(corpId, sopId);
    }

    private void setSopUserVO(@NotEmpty List<WeUserVO> weUsers, @NotEmpty List<SopUserVO> sopUserVOList) {
        weUsers.forEach(weUserVO -> {
            SopUserVO sopUserVO = new SopUserVO();
            BeanUtils.copyProperties(weUserVO, sopUserVO);
            sopUserVOList.add(sopUserVO);
        });
    }
}