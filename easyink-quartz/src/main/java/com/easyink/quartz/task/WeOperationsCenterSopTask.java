package com.easyink.quartz.task;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.*;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.vo.sop.SopAttachmentVO;
import com.easyink.wecom.domain.vo.sop.WeSopUserIdAndTargetIdVO;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.radar.WeRadarService;
import com.easyink.wecom.utils.ApplicationMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static com.easyink.common.enums.WeOperationsCenterSop.SopTypeEnum.*;
import static com.easyink.common.utils.DateUtils.HH_MM_SS;
import static com.easyink.common.utils.DateUtils.YYYY_MM_DD_HH_MM;

/**
 * 类名：WeOperationsCenterSopTask
 *
 * @author admin
 * @adata 2021-12-03 09:08
 **/
@Slf4j
@Component("weOperationsCenterSopTask")
public class WeOperationsCenterSopTask {

    private final WeOperationsCenterSopService sopService;
    private final WeOperationsCenterSopScopeService sopScopeService;
    private final WeOperationsCenterGroupSopFilterService groupSopFilterService;
    private final WeOperationsCenterGroupSopFilterCycleService groupSopFilterCycleService;
    private final WeGroupService weGroupService;
    private final WeOperationsCenterSopRulesService sopRulesService;
    private final ApplicationMessageUtil applicationMessageUtil;
    private final WeOperationsCenterSopDetailService sopDetailService;
    private final WeOperationsCenterCustomerSopFilterService customerSopFilterService;
    private final WeCustomerService weCustomerService;
    private final WeWordsDetailService weWordsDetailService;
    private final WeOperationsCenterSopTaskService sopTaskService;
    private final WeCustomerTrajectoryService trajectoryService;

    @Autowired
    public WeOperationsCenterSopTask(WeOperationsCenterSopService sopService, WeOperationsCenterSopScopeService sopScopeService, WeOperationsCenterGroupSopFilterService groupSopFilterService, WeOperationsCenterGroupSopFilterCycleService groupSopFilterCycleService, WeGroupService weGroupService, WeOperationsCenterSopRulesService sopRulesService, ApplicationMessageUtil applicationMessageUtil, WeOperationsCenterSopDetailService sopDetailService, WeOperationsCenterCustomerSopFilterService customerSopFilterService, WeCustomerService weCustomerService, WeWordsDetailService weWordsDetailService, WeOperationsCenterSopTaskService sopTaskService, WeCustomerTrajectoryService trajectoryService) {
        this.sopService = sopService;
        this.sopScopeService = sopScopeService;
        this.groupSopFilterService = groupSopFilterService;
        this.groupSopFilterCycleService = groupSopFilterCycleService;
        this.weGroupService = weGroupService;
        this.sopRulesService = sopRulesService;
        this.applicationMessageUtil = applicationMessageUtil;
        this.sopDetailService = sopDetailService;
        this.customerSopFilterService = customerSopFilterService;
        this.weCustomerService = weCustomerService;
        this.weWordsDetailService = weWordsDetailService;
        this.sopTaskService = sopTaskService;
        this.trajectoryService = trajectoryService;
    }

    /**
     * 运营中心SOP定时任务
     * cron: 0 * * * * ?
     */
    public void execute() {
        //查询所有已开启的sop任务
        List<WeOperationsCenterSopEntity> list = sopService.findSwitchOpenSop();
        if (CollectionUtils.isNotEmpty(list)) {
            log.info("开始执行运营中心SOP任务------->");
            //防止业务时间过长将当前时间放到最外层
            Date nowDate = new Date();
            for (WeOperationsCenterSopEntity sopEntity : list) {
                try {
                    switch (WeOperationsCenterSop.SopTypeEnum.getSopTypeEnumByType(sopEntity.getSopType())) {
                        case TIME_TASK:
                            groupSopTask(sopEntity, TIME_TASK.getSopType(), TIME_TASK.getContent(), nowDate);
                            break;
                        case CYCLE:
                            groupSopTask(sopEntity, CYCLE.getSopType(), CYCLE.getContent(), nowDate);
                            break;
                        case ACTIVITY:
                            customerSopTask(ACTIVITY.getSopType(),sopEntity, ACTIVITY.getContent(), nowDate);
                            break;
                        case BIRTH_DAT:
                            customerSopTask(BIRTH_DAT.getSopType(),sopEntity, BIRTH_DAT.getContent(), nowDate);
                            break;
                        case NEW_CUSTOMER:
                            customerSopTask(NEW_CUSTOMER.getSopType(),sopEntity, NEW_CUSTOMER.getContent(), nowDate);
                            break;
                        case GROUP_CALENDAR:
                            groupSopTask(sopEntity, GROUP_CALENDAR.getSopType(), GROUP_CALENDAR.getContent(), nowDate);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("weOperationsCenterSopTask error!! data={},e={}", JSONObject.toJSONString(sopEntity), ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    /**
     * 客户SOP定时任务
     */
    private void customerSopTask(Integer sopType, WeOperationsCenterSopEntity sopEntity, String content, Date nowDate) {
        if (sopEntity == null || StringUtils.isBlank(content) || sopEntity.getId() == null || StringUtils.isBlank(sopEntity.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String corpId = sopEntity.getCorpId();
        Long sopId = sopEntity.getId();
        //1、查询新客/生日/活动SOP 对应的客户数据
        List<WeSopUserIdAndTargetIdVO> customerList = getCustomerData(sopType,sopEntity.getCorpId(), sopEntity.getId());
        //2、使用范围有数据则查询规则是否满足条件
        List<WeOperationsCenterSopRulesEntity> sopRules = getSopRules(corpId, sopId);
        if (CollectionUtils.isEmpty(sopRules)) {
            log.info("weOperationsCenterSopTask. 新客/生日/活动SOP查无规则，结束执行！corpId={},sopId={}", corpId, sopId);
            return;
        }
        //3、满足规则的客户,加入执行任务
        executeGroupSopTask(sopType, content, sopEntity.getName(), corpId, sopId, customerList, sopRules, nowDate, RadarChannelEnum.CUSTOMER_SOP.getTYPE());
    }

    /**
     * 获得客户数据
     *
     * @param sopType sop类型
     * @param corpId 企业id
     * @param sopId sopid
     * @return {@link List<WeSopUserIdAndTargetIdVO>}
     */
    private List<WeSopUserIdAndTargetIdVO> getCustomerData(Integer sopType, String corpId, Long sopId) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeSopUserIdAndTargetIdVO> resultList = new ArrayList<>();
        //查询新客/生日SOP的过滤条件
        LambdaQueryWrapper<WeOperationsCenterCustomerSopFilterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId, corpId)
                .eq(WeOperationsCenterCustomerSopFilterEntity::getSopId, sopId).last(GenConstants.LIMIT_1);
        WeOperationsCenterCustomerSopFilterEntity sopFilter = customerSopFilterService.getOne(wrapper);
        if (sopFilter == null) {
            log.error("weOperationsCenterSopTask. 新客/生日SOP的过滤条件为空,不再继续执行.corpId={},sopId={}", corpId, sopId);
            return resultList;
        }
        List<WeCustomer> weCustomers = weCustomerService.listOfUseCustomer(corpId, sopFilter);
        for (WeCustomer weCustomer : weCustomers) {
            Date settingTime = BIRTH_DAT.getSopType().equals(sopType) ? weCustomer.getBirthday() : weCustomer.getCreateTime();
            if (settingTime != null) {
                resultList.add(new WeSopUserIdAndTargetIdVO(weCustomer.getUserId(), weCustomer.getExternalUserid(), weCustomer.getName(), settingTime, weCustomer.getBirthday()));
            }
        }
        return resultList;
    }

    /**
     * 群SOP定时任务
     */
    private void groupSopTask(WeOperationsCenterSopEntity sopEntity, Integer sopType, String content, Date nowDate) {
        if (sopEntity == null || sopType == null || StringUtils.isBlank(content) || StringUtils.isBlank(sopEntity.getCorpId()) || sopEntity.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String corpId = sopEntity.getCorpId();
        Long sopId = sopEntity.getId();
        //1、查询使用范围
        List<WeSopUserIdAndTargetIdVO> groupSopScopeList = getGroupSopScopeList(corpId, sopId, sopType, sopEntity.getFilterType());
        if (CollectionUtils.isEmpty(groupSopScopeList)) {
            log.info("weOperationsCenterSopTask. 定时SOP查无使用范围，结束执行！corpId={},sopId={}", corpId, sopId);
            return;
        }
        //2、使用范围有数据则查询规则是否满足条件
        List<WeOperationsCenterSopRulesEntity> sopRules = getSopRules(corpId, sopId);
        if (CollectionUtils.isEmpty(sopRules)) {
            log.info("weOperationsCenterSopTask. 定时SOP查无规则，结束执行！corpId={},sopId={}", corpId, sopId);
            return;
        }
        //3、满足规则的客户,加入执行任务
        if (GROUP_CALENDAR.getSopType().equals(sopType)) {
            executeGroupSopTask(sopType, content, sopEntity.getName(), corpId, sopId, groupSopScopeList, sopRules, nowDate, RadarChannelEnum.GROUP_CALENDAR.getTYPE());
        } else {
            executeGroupSopTask(sopType, content, sopEntity.getName(), corpId, sopId, groupSopScopeList, sopRules, nowDate, RadarChannelEnum.GROUP_SOP.getTYPE());
        }
    }

    /**
     * 执行任务
     *
     * @param sopType           sop类型
     * @param content           提醒内容
     * @param name              SOP名称
     * @param corpId            企业ID
     * @param sopId             sopId
     * @param groupSopScopeList 受作用的群聊
     * @param sopRules          sop规则
     * @param nowDate           当前时间
     */
    private void executeGroupSopTask(Integer sopType, String content, String name, String corpId, Long sopId, List<WeSopUserIdAndTargetIdVO> groupSopScopeList, List<WeOperationsCenterSopRulesEntity> sopRules, Date nowDate, int radarChannelType) {
        if (StringUtils.isBlank(corpId) || sopId == null || CollectionUtils.isEmpty(groupSopScopeList) || CollectionUtils.isEmpty(sopRules)) {
            log.error("weOperationsCenterSopTask.executeGroupSopTask 数据为空！不再继续执行.corpId={},sopId={},groupSopScopeList={},sopRules={}", corpId, sopId, JSONObject.toJSONString(groupSopScopeList), JSONObject.toJSONString(sopRules));
            return;
        }
        List<String> userIdList;
        boolean conformTime;
        Map<String, List<WeSopUserIdAndTargetIdVO>> dataMap;
        List<WeOperationsCenterSopDetailEntity> detailList = new ArrayList<>();
        //员工Id,客户id/群id
        Map<String, String> saveRuleNameMap = new HashMap<>(sopRules.size());
        for (WeOperationsCenterSopRulesEntity rulesEntity : sopRules) {
            dataMap = new HashMap<>();
            for (WeSopUserIdAndTargetIdVO targetVO : groupSopScopeList) {
                //userId为空则跳过
                if (StringUtils.isBlank(targetVO.getUserId())) {
                    continue;
                }
                //生日过后提醒类型修改
                if (BIRTH_DAT.getSopType().equals(sopType) && WeOperationsCenterSop.AlertTypeEnum.TYPE_1.getAlertType().equals(rulesEntity.getAlertType())){
                    rulesEntity.setAlertType(WeOperationsCenterSop.AlertTypeEnum.TYPE_7.getAlertType());
                }
                //判断时间是否符合条件
                conformTime = DateUtils.isConformTime(nowDate, targetVO.getCreateTime(), rulesEntity.getAlertType(), rulesEntity.getAlertData1(), rulesEntity.getAlertData2());
                //时间不合法
                if (!conformTime) {
                    continue;
                }
                //符合的数据放入dataMap,提醒人相同的统一处理
                if (!dataMap.containsKey(targetVO.getUserId())) {
                    dataMap.put(targetVO.getUserId(), new ArrayList<>());
                }
                detailList.add(new WeOperationsCenterSopDetailEntity(corpId, sopId, rulesEntity.getId(), targetVO.getUserId(), targetVO.getTargetId(), nowDate));
                saveRuleNameMap.put(targetVO.getUserId()+rulesEntity.getId(), rulesEntity.getName());
                dataMap.get(targetVO.getUserId()).add(targetVO);
            }

            for (Map.Entry<String, List<WeSopUserIdAndTargetIdVO>> obj : dataMap.entrySet()) {
                userIdList = new ArrayList<>();
                userIdList.add(obj.getKey());
                List<WeSopUserIdAndTargetIdVO> value = obj.getValue();
                String urlContent = applicationMessageUtil.getUrlContent(corpId, CommunityTaskType.GROUP_SOP.getType());
                try {//异常捕获保证消息发送失败后不影响任务的保存
                    applicationMessageUtil.sendAppMessage(userIdList, corpId, content, name, value.get(0).getTargetName(), String.valueOf(value.size()), rulesEntity.getName(), urlContent);
                } catch (ForestRuntimeException e) {
                    log.error("发送sop应用消息失败：userId:{}, corpId:{}, ex:{}", obj.getKey(), corpId, ExceptionUtils.getStackTrace(e));
                }
            }
        }
        //批量插入执行数据
        if (CollectionUtils.isNotEmpty(detailList)) {
            sopDetailService.saveBatch(detailList);
        }
        saveSopTask(detailList, corpId, saveRuleNameMap, radarChannelType);
    }

    /**
     * 保存sop待办任务
     *
     * @param detailList      任务详情
     * @param corpId          企业id
     * @param saveRuleNameMap 规则名称对应map
     * @param channelType     对应雷达渠道的SOP type
     */
    private void saveSopTask(List<WeOperationsCenterSopDetailEntity> detailList, String corpId, Map<String, String> saveRuleNameMap, int channelType) {
        for (WeOperationsCenterSopDetailEntity sopDetailEntity : detailList) {
            List<WeOperationsCenterSopTaskEntity> taskEntityList = new ArrayList<>();
            List<SopAttachmentVO> sopAttachmentVos = weWordsDetailService.listOfRuleId(sopDetailEntity.getRuleId());
            String userId = sopDetailEntity.getUserId();
            final String sopRuleName = saveRuleNameMap.get(userId + sopDetailEntity.getRuleId());
            for (SopAttachmentVO sopAttachmentVO : sopAttachmentVos) {
                WeOperationsCenterSopTaskEntity sopTask = new WeOperationsCenterSopTaskEntity();
                if (AttachmentTypeEnum.RADAR.getMessageType().equals(sopAttachmentVO.getMediaType())) {
                    try {
                        buildRadarAttachment(sopAttachmentVO, channelType, userId, corpId, sopRuleName);
                    } catch (Exception e) {
                        log.error("sop处理构建雷达素材失败, corpId: {},sopRuleName: {},userId: {}, sopType: {}, radarInfo: {}, e: {}", corpId, sopRuleName, userId, channelType, sopAttachmentVO, ExceptionUtils.getMessage(e));
                        continue;
                    }
                }
                BeanUtils.copyProperties(sopAttachmentVO, sopTask);
                sopTask.setId(SnowFlakeUtil.nextId());
                taskEntityList.add(sopTask);
            }
            if (CollectionUtils.isNotEmpty(taskEntityList)) {
                sopTaskService.saveBatch(taskEntityList);
                //插入客户轨迹待办消息
                List<String> taskIds = taskEntityList.stream().filter(task -> task.getId() != null).map(task -> task.getId().toString()).collect(Collectors.toList());
                log.info("[保存sopTask] corpId: {}, userId: {}, targetId: {}, sopDetailId: {}, taskIds:{}", corpId, userId, sopDetailEntity.getTargetId(), sopDetailEntity.getId(), String.join(StrUtil.COMMA, taskIds));
                saveCustomerTrajectorySopTask(sopDetailEntity.getAlertTime(), saveRuleNameMap.get(userId + sopDetailEntity.getRuleId()), corpId, sopDetailEntity.getId(), String.join(StrUtil.COMMA, taskIds), userId, sopDetailEntity.getTargetId());
            }
        }
    }


    /**
     * 构建雷达附件
     *
     * @param sopAttachmentVO
     * @param channelType
     * @param userId
     * @param corpId
     * @param sopRuleName
     */
    private void buildRadarAttachment(SopAttachmentVO sopAttachmentVO, int channelType, String userId, String corpId, String sopRuleName) {
        final AttachmentParam radarAttachment = SpringUtils.getBean(WeRadarService.class).getRadarShortUrl(sopAttachmentVO.getRadarId(), channelType, userId, corpId, sopRuleName);
        if (ObjectUtils.isEmpty(radarAttachment)) {
            return;
        }
        sopAttachmentVO.setTitle(radarAttachment.getContent());
        sopAttachmentVO.setContent(radarAttachment.getDescription());
        sopAttachmentVO.setCoverUrl(radarAttachment.getPicUrl());
        sopAttachmentVO.setUrl(radarAttachment.getUrl());
        sopAttachmentVO.setMediaType(WeCategoryMediaTypeEnum.LINK.getMediaType());
    }

    /**
     * 插入客户轨迹待办消息
     * @param alertTime
     * @param ruleName 规则名称
     * @param corpId 企业id
     * @param detailId 消息详情id
     * @param taskIds  任务id
     * @param userId 员工id
     * @param targetId 目标id
     */
    private void saveCustomerTrajectorySopTask(Date alertTime, String ruleName, String corpId, Long detailId, String taskIds, String userId, String targetId) {
        if (detailId != null && StringUtils.isNotEmpty(taskIds)) {
            WeCustomerTrajectory weCustomerTrajectory = new WeCustomerTrajectory();
            weCustomerTrajectory.setDetailId(detailId);
            weCustomerTrajectory.setCorpId(corpId);
            weCustomerTrajectory.setUserId(userId);
            weCustomerTrajectory.setExternalUserid(targetId);
            weCustomerTrajectory.setSopTaskIds(taskIds);
            weCustomerTrajectory.setContent(ruleName);
            weCustomerTrajectory.setTrajectoryType(CustomerTrajectoryEnums.Type.TO_DO.getDesc());
            weCustomerTrajectory.setCreateDate(alertTime);
            weCustomerTrajectory.setStartTime(Time.valueOf(DateUtils.parseDateToStr(HH_MM_SS,alertTime)));
            trajectoryService.save(weCustomerTrajectory);
        }
    }

    /**
     * 查询SOP下的规则
     *
     * @param corpId 企业ID
     * @param sopId  sopId
     * @return List<WeOperationsCenterSopRulesEntity>
     */
    private List<WeOperationsCenterSopRulesEntity> getSopRules(String corpId, Long sopId) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterSopRulesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopRulesEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopRulesEntity::getSopId, sopId);
        return sopRulesService.list(wrapper);
    }

    /**
     * 查询符合条件的群聊数据
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param sopType    sop类型
     * @param filterType 筛选类型
     * @return List<WeSopUserIdAndTargetIdVO>
     */
    private List<WeSopUserIdAndTargetIdVO> getGroupSopScopeList(String corpId, Long sopId, Integer sopType, Integer filterType) {
        if (StringUtils.isBlank(corpId) || sopId == null || sopType == null || filterType == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeSopUserIdAndTargetIdVO> resultList = new ArrayList<>();
        List<WeGroup> groupList;
        //循环SOP,则先查询循环周期是否符合
        if (CYCLE.getSopType().equals(sopType)) {
            //查询循环周期
            LambdaQueryWrapper<WeOperationsCenterGroupSopFilterCycleEntity> groupSopFilterCycleWrapper = new LambdaQueryWrapper<>();
            groupSopFilterCycleWrapper.eq(WeOperationsCenterGroupSopFilterCycleEntity::getCorpId, corpId)
                    .eq(WeOperationsCenterGroupSopFilterCycleEntity::getSopId, sopId);
            WeOperationsCenterGroupSopFilterCycleEntity sopFilterCycle = groupSopFilterCycleService.getOne(groupSopFilterCycleWrapper);
            if (sopFilterCycle == null) {
                log.warn("未查询到循环SOP的循环周期，放弃执行！");
                return resultList;
            }
            //查询是否还在循环周期内
            Date cycleStart = DateUtils.dateTime(YYYY_MM_DD_HH_MM, sopFilterCycle.getCycleStart());
            Date cycleEnd = DateUtils.dateTime(YYYY_MM_DD_HH_MM, sopFilterCycle.getCycleEnd());
            long currentTimeMillis = System.currentTimeMillis();
            if (!(cycleStart.getTime() <= currentTimeMillis && currentTimeMillis < cycleEnd.getTime())) {
                log.info("weOperationsCenterSopTask,不在指定周期内,不继续执行！corpId={},sopId={},cycleStart={},cycleEnd={}", corpId, sopId, sopFilterCycle.getCycleStart(), sopFilterCycle.getCycleEnd());
                return resultList;
            }
        }

        //筛选群聊
        if (WeOperationsCenterSop.FilterTypeEnum.FILTER.getFilterType().equals(filterType)) {

            //查询使用群聊的条件
            LambdaQueryWrapper<WeOperationsCenterGroupSopFilterEntity> filterWrapper = new LambdaQueryWrapper<>();
            filterWrapper.eq(WeOperationsCenterGroupSopFilterEntity::getCorpId, corpId).eq(WeOperationsCenterGroupSopFilterEntity::getSopId, sopId);

            WeOperationsCenterGroupSopFilterEntity filterEntity = groupSopFilterService.getOne(filterWrapper);
            if (filterEntity == null) {
                log.warn("weOperationsCenterSopTask,循环SOP查无筛选条件,不继续执行！corpId={},sopId={}", corpId, sopId);
                return resultList;
            }
            //查询符合条件的群数据
            groupList = weGroupService.listNoRelTag(corpId, filterEntity.getTagId(), filterEntity.getOwner(), filterEntity.getCreateTime(), filterEntity.getEndTime());

            //查询这分钟内符合条件的保存到scope
            List<String> chatIdList = groupList.stream().map(WeGroup::getChatId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(chatIdList)) {
                //此处用不到部门
                sopScopeService.updateSopScope(corpId, sopId, chatIdList, null);
            } else {
                //当查无符合条件的数据时，清空scope数据
                List<Long> sopIdList = new ArrayList<>();
                sopIdList.add(sopId);
                sopScopeService.delSopByCorpIdAndSopIdList(corpId, sopIdList);
                return resultList;
            }
        }
        LambdaQueryWrapper<WeOperationsCenterSopScopeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopScopeEntity::getCorpId, corpId)
                .eq(WeOperationsCenterSopScopeEntity::getSopId, sopId);
        List<WeOperationsCenterSopScopeEntity> list = sopScopeService.list(wrapper);

        //存在群接收者目标数据
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Date> dateMap = list.stream().collect(Collectors.toMap(WeOperationsCenterSopScopeEntity::getTargetId, WeOperationsCenterSopScopeEntity::getCreateTime));

            List<String> collect = list.stream().map(WeOperationsCenterSopScopeEntity::getTargetId).collect(Collectors.toList());
            //查询群对应的群主
            LambdaQueryWrapper<WeGroup> weGroupWrapper = new LambdaQueryWrapper<>();
            weGroupWrapper.eq(WeGroup::getCorpId, corpId)
                    .in(WeGroup::getChatId, collect);
            groupList = weGroupService.list(weGroupWrapper);
            for (WeGroup weGroup : groupList) {
                resultList.add(new WeSopUserIdAndTargetIdVO(weGroup.getOwner(), weGroup.getChatId(), weGroup.getGroupName(), dateMap.get(weGroup.getChatId())));
            }
        }
        return resultList;
    }


}
