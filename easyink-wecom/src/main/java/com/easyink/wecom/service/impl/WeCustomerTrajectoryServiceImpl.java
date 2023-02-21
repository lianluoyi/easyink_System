package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.SysProperty;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.CustomerExtendPropertyEnum;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.ExternalGroupMemberTypeEnum;
import com.easyink.common.enums.MessageType;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormOperRecord;
import com.easyink.wecom.domain.entity.radar.WeRadar;
import com.easyink.wecom.domain.vo.sop.SopAttachmentVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeCustomerTrajectoryMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.*;
import io.swagger.annotations.ApiModel;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@ApiModel("活动轨迹相关Service")
public class WeCustomerTrajectoryServiceImpl extends ServiceImpl<WeCustomerTrajectoryMapper, WeCustomerTrajectory> implements WeCustomerTrajectoryService {


    private final WeMessagePushClient weMessagePushClient;
    private final WeCustomerMapper weCustomerMapper;
    private final WeUserMapper weUserMapper;
    private final WeGroupService weGroupService;
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    private final WeTagService weTagService;
    private final WeOperationsCenterSopDetailService sopDetailService;
    private final WeOperationsCenterSopTaskService sopTaskService;

    @Autowired
    @Lazy
    public WeCustomerTrajectoryServiceImpl(WeMessagePushClient weMessagePushClient, WeCustomerMapper weCustomerMapper, WeUserMapper weUserMapper, WeGroupService weGroupService, WeCustomerExtendPropertyService weCustomerExtendPropertyService, WeTagService weTagService, WeOperationsCenterSopDetailService sopDetailService, WeOperationsCenterSopTaskService sopTaskService) {
        this.weMessagePushClient = weMessagePushClient;
        this.weCustomerMapper = weCustomerMapper;
        this.weUserMapper = weUserMapper;
        this.weGroupService = weGroupService;
        this.weCustomerExtendPropertyService = weCustomerExtendPropertyService;
        this.weTagService = weTagService;
        this.sopDetailService = sopDetailService;
        this.sopTaskService = sopTaskService;
    }

    /**
     * 待办理处理通知
     */
    @Override
    public void waitHandleMsg(String url) {
        //获取所有满足时间的通知
        List<WeCustomerTrajectory> trajectories = this.list(new LambdaQueryWrapper<WeCustomerTrajectory>()
                .ne(WeCustomerTrajectory::getStatus, Constants.DELETE_CODE)
                .last(" AND concat_ws(' ',create_date,start_time)  <= DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s')" +
                        " AND concat_ws(' ',create_date,end_time) >= DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s')"));
        if (CollUtil.isNotEmpty(trajectories)) {

            List<WeCustomer> weCustomers = weCustomerMapper.selectBatchIds(
                    trajectories.stream().map(WeCustomerTrajectory::getExternalUserid).collect(Collectors.toList())
            );
            Map<String, WeCustomer> weCustomerMap
                    = weCustomers.stream().collect(Collectors.toMap(WeCustomer::getExternalUserid, a -> a, (k1, k2) -> k1));
            //给员工发送通知
            trajectories.stream().forEach(trajectory -> weMessagePushClient.sendMessageToUser(WeMessagePushDTO.builder()
                            .touser(trajectory.getUserId())
                            .msgtype(MessageType.TEXT.getMessageType())
                            .agentid(Integer.parseInt(trajectory.getAgentId()))
                            .text(TextMessageDTO.builder()
                                    .content("您有一项关于客户" + weCustomerMap.get(trajectory.getExternalUserid()).getName() + "的待办任务提醒，请尽快处理，\n<a href=" + url + ">点击查看客户详情。</a>")
                                    .build())
                            .build(),
                    trajectory.getAgentId(), trajectory.getCorpId()
            ));

        }
    }


    @Override
    public void recordEditCustomerOperation(String corpId, String userId, String externalUserId, String updateBy, EditCustomerDTO dto) {
        if (StringUtils.isAnyBlank(userId, corpId, externalUserId) || dto == null) {
            return;
        }
        List<WeCustomerTrajectory> list = new ArrayList<>();
        Time now = new Time(System.currentTimeMillis());
        try {
            Class clazz = dto.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(Boolean.TRUE);
                if (field.get(dto) == null) {
                    continue;
                }
                if (field.isAnnotationPresent(SysProperty.class)) {
                    // 通过注解获取字段的中文名称
                    SysProperty sysProperty = field.getAnnotation(SysProperty.class);
                    String name = sysProperty.name();
                    String value = String.valueOf(field.get(dto));
                    // 组装记录文本
                    String content = buildContent(updateBy, name);
                    // 构建记录实体
                    list.add(WeCustomerTrajectory.builder()
                            .corpId(corpId)
                            .userId(userId)
                            .externalUserid(externalUserId)
                            .trajectoryType(CustomerTrajectoryEnums.Type.INFO.getDesc())
                            .createDate(new Date())
                            .detail(value)
                            .content(content)
                            .subType(CustomerTrajectoryEnums.SubType.EDIT_REMARK.getType())
                            .startTime(now)
                            .detailId(Constants.DEFAULT_ID)
                            .build()
                    );
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
            }
        } catch (Exception e) {
            // 不让记录操作异常 导致编辑资料失败
            log.error("[记录客户信息动态]记录修改客户资料和跟进人备注异常,corpId:{},userId{},customer:{},dto:{}, e:{}", corpId, userId, externalUserId, dto, ExceptionUtils.getStackTrace(e));
        }

    }


    @Override
    public void recordEditExtendPropOperation(String corpId, String userId, String externalUserId, String updateBy, List<BaseExtendPropertyRel> extendProperties) {
        if (StringUtils.isAnyBlank(userId, corpId, externalUserId) || CollectionUtils.isEmpty(extendProperties)) {
            return;
        }
        List<WeCustomerTrajectory> list = new ArrayList<>();
        Time now = new Time(System.currentTimeMillis());
        try {
            // 获取该用户本次修改的自定义属性,名称->值 的映射
            Map<WeCustomerExtendProperty, String> prop2valueMap = weCustomerExtendPropertyService.mapProperty2Value(extendProperties, corpId);
            // 把扩展属性按id分组后用,拼接
            for (Map.Entry<WeCustomerExtendProperty, String> prop : prop2valueMap.entrySet()) {
                if (prop.getKey() == null) {
                    continue;
                }
                // 构建记录实体
                list.add(WeCustomerTrajectory.builder()
                        .corpId(corpId)
                        .userId(userId)
                        .externalUserid(externalUserId)
                        .createDate(new Date())
                        .trajectoryType(CustomerTrajectoryEnums.Type.INFO.getDesc())
                        .content(buildContent(updateBy, prop.getKey().getName()))
                        .subType(CustomerExtendPropertyEnum.getByType(prop.getKey().getType()).getOprSubType().getType())
                        .detail(prop.getValue())
                        .startTime(now)
                        .detailId(Constants.DEFAULT_ID)
                        .build()
                );
            }
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
            }
        } catch (Exception e) {
            // 不让记录操作异常 导致编辑资料失败
            log.error("[记录客户信息动态]修改客户扩展字段异常,corpId:{},userId{},customer:{},list:{}, e:{}", corpId, userId, externalUserId, extendProperties, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void recordEditTagOperation(String corpId, String userId, String externalUserId, String updateBy, List<WeTag> editTags) {
        if (StringUtils.isAnyBlank(userId, corpId, externalUserId) || editTags == null) {
            return;
        }
        List<WeCustomerTrajectory> list = new ArrayList<>();
        Time now = new Time(System.currentTimeMillis());
        try {
            String editTagStr = StringUtils.EMPTY;
            if (CollectionUtils.isNotEmpty(editTags)) {
                List<String> tagIdList = editTags.stream().map(WeTag::getTagId).collect(Collectors.toList());
                List<WeTag> tagList = weTagService.list(new LambdaQueryWrapper<WeTag>()
                        .eq(WeTag::getCorpId, corpId)
                        .in(WeTag::getTagId, tagIdList)
                );
                editTagStr = tagList.stream().map(WeTag::getName).collect(Collectors.joining(","));
            }
            list.add(WeCustomerTrajectory.builder()
                    .corpId(corpId)
                    .userId(userId)
                    .externalUserid(externalUserId)
                    .createDate(new Date())
                    .trajectoryType(CustomerTrajectoryEnums.Type.INFO.getDesc())
                    .content(buildContent(updateBy, GenConstants.CUSTOMER_TAG))
                    .subType(CustomerTrajectoryEnums.SubType.EDIT_TAG.getType())
                    .detail(editTagStr)
                    .startTime(now)
                    .detailId(Constants.DEFAULT_ID)
                    .build()
            );
            if (CollectionUtils.isNotEmpty(list)) {
                this.saveBatch(list);
            }
        } catch (Exception e) {
            // 不让记录操作异常 导致编辑资料失败
            log.error("[记录客户信息动态]修改客户扩展字段异常,corpId:{},userId{},customer:{},list:{}, e:{}", corpId, userId, externalUserId, editTags, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void recordEditOperation(EditCustomerDTO dto) {
        if (dto == null || StringUtils.isAnyBlank(dto.getCorpId(), dto.getUserId(), dto.getExternalUserid())) {
            return;
        }
        String userId = dto.getUserId();
        String externalUserId = dto.getExternalUserid();
        String corpId = dto.getCorpId();
        String updateBy = dto.getUpdateBy();
        // 记录客户资料修改操作
        this.recordEditCustomerOperation(corpId, userId, externalUserId, updateBy, dto);
        // 记录客户扩展字段修改操作
        this.recordEditExtendPropOperation(corpId, userId, externalUserId, updateBy, dto.getExtendProperties());
        // 记录客户标签修改操作
        this.recordEditTagOperation(corpId, userId, externalUserId, updateBy, dto.getEditTag());
    }

    /**
     * 组装记录文案
     *
     * @param updateBy 操作人
     * @param name     名称
     * @return 记录文案
     */
    private String buildContent(String updateBy, String name) {
        return GenConstants.EDIT_CUSTOMER_RECORD_MSG
                .replace(GenConstants.USER_NAME, updateBy)
                .replace(GenConstants.PROPERTY_NAME, name);
    }


    @Override
    public void saveActivityRecord(List<WeGroupMember> list, String subType) {
        if (CollectionUtils.isEmpty(list) || !CustomerTrajectoryEnums.SubType.isGroupOperation(subType)) {
            log.info("[活动轨迹]记录客户加入/退出群聊,参数有误,list:{},type:{}", list, subType);
            return;
        }
        // 获取客户轨迹记录描述模板
        String model = CustomerTrajectoryEnums.SubType.getByType(subType).getDesc();
        if (StringUtils.isBlank(model)) {
            return;
        }
        Time now = new Time(System.currentTimeMillis());
        // 记录描述内容
        String content;
        for (WeGroupMember member : list) {
            if (ExternalGroupMemberTypeEnum.INTERNAL.getType().equals(member.getJoinType())) {
                // 内部成员不记录
                continue;
            }
            WeGroup group = weGroupService.getById(member.getChatId());
            if (group == null || StringUtils.isBlank(group.getGroupName())) {
                log.info("[活动轨迹]记录客户加入/退出群聊,群聊详情获取失败,member:{}", member);
                continue;
            }
            // 组装文本
            content = model.replace(GenConstants.CUSTOMER, member.getMemberName())
                    .replace(GenConstants.GROUP_NAME, group.getGroupName());
            WeCustomerTrajectory trajectory = WeCustomerTrajectory.builder()
                    .externalUserid(member.getUserId())
                    .trajectoryType(CustomerTrajectoryEnums.Type.ACTIVITY.getDesc())
                    .content(content)
                    .createDate(new Date())
                    .detail(group.getGroupName())
                    .subType(subType)
                    .corpId(member.getCorpId())
                    .startTime(now)
                    .sopTaskIds(Strings.EMPTY)
                    .detailId(Constants.DEFAULT_ID)
                    .build();
            this.saveOrUpdate(trajectory);
        }
    }

    @Override
    public void saveActivityRecord(String corpId, String userId, String externalUserId, String subType) {
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId)
                || !CustomerTrajectoryEnums.SubType.isUserOperation(subType)) {
            log.info("[活动轨迹]记录客户添加/删除跟进人,参数有误,corpId:{},user:{},customer:{},type:{}", corpId, userId, externalUserId, subType);
            return;
        }
        // 获取描述模板
        String model = CustomerTrajectoryEnums.SubType.getByType(subType).getDesc();
        if (StringUtils.isBlank(model)) {
            return;
        }
        // 获取客户详情
        WeCustomer customer = weCustomerMapper.selectWeCustomerById(externalUserId, corpId);
        if (customer == null || customer.getName() == null) {
            log.info("[活动轨迹]记录客户添加/删除跟进人,获取客户详情失败,corpId:{},user:{},customer:{},type:{}", corpId, userId, externalUserId, subType);
            return;
        }
        // 获取跟进人详情
        WeUser user = weUserMapper.selectWeUserById(corpId, userId);
        if (user == null || user.getName() == null) {
            log.info("[活动轨迹]记录客户添加/删除跟进人,获取跟进人详情失败,corpId:{},user:{},customer:{},type:{}", corpId, userId, externalUserId, subType);
            return;
        }
        Time now = new Time(System.currentTimeMillis());
        String content = model.replace(GenConstants.CUSTOMER, customer.getName())
                .replace(GenConstants.USER_NAME, user.getName());
        WeCustomerTrajectory trajectory = WeCustomerTrajectory.builder()
                .userId(userId)
                .externalUserid(externalUserId)
                .trajectoryType(CustomerTrajectoryEnums.Type.ACTIVITY.getDesc())
                .content(content)
                .createDate(new Date())
                .detail(user.getAvatarMediaid())
                .subType(subType)
                .corpId(corpId)
                .startTime(now)
                .build();
        this.saveOrUpdate(trajectory);
    }

    @Override
    public List<WeCustomerTrajectory> listOfTrajectory(String corpId, String externalUserid, Integer trajectoryType, String userId) {
        boolean isTodo = false;
        LambdaQueryWrapper<WeCustomerTrajectory> wrapper = new LambdaQueryWrapper<WeCustomerTrajectory>()
                .eq(WeCustomerTrajectory::getCorpId, corpId)
                .ne(WeCustomerTrajectory::getStatus, CustomerTrajectoryEnums.TodoTaskStatusEnum.DEL.getCode())
                .eq(WeCustomerTrajectory::getExternalUserid, externalUserid)
                .eq(WeCustomerTrajectory::getTrajectoryType, trajectoryType);
        if (CustomerTrajectoryEnums.Type.TO_DO.getDesc().equals(trajectoryType)) {
            wrapper.eq(WeCustomerTrajectory::getUserId, userId);
            isTodo = true;
        }
        List<WeCustomerTrajectory> trajectoryList = baseMapper.selectList(wrapper);
        if (isTodo) {
            for (WeCustomerTrajectory weCustomerTrajectory : trajectoryList) {
                if (weCustomerTrajectory.getDetailId() != null && StringUtils.isNotBlank(weCustomerTrajectory.getSopTaskIds())) {
                    WeOperationsCenterSopDetailEntity sopDetailEntity = sopDetailService.getById(weCustomerTrajectory.getDetailId());
                    weCustomerTrajectory.setCreateDate(sopDetailEntity.getAlertTime());
                    Long[] taskIdArray = (Long[]) ConvertUtils.convert(weCustomerTrajectory.getSopTaskIds().split(StrUtil.COMMA), Long.class);
                    List<Long> taskIds = new ArrayList<>(Arrays.asList(taskIdArray));
                    //附件
                    List<WeOperationsCenterSopTaskEntity> taskEntityList = sopTaskService.listByIds(taskIds);
                    setSopAttachment(taskEntityList, weCustomerTrajectory);
                }
            }
        }
        // 如果是信息动态,则 过滤掉非当前操作人操作的记录
        if (CustomerTrajectoryEnums.Type.INFO.getDesc().equals(trajectoryType)) {
            LoginUser loginUser = LoginTokenService.getLoginUser();
            return trajectoryList.stream().filter(a -> loginUser.getUserId().equals(a.getUserId())).collect(Collectors.toList());
        }
        return trajectoryList;
    }

    @Override
    public void recordRadarClickOperation(WeRadar radar, WeUser user, WeCustomer customer) {
        if (user == null || customer == null || radar == null
                || StringUtils.isAnyBlank(user.getUserId(), customer.getExternalUserid(), customer.getName())) {
            log.info("[活动轨迹]记录雷达点击事件,参数缺失,radar:{},customer:{}.user:{}", radar, customer, user);
            return;
        }
        Time now = new Time(System.currentTimeMillis());
        String content = CustomerTrajectoryEnums.SubType.CLICK_RADAR.getDesc()
                .replace(GenConstants.CUSTOMER, customer.getName())
                .replace(GenConstants.RADAR_TITLE, radar.getRadarTitle());
        WeCustomerTrajectory trajectory = WeCustomerTrajectory.builder()
                .userId(user.getUserId())
                .externalUserid(customer.getExternalUserid())
                .trajectoryType(CustomerTrajectoryEnums.Type.ACTIVITY.getDesc())
                .content(content)
                .createDate(new Date())
                .detail(user.getAvatarMediaid())
                .subType(CustomerTrajectoryEnums.SubType.CLICK_RADAR.getType())
                .corpId(radar.getCorpId())
                .startTime(now)
                .build();
        this.saveOrUpdate(trajectory);
    }

    @Override
    public void recordFormCommitOperation(WeForm weForm, WeFormOperRecord weFormOperRecord, WeUser user, WeCustomer customer) {
        if (user == null || customer == null || weForm == null || weFormOperRecord == null
                || StringUtils.isAnyBlank(user.getUserId(), customer.getExternalUserid(), customer.getName())) {
            log.info("[活动轨迹] 记录表单提交事件,参数缺失,weForm:{}, weFormOperRecord:{}, customer:{}. user:{}", weForm, weFormOperRecord, customer, user);
            return;
        }
        recordFormOperation(CustomerTrajectoryEnums.SubType.COMMIT_FORM, weForm, weFormOperRecord, user, customer, weFormOperRecord.getCommitTime());
    }

    @Override
    public void recordFormClickOperation(WeForm weForm, WeFormOperRecord weFormOperRecord, WeUser user, WeCustomer customer) {
        if (user == null || customer == null || weForm == null || weFormOperRecord == null
                || StringUtils.isAnyBlank(user.getUserId(), customer.getExternalUserid(), customer.getName())) {
            log.info("[活动轨迹] 记录表单点击事件,参数缺失,weForm:{}, weFormOperRecord:{}, customer:{}. user:{}", weForm, weFormOperRecord, customer, user);
            return;
        }
        recordFormOperation(CustomerTrajectoryEnums.SubType.CLICK_FORM, weForm, weFormOperRecord, user, customer, weFormOperRecord.getCreateTime());
    }

    /**
     * 记录表单轨迹记录
     *
     * @param subType          {@link CustomerTrajectoryEnums.SubType}
     * @param weForm           {@link WeForm}
     * @param weFormOperRecord {@link WeFormOperRecord}
     * @param user             {@link WeUser}
     * @param customer         {@link WeCustomer}
     * @param eventTime        发生事件时间
     */
    public void recordFormOperation(CustomerTrajectoryEnums.SubType subType, WeForm weForm, WeFormOperRecord weFormOperRecord, WeUser user, WeCustomer customer, Date eventTime) {
        if (subType == null || user == null || customer == null || weForm == null || weFormOperRecord == null
                || StringUtils.isAnyBlank(user.getUserId(), customer.getExternalUserid(), customer.getName())) {
            return;
        }
        Time now = new Time(System.currentTimeMillis());
        String content = subType.getDesc()
                .replace(GenConstants.CUSTOMER, customer.getName())
                .replace(GenConstants.FORM_NAME, weForm.getFormName());

        WeCustomerTrajectory trajectory = WeCustomerTrajectory.builder()
                .userId(user.getUserId())
                .externalUserid(customer.getExternalUserid())
                .trajectoryType(CustomerTrajectoryEnums.Type.ACTIVITY.getDesc())
                .content(content)
                .createDate(eventTime)
                .detail(user.getAvatarMediaid())
                .subType(subType.getType())
                .corpId(weForm.getCorpId())
                .startTime(now)
                .build();
        this.saveOrUpdate(trajectory);
    }

    private void setSopAttachment(List<WeOperationsCenterSopTaskEntity> taskEntityList, WeCustomerTrajectory weCustomerTrajectory) {
        List<SopAttachmentVO> materialList = new ArrayList<>();
        for (WeOperationsCenterSopTaskEntity taskEntity : taskEntityList) {
            SopAttachmentVO sopAttachmentVO = new SopAttachmentVO();
            BeanUtils.copyProperties(taskEntity, sopAttachmentVO);
            materialList.add(sopAttachmentVO);
        }
        weCustomerTrajectory.setMaterialList(materialList);
    }

}
