package com.easywecom.wecom.service.impl.moment;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.GroupMessageType;
import com.easywecom.common.enums.MediaType;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.moment.*;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeMomentClient;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easywecom.wecom.domain.dto.message.ImageMessageDTO;
import com.easywecom.wecom.domain.dto.message.LinkMessageDTO;
import com.easywecom.wecom.domain.dto.moment.*;
import com.easywecom.wecom.domain.entity.moment.*;
import com.easywecom.wecom.domain.vo.WeUserVO;
import com.easywecom.wecom.domain.vo.moment.*;
import com.easywecom.wecom.domain.vo.sop.DepartmentVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.moment.WeMomentTaskMapper;
import com.easywecom.wecom.mapper.moment.WeMomentUserCustomerRelMapper;
import com.easywecom.wecom.service.*;
import com.easywecom.wecom.service.moment.WeMomentDetailRelService;
import com.easywecom.wecom.service.moment.WeMomentTaskResultService;
import com.easywecom.wecom.service.moment.WeMomentTaskService;
import com.easywecom.wecom.service.moment.WeMomentUserCustomerRelService;
import com.easywecom.wecom.utils.ApplicationMessageUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名： 朋友圈任务信息表接口
 *
 * @author 佚名
 * @date 2022/1/10 13:58
 */
@Service
@Slf4j
public class WeMomentTaskServiceImpl extends ServiceImpl<WeMomentTaskMapper, WeMomentTaskEntity> implements WeMomentTaskService {

    private final WeMomentClient momentClient;
    private final WeWordsDetailService weWordsDetailService;
    private final WeMomentTaskResultService weMomentTaskResultService;
    private final WeCustomerMessageService weCustomerMessageService;
    private final WeMomentDetailRelService weMomentDetailRelService;
    private final WeUserService weUserService;
    private final ApplicationMessageUtil applicationMessageUtil;
    private final WeMomentUserCustomerRelService weMomentUserCustomerRelService;
    private final WeCustomerService weCustomerService;
    private final WeTagService weTagService;
    private final WeMomentUserCustomerRelMapper weMomentUserCustomerRelMapper;
    private final WeDepartmentService weDepartmentService;

    @Autowired
    public WeMomentTaskServiceImpl(WeMomentClient momentClient, WeWordsDetailService weWordsDetailService, WeMomentTaskResultService weMomentTaskResultService, WeCustomerMessageService weCustomerMessageService, WeMomentDetailRelService weMomentDetailRelService, WeUserService weUserService, ApplicationMessageUtil applicationMessageUtil, WeMomentUserCustomerRelService weMomentUserCustomerRelService, WeCustomerService weCustomerService, WeTagService weTagService, WeMomentUserCustomerRelMapper weMomentUserCustomerRelMapper, WeDepartmentService weDepartmentService) {
        this.momentClient = momentClient;
        this.weWordsDetailService = weWordsDetailService;
        this.weMomentTaskResultService = weMomentTaskResultService;
        this.weCustomerMessageService = weCustomerMessageService;
        this.weMomentDetailRelService = weMomentDetailRelService;
        this.weUserService = weUserService;
        this.applicationMessageUtil = applicationMessageUtil;
        this.weMomentUserCustomerRelService = weMomentUserCustomerRelService;
        this.weCustomerService = weCustomerService;
        this.weTagService = weTagService;
        this.weMomentUserCustomerRelMapper = weMomentUserCustomerRelMapper;
        this.weDepartmentService = weDepartmentService;
    }

    /**
     * 创建朋友圈任务
     *
     * @param createMomentTaskDTO 实体
     * @param loginUser           当前登录用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMomentTask(CreateMomentTaskDTO createMomentTaskDTO, LoginUser loginUser) {
        //校验参数
        checkCreateMomentTaskParam(createMomentTaskDTO);
        //权限下的部门
        List<String> departmentScope = getDepartmentScope(loginUser, createMomentTaskDTO.getDepartments());
        //权限下的员工
        Set<String> userScope = getUserScope(loginUser, createMomentTaskDTO.getUsers(), departmentScope);

        WeMomentTaskEntity weMomentTaskEntity = new WeMomentTaskEntity(createMomentTaskDTO, new ArrayList<>(userScope), departmentScope, loginUser.getUserId());
        if (CollectionUtils.isNotEmpty(createMomentTaskDTO.getUsers()) || CollectionUtils.isNotEmpty(createMomentTaskDTO.getDepartments())) {
            weMomentTaskEntity.setSelectUser(MomentSelectUserEnum.SELECT_USER.getType());
        }
        //先上传一遍素材判断是否合法
        if (CollectionUtils.isNotEmpty(createMomentTaskDTO.getAttachments())) {
            buildMomentAttachment(createMomentTaskDTO.getAttachments(), weMomentTaskEntity.getCorpId());
        }
        //保存任务附件
        saveMomentDetail(createMomentTaskDTO.getCorpId(), createMomentTaskDTO.getAttachments());
        if (createMomentTaskDTO.getText() != null && StringUtils.isNotBlank(createMomentTaskDTO.getText().getContent())) {
            weMomentTaskEntity.setContent(createMomentTaskDTO.getText().getContent());
        }
        //保存we_moment_detail_rel附件关联表
        saveMomentDetailRel(createMomentTaskDTO.getAttachments(), weMomentTaskEntity.getId());
        String users = CollectionUtils.isNotEmpty(createMomentTaskDTO.getUsers()) ? String.join(StrUtil.COMMA,createMomentTaskDTO.getUsers()) : StringUtils.EMPTY;
        String departments = CollectionUtils.isNotEmpty(createMomentTaskDTO.getDepartments()) ? String.join(StrUtil.COMMA,createMomentTaskDTO.getDepartments()) : StringUtils.EMPTY;
        List<WeCustomer> weCustomers = weCustomerService.selectWeCustomerListNoRel(new WeCustomerPushMessageDTO(users, weMomentTaskEntity.getTags(), weMomentTaskEntity.getCorpId(), departments));
        //个人朋友无客户圈抛出异常
        if(MomentTypeEnum.PERSONAL_MOMENT.getType().equals(createMomentTaskDTO.getType())&& CollectionUtils.isEmpty(weCustomers)){
            throw new CustomException(ResultTip.TIP_MOMENT_CREATE_ERROR);
        }

        Set<String> userIdSet = weCustomers.stream().map(WeCustomer::getUserId).collect(Collectors.toSet());
        List<String> userScopeByDepartment = weUserService.listOfUserId(weMomentTaskEntity.getCorpId(),departmentScope.toArray(new String[]{}));
        if(CollectionUtils.isNotEmpty(userScopeByDepartment)){
            userScope.addAll(userScopeByDepartment);
        }
        List<String> userScopeList = new ArrayList<>(userScope);
        //保存执行结果
        saveTaskNotPublishResult(weMomentTaskEntity.getId(), userScopeList,userIdSet,createMomentTaskDTO.getType(),createMomentTaskDTO.getPushRange());
        //是否为定时任务
        if (MomentTaskTypeEnum.RIGHT_NOW.getType().equals(createMomentTaskDTO.getTaskType())){
            startCreatMoment(weMomentTaskEntity,createMomentTaskDTO.getAttachments());
        }else {
            weMomentTaskEntity.setStatus(MomentStatusEnum.NOT_START.getType());
            this.save(weMomentTaskEntity);
        }
    }

    /**
     * 查看登录用户是否有传入部门的使用权限，返回有使用权限的部门
     *
     * @param loginUser
     * @param departments
     * @return
     */
    private List<String> getDepartmentScope(LoginUser loginUser, List<String> departments) {
        if (ObjectUtil.isNull(loginUser)
                || org.apache.commons.lang3.StringUtils.isBlank(loginUser.getCorpId())
                || org.apache.commons.lang3.StringUtils.isBlank(loginUser.getDepartmentDataScope())) {
            return Collections.emptyList();
        }
        // 获取登录用户缓存中该用户的所有部门权限(格式：1,2,3)
        String dataScope = loginUser.getDepartmentDataScope();
        List<String> departmentScope = Arrays.asList(dataScope.split(StrUtil.COMMA));
        return CollectionUtils.isEmpty(departments)? Collections.emptyList() :departments.stream().filter(departmentScope::contains).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startCreatMoment(WeMomentTaskEntity weMomentTaskEntity,List<WeWordsDetailEntity> attachments){
        weMomentTaskEntity.setSendTime(DateUtils.getNowDate());
        //是否为企业朋友圈
        if (MomentTypeEnum.ENTERPRISE_MOMENT.getType().equals(weMomentTaskEntity.getType())) {
            //创建企业朋友圈任务
            addMomentTask(attachments,weMomentTaskEntity);
            //更新创建状态
            updateTaskStatus(weMomentTaskEntity);
        } else {
            //个人朋友圈、发送应用消息、设置创建状态
            List<WeCustomer> weCustomers = weCustomerService.selectWeCustomerListNoRel(new WeCustomerPushMessageDTO(weMomentTaskEntity.getUsers(), weMomentTaskEntity.getTags(),weMomentTaskEntity.getCorpId(),weMomentTaskEntity.getDepartments()));
            //通过部门查询员工
            List<String> userIdsByDepartment = weUserService.listOfUserId(weMomentTaskEntity.getCorpId(),weMomentTaskEntity.getDepartments().split(StrUtil.COMMA));
            List<String> userIds = new ArrayList<>(Arrays.asList(weMomentTaskEntity.getUsers().split(StrUtil.COMMA)));
            if(CollectionUtils.isNotEmpty(userIdsByDepartment)){
                userIds.addAll(userIdsByDepartment);
            }
            if (CollectionUtils.isNotEmpty(weCustomers)){
                Set<String> userIdSet = weCustomers.stream().map(WeCustomer::getUserId).collect(Collectors.toSet());
                userIds = userIds.stream().filter(userIdSet::contains).collect(Collectors.toList());
            }
            applicationMessageUtil.sendAppMessage(userIds, weMomentTaskEntity.getCorpId(), WeConstans.PERSONAL_MOMENT_MSG, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, weMomentTaskEntity.getSendTime()), applicationMessageUtil.getMomentUrl(weMomentTaskEntity.getCorpId(), weMomentTaskEntity.getId()));
            weMomentTaskEntity.setStatus(MomentStatusEnum.FINISH.getType());
            //保存个人朋友圈客户员工关联表 we_moment_user_customer_rel
            List<WeMomentUserCustomerRelEntity> weMomentUserCustomerRelEntityList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(weCustomers)){
                weCustomers.forEach(weCustomer -> weMomentUserCustomerRelEntityList.add(new WeMomentUserCustomerRelEntity(weMomentTaskEntity.getId(),weCustomer.getExternalUserid(),weCustomer.getUserId())));
                weMomentUserCustomerRelService.saveBatch(weMomentUserCustomerRelEntityList);
            }
            //保存个人朋友圈任务
            this.saveOrUpdate(weMomentTaskEntity);
        }
    }

    @Override
    public List<WeMomentTaskEntity> listOfSettingTask(Date now) {
        return baseMapper.listOfSettingTask(now);
    }

    @Override
    public List<SearchMomentVO> listOfMomentTask(SearchMomentContentDTO searchMomentContentDTO, LoginUser loginUser) {
        //朋友圈内容
        List<WeUser> userInDataScope = weUserService.getUserInDataScope(loginUser);
        List<String> userIds = userInDataScope.stream().map(WeUser::getUserId).collect(Collectors.toList());
        if (loginUser.isSuperAdmin()){
            userIds.add("admin");
        }
        //权限下的任务
        if (searchMomentContentDTO.getPageNum() != null && searchMomentContentDTO.getPageSize() != null){
            PageHelper.startPage(searchMomentContentDTO.getPageNum(), searchMomentContentDTO.getPageSize());
        }
        List<SearchMomentVO> searchMomentVos = baseMapper.listOfMomentTask(userIds, loginUser.getCorpId(), searchMomentContentDTO.getContent(), searchMomentContentDTO.getEndTime(), searchMomentContentDTO.getBeginTime(), searchMomentContentDTO.getType());
        for (SearchMomentVO searchMomentVo : searchMomentVos) {
            MomentTotalVO total = getTotal(searchMomentVo.getMomentTaskId());
            searchMomentVo.setPublishNum(total.getPublishNum());
            searchMomentVo.setNotPublishNum(total.getNotPublishNum());
            //所属标签信息
            if (StringUtils.isNotBlank(searchMomentVo.getTags())){
                List<WeTag> weTags = weTagService.listByIds(Arrays.asList(searchMomentVo.getTags().split(StrUtil.COMMA)));
                searchMomentVo.setTagList(weTags);
            }
            //所属员工信息
            if (MomentPushRangeEnum.SELECT_CUSTOMER.getType().equals(searchMomentVo.getPushRange())){

                LambdaQueryWrapper<WeUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WeUser::getCorpId,searchMomentContentDTO.getCorpId())
                        .in(WeUser::getUserId,new ArrayList<>(Arrays.asList(searchMomentVo.getUsers().split(StrUtil.COMMA))));
                List<WeUser> weUsers = weUserService.list(wrapper);

                List<SearchMomentVO.MomentUserVO> momentUserVoList = new ArrayList<>();
                for (WeUser weUser : weUsers) {
                    momentUserVoList.add(new SearchMomentVO.MomentUserVO(weUser));
                }
                searchMomentVo.setUserList(momentUserVoList);
            }
            //设置mediaType
            if (CollectionUtils.isNotEmpty(searchMomentVo.getWeWordsDetailList())){
                searchMomentVo.setMediaType(searchMomentVo.getWeWordsDetailList().get(0).getMediaType());
            }
        }
        return searchMomentVos;
    }


    @Override
    public List<MomentUserCustomerVO> listOfMomentPublishDetail(MomentUserCustomerDTO momentUserCustomerDTO) {
        momentUserCustomerDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return baseMapper.listOfMomentPublishDetail(momentUserCustomerDTO);
    }

    @Override
    public MomentTotalVO getTotal(Long momentTaskId) {
        return baseMapper.getTotal(momentTaskId);
    }

    @Override
    public SearchMomentVO getMomentTaskBasicInfo(Long momentTaskId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        SearchMomentVO momentTaskBasicInfo = baseMapper.getMomentTaskBasicInfo(momentTaskId,LoginTokenService.getLoginUser().getCorpId());
        //设置使用员工、部门
        if(StringUtils.isNotBlank(momentTaskBasicInfo.getUsers())){
            List<WeUserVO> weUsers = weUserService.listOfUser(corpId, Arrays.asList(momentTaskBasicInfo.getUsers().split(StrUtil.COMMA)));
            if(CollectionUtils.isNotEmpty(weUsers)){
                momentTaskBasicInfo.setUseUserList(weUsers);
            }
        }
        if(StringUtils.isNotBlank(momentTaskBasicInfo.getDepartments())){
            List<DepartmentVO> weDepartmentVO = weDepartmentService.getDeparmentDetailByIds(corpId, Arrays.asList(momentTaskBasicInfo.getDepartments().split(StrUtil.COMMA)));
            if (CollectionUtils.isNotEmpty(weDepartmentVO)) {
                momentTaskBasicInfo.setUseDepartmentList(weDepartmentVO);
            }
        }

        if (StringUtils.isNotBlank(momentTaskBasicInfo.getTags())){
            List<WeTag> weTags = weTagService.listByIds(Arrays.asList(momentTaskBasicInfo.getTags().split(StrUtil.COMMA)));
            momentTaskBasicInfo.setTagList(weTags);
        }
        //设置mediaType
        if (CollectionUtils.isNotEmpty(momentTaskBasicInfo.getWeWordsDetailList())){
            momentTaskBasicInfo.setMediaType(momentTaskBasicInfo.getWeWordsDetailList().get(0).getMediaType());
        }else{
            momentTaskBasicInfo.setMediaType(Integer.valueOf(MediaType.TEXT.getType()));
        }
        return momentTaskBasicInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMoment(Long momentTaskId) {
        //删除we_words_detail
        List<WeMomentDetailRelEntity> momentAttachment = weMomentDetailRelService.list(new LambdaQueryWrapper<WeMomentDetailRelEntity>().eq(WeMomentDetailRelEntity::getMomentTaskId, momentTaskId));
        if (CollectionUtils.isNotEmpty(momentAttachment)){
            weWordsDetailService.remove(new LambdaQueryWrapper<WeWordsDetailEntity>().in(WeWordsDetailEntity::getId,momentAttachment.stream().map(WeMomentDetailRelEntity::getDetailId).collect(Collectors.toList())));
        }
        //删除we_moment_detail_rel
        weMomentDetailRelService.remove(new LambdaQueryWrapper<WeMomentDetailRelEntity>().eq(WeMomentDetailRelEntity::getMomentTaskId, momentTaskId));
        //删除we_moment_task_result
        weMomentTaskResultService.remove(new LambdaQueryWrapper<WeMomentTaskResultEntity>().eq(WeMomentTaskResultEntity::getMomentTaskId, momentTaskId));
        //删除we_moment_user_customer_rel
        weMomentUserCustomerRelService.remove(new LambdaQueryWrapper<WeMomentUserCustomerRelEntity>().eq(WeMomentUserCustomerRelEntity::getMomentTaskId, momentTaskId));
        //删除朋友圈任务
        this.removeById(momentTaskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMoment(CreateMomentTaskDTO createMomentTaskDTO) {
        //判断是否定时且未发送
        WeMomentTaskEntity momentTaskEntity = this.getById(createMomentTaskDTO.getMomentTaskId());
        if (momentTaskEntity != null && MomentTaskTypeEnum.SETTING_TIME.getType().equals(momentTaskEntity.getTaskType()) && MomentStatusEnum.NOT_START.getType().equals(momentTaskEntity.getStatus())) {
            //删除
            deleteMoment(createMomentTaskDTO.getMomentTaskId());
            createMomentTaskDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
            //创建
            createMomentTask(createMomentTaskDTO,LoginTokenService.getLoginUser());
        } else {
            log.error("更新朋友圈失败，只有定时未发送的朋友圈可编辑");
            throw new CustomException(ResultTip.TIP_MOMENT_UPDATE_ERROR);
        }
    }

    @Override
    public void refreshMoment(Long momentTaskId) {
        WeMomentTaskEntity momentTaskEntity = this.getById(momentTaskId);
        //48小时内 已创建的企业朋友圈
        if (momentTaskEntity != null && StringUtils.isNotBlank(momentTaskEntity.getMomentId()) &&  DateUtils.dateSubHour(new Date(),48).getTime() <= momentTaskEntity.getSendTime().getTime() ){
            updatePublishStatus(momentTaskEntity.getMomentId(),momentTaskEntity.getId(),momentTaskEntity.getCorpId());
        }else {
            assert momentTaskEntity != null;
            if (DateUtils.dateSubHour(new Date(),48).getTime() > momentTaskEntity.getSendTime().getTime() ){
                weMomentTaskResultService.update(new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                        .eq(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.NOT_PUBLISH.getType())
                        .in(WeMomentTaskResultEntity::getMomentTaskId, momentTaskId)
                        .set(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.EXPIRE.getType()));
                this.update(new LambdaUpdateWrapper<WeMomentTaskEntity>()
                        .in(WeMomentTaskEntity::getId, momentTaskId)
                        .set(WeMomentTaskEntity::getUpdateTime, new Date()));
            }
        }
    }

    /**
     * 更新发布状态
     *
     * @param momentId 朋友圈id
     * @param taskId   任务id
     * @param corpId   企业id
     */
    @Override
    public void updatePublishStatus(String momentId, Long taskId, String corpId) {
        if (StringUtils.isAnyBlank(momentId,corpId)|| taskId == null){
            log.error("更新发布状态异常入参为空 momentId:{},taskId:{},corpId:{}",momentId,taskId,corpId);
            return;
        }
        MomentTaskVO momentTaskVo = new MomentTaskVO();
        List<String> taskUserList = new ArrayList<>();
        do {
            momentTaskVo = momentClient.getMomentTask(new MomentTaskDTO(momentId, momentTaskVo.getNext_cursor()), corpId);
            //异常停止
            if (!Integer.valueOf(0).equals(momentTaskVo.getErrcode())) {
                log.error("获取客户朋友圈执行详情异常 errorCode:{},errorMsg:{}", momentTaskVo.getErrcode(), momentTaskVo.getErrmsg());
                return;
            }
            List<MomentTask> taskPublishList = momentTaskVo.getTask_list();
            for (MomentTask momentTask : taskPublishList) {
                taskUserList.add(momentTask.getUserid());
                //更新执行状态
                LambdaUpdateWrapper<WeMomentTaskResultEntity> resultEntityLambdaUpdateWrapper = new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                        .eq(WeMomentTaskResultEntity::getUserId, momentTask.getUserid())
                        .eq(WeMomentTaskResultEntity::getMomentTaskId, taskId)
                        .set(WeMomentTaskResultEntity::getPublishStatus, momentTask.getPublish_status());
                //设置发布时间
                if (MomentPublishStatusEnum.PUBLISH.getType().equals(momentTask.getPublish_status())){
                    resultEntityLambdaUpdateWrapper.set(WeMomentTaskResultEntity::getPublishTime,new Date());
                }
                weMomentTaskResultService.update(resultEntityLambdaUpdateWrapper);
                log.info("朋友圈执行状态已更新 corpId:{},userId:{},taskId:{}", corpId, momentTask.getUserid(), taskId);
                this.update(new LambdaUpdateWrapper<WeMomentTaskEntity>().eq(WeMomentTaskEntity::getId, taskId).set(WeMomentTaskEntity::getUpdateTime, new Date()));
                //朋友圈触达客户(已执行产生触达)
                if (MomentPublishStatusEnum.PUBLISH.getType().equals(momentTask.getPublish_status())) {
                    saveMomentCustomer(momentId, momentTask.getUserid(), corpId, taskId);
                }
            }
        } while (StringUtils.isNotBlank(momentTaskVo.getNext_cursor()));
        //不在任务列表中的待执行员工改为不可发布
        statusToNoAuth(taskUserList,taskId);
    }

    private void statusToNoAuth(List<String> taskUserList, Long taskId) {
        LambdaUpdateWrapper<WeMomentTaskResultEntity> updateStatusWrapper = new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                .eq(WeMomentTaskResultEntity::getMomentTaskId, taskId)
                .eq(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.NOT_PUBLISH.getType())
                .set(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.NO_AUTHORITY.getType())
                .set(WeMomentTaskResultEntity::getRemark,WeConstans.MOMENT_NO_CUSTOMER);
        if (CollectionUtils.isNotEmpty(taskUserList)){
            updateStatusWrapper.notIn(WeMomentTaskResultEntity::getUserId, taskUserList);
        }
        //不在任务列表中的待执行员工改为不可发布
        weMomentTaskResultService.update(updateStatusWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserMoment(Long momentTaskId, String userId) {
        if (momentTaskId == null || StringUtils.isBlank(userId)){
            log.error("更新员工个人朋友圈执行情况失败，传入参数为空：momentTaskId :{},user:{}",momentTaskId,userId);
            return;
        }
        WeMomentTaskEntity momentTaskEntity = this.getById(momentTaskId);
        if (momentTaskEntity != null && MomentTypeEnum.PERSONAL_MOMENT.getType().equals(momentTaskEntity.getType())){
            try {
                //更新执行结果
                weMomentTaskResultService.update(new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                        .eq(WeMomentTaskResultEntity::getMomentTaskId, momentTaskId)
                        .eq(WeMomentTaskResultEntity::getUserId, userId)
                        .set(WeMomentTaskResultEntity::getPublishTime,new Date())
                        .set(WeMomentTaskResultEntity::getPublishStatus,MomentPublishStatusEnum.PUBLISH.getType()));
                //更新时间修改
                this.update(new LambdaUpdateWrapper<WeMomentTaskEntity>().eq(WeMomentTaskEntity::getId,momentTaskId).set(WeMomentTaskEntity::getUpdateTime,new Date()));
            }catch (Exception e){
                log.error("更新员工个人朋友圈执行情况失败,momentTaskId:{},userId:{},e:{}",momentTaskId,userId, ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public void sendToUser(List<String> userIds, Integer type, String sendTime, Long momentTaskId) {
        if (CollectionUtils.isEmpty(userIds)){
            log.info("userIds 为空发送朋友圈提醒信息失败,momentTaskId：{}",momentTaskId);
            return;
        }
        //判断员工是否可以发送 只有待发布状态可发送
        List<WeMomentTaskResultEntity> noPublishUsers = weMomentTaskResultService.list(new LambdaQueryWrapper<WeMomentTaskResultEntity>()
                .eq(WeMomentTaskResultEntity::getMomentTaskId, momentTaskId)
                .eq(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.NOT_PUBLISH.getType()));
        if (CollectionUtils.isEmpty(noPublishUsers)){
            log.info("未发送朋友圈员工为空发送朋友圈提醒信息失败,momentTaskId：{}",momentTaskId);
            return;
        }
        List<String> noPublishUserIdList = noPublishUsers.stream().map(WeMomentTaskResultEntity::getUserId).collect(Collectors.toList());
        //求两个集合交集
        List<String> sendList = userIds.stream().filter(noPublishUserIdList::contains).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sendList)){
            log.info("可发送员工为空发送朋友圈提醒信息失败,momentTaskId：{}",momentTaskId);
            return;
        }
        log.info("发送员工朋友圈提醒信息,momentTaskId：{}",momentTaskId);
        if (MomentTypeEnum.ENTERPRISE_MOMENT.getType().equals(type)){
            applicationMessageUtil.sendAppMessage(sendList,LoginTokenService.getLoginUser().getCorpId(), WeConstans.ENTERPRISE_MOMENT_USER_MSG,sendTime);
        }else {
            applicationMessageUtil.sendAppMessage(sendList,LoginTokenService.getLoginUser().getCorpId(), WeConstans.PERSONAL_MOMENT_USER_MSG,sendTime,applicationMessageUtil.getMomentUrl(LoginTokenService.getLoginUser().getCorpId(),momentTaskId));
        }
    }

    @Override
    public SearchMomentVO getMomentTask(Long momentTaskId, String userId) {
        if(momentTaskId == null || StringUtils.isBlank(userId)){
            log.info("朋友圈h5页面参数为空，返回空数据 ");
            return new SearchMomentVO();
        }
        SearchMomentVO searchMomentVO = baseMapper.getMomentTaskByUserIdMomentId(momentTaskId, userId);
        if (searchMomentVO == null){
            return new SearchMomentVO(MomentStatusEnum.DEL.getType());
        }
        if (StringUtils.isNotBlank(searchMomentVO.getTags())){
            List<WeTag> weTags = weTagService.listByIds(Arrays.asList(searchMomentVO.getTags().split(StrUtil.COMMA)));
            searchMomentVO.setTagList(weTags);
        }
        //设置mediaType
        if (CollectionUtils.isNotEmpty(searchMomentVO.getWeWordsDetailList())){
            searchMomentVO.setMediaType(searchMomentVO.getWeWordsDetailList().get(0).getMediaType());
            //设置mediaid
            for (WeWordsDetailEntity detailEntity : searchMomentVO.getWeWordsDetailList()) {
                detailEntity.initMediaId(searchMomentVO.getCorpId());
            }
        }
        return searchMomentVO;
    }

    /**
     * 保存触达对象
     *
     * @param momentId 朋友圈id
     * @param userId   员工id
     * @param corpId   企业id
     */
    private void saveMomentCustomer(String momentId, String userId, String corpId, Long taskId) {
        if (StringUtils.isAnyBlank(momentId,userId,corpId)|| taskId == null){
            log.error("保存触达客户失败，传入参数为空：momentId:{} userId:{} ,corpId:{},taskId:{}",momentId,userId,corpId,taskId);
            return;
        }
        MomentCustomerVO momentCustomerVO = new MomentCustomerVO();
        do {
            momentCustomerVO = momentClient.getMomentCustomerList(new MomentCustomerDTO(momentId, userId, momentCustomerVO.getNext_cursor()), corpId);
            if (Integer.valueOf(0).equals(momentCustomerVO.getErrcode())) {
                List<WeMomentUserCustomerRelEntity> userCustomerRelList = new ArrayList<>();
                //插入we_moment_user_customer_rel
                for (MomentCustomer momentCustomer : momentCustomerVO.getCustomer_list()) {
                    WeMomentUserCustomerRelEntity momentUserCustomerRelEntity = new WeMomentUserCustomerRelEntity();
                    momentUserCustomerRelEntity.setMomentTaskId(taskId);
                    momentUserCustomerRelEntity.setUserId(momentCustomer.getUserid());
                    momentUserCustomerRelEntity.setExternalUserid(momentCustomer.getExternal_userid());
                    userCustomerRelList.add(momentUserCustomerRelEntity);
                }
                weMomentUserCustomerRelMapper.saveIgnoreDuplicateKey(userCustomerRelList);
            } else {
                log.error("定时任务获取客户朋友圈触达对象异常 errorCode:{},errorMsg:{}", momentCustomerVO.getErrcode(), momentCustomerVO.getErrmsg());
            }
        } while (StringUtils.isNotBlank(momentCustomerVO.getNext_cursor()));
    }

    private Set<String> getUserScope(LoginUser loginUser, List<String> users, List<String> departmentScope){
        List<WeUser> userInDataScope = weUserService.getUserInDataScope(loginUser);
        Set<String> userIds = userInDataScope.stream().map(WeUser::getUserId).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(users)){
            if(CollectionUtils.isEmpty(departmentScope)){
                return userIds;
            }
            List<String> userIdsFromDepartment = weUserService.listOfUserId(loginUser.getCorpId(), departmentScope.toArray(new String[]{}));
            if(CollectionUtils.isEmpty(userIdsFromDepartment)){
                return userIds;
            }else{
                return new HashSet<>();
            }
        }else{
            return users.stream().filter(userIds::contains).collect(Collectors.toSet());
        }
    }

    /**
     * 需要保证详情能查询数据，插入成员待发布结果
     * @param taskId    任务id
     * @param userIds   待发送员工
     * @param userIdSet 客户所属员工
     * @param type
     * @param pushRange
     */
    private void saveTaskNotPublishResult(Long taskId, List<String> userIds, Set<String> userIdSet, Integer type, Integer pushRange) {
        if (CollectionUtils.isEmpty(userIds) || taskId == null) {
            return;
        }
        //个人朋友圈发送员工限制
        if(MomentTypeEnum.PERSONAL_MOMENT.getType().equals(type) && MomentPushRangeEnum.SELECT_CUSTOMER.getType().equals(pushRange)){
            userIds = userIds.stream().filter(userIdSet::contains).collect(Collectors.toList());
        }
        List<WeMomentTaskResultEntity> resultEntityList = new ArrayList<>();
        //构造执行详情数据
        for (String userId : userIds) {
            WeMomentTaskResultEntity resultEntity = new WeMomentTaskResultEntity();
            resultEntity.setUserId(userId);
            resultEntity.setMomentTaskId(taskId);
            if (CollectionUtils.isNotEmpty(userIdSet) && userIdSet.contains(userId)){
                resultEntity.setPublishStatus(MomentPublishStatusEnum.NOT_PUBLISH.getType());
            }else {
                resultEntity.setPublishStatus(MomentPublishStatusEnum.NO_AUTHORITY.getType());
                resultEntity.setRemark(WeConstans.MOMENT_NO_CUSTOMER);
            }
            resultEntityList.add(resultEntity);
        }
        //构造执行详情数据
        weMomentTaskResultService.saveBatch(resultEntityList);
    }

    /**
     * 创建企业朋友圈任务
     *
     * @param weMomentTaskEntity  任务实体
     */
    private void addMomentTask(List<WeWordsDetailEntity> attachmentDetails, WeMomentTaskEntity weMomentTaskEntity) {
        //构造创建企业朋友圈参数
        Set<String> users = StringUtils.isNotBlank(weMomentTaskEntity.getUsers())?new HashSet<>(Arrays.asList(weMomentTaskEntity.getUsers().split(StrUtil.COMMA))) : new HashSet<>();
        List<Integer> departments = StringUtils.isNotBlank(weMomentTaskEntity.getDepartments())?
                Arrays.asList(weMomentTaskEntity.getDepartments().split(StrUtil.COMMA)).stream().map(Integer::valueOf).collect(Collectors.toList()) :
                Lists.newArrayList();
        List<String> tags = StringUtils.isNotBlank(weMomentTaskEntity.getTags())?Arrays.asList(weMomentTaskEntity.getTags().split(StrUtil.COMMA)):new ArrayList<>();
        final List<String> userIds = weUserService.listOfUserId(weMomentTaskEntity.getCorpId(), StringUtils.join(departments,StrUtil.COMMA));
        if(CollectionUtils.isNotEmpty(userIds)){
            users.addAll(userIds);
        }
        List<String> userIdList = new ArrayList<>(users);
        AddMomentTaskDTO addMomentTaskDTO = new AddMomentTaskDTO(userIdList, departments, tags, weMomentTaskEntity.getContent());
        //临时素材id
        if (CollectionUtils.isNotEmpty(attachmentDetails)){
            List<MomentAttachment> attachments = buildMomentAttachment(attachmentDetails, weMomentTaskEntity.getCorpId());
            addMomentTaskDTO.setAttachments(attachments);
        }
        //调用企业创建接口
        AddMomentTaskVO addMomentTaskVO = momentClient.addMomentTask(addMomentTaskDTO, weMomentTaskEntity.getCorpId());
        //保存jobId、开始创建状态
        if (Integer.valueOf(0).equals(addMomentTaskVO.getErrcode())) {
            weMomentTaskEntity.setJobId(addMomentTaskVO.getJobid());
            weMomentTaskEntity.setStatus(MomentStatusEnum.START.getType());
        }
        //保存本地朋友圈任务
        this.saveOrUpdate(weMomentTaskEntity);
    }

    /**
     * 更新朋友圈任务创建状态
     *
     * @param weMomentTaskEntity 任务实体
     */
    @Override
    public void updateTaskStatus(WeMomentTaskEntity weMomentTaskEntity) {
        //更新创建状态
        MomentTaskResultVO momentTaskResult = momentClient.getMomentTaskResult(weMomentTaskEntity.getJobId(), weMomentTaskEntity.getCorpId());
        //企微接口调用失败
        if (!WeConstans.WE_SUCCESS_CODE.equals(momentTaskResult.getErrcode())) {
           return;
        }
        weMomentTaskEntity.setStatus(momentTaskResult.getStatus());
        if (MomentStatusEnum.FINISH.getType().equals(momentTaskResult.getStatus())) {
            MomentTaskResultVO.AddMomentResult result = momentTaskResult.getResult();
            String momentId = result.getMoment_id();
            //保存momentId
            weMomentTaskEntity.setMomentId(momentId);
            //不合法的执行者处理
            if (result.getInvalid_sender_list() != null && CollectionUtils.isNotEmpty(result.getInvalid_sender_list().getUser_list())) {
                //任务执行状态置为不可发布
                weMomentTaskResultService.update(new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                        .eq(WeMomentTaskResultEntity::getMomentTaskId,weMomentTaskEntity.getId())
                        .in(WeMomentTaskResultEntity::getUserId,result.getInvalid_sender_list().getUser_list())
                        .set(WeMomentTaskResultEntity::getPublishStatus,MomentPublishStatusEnum.NO_AUTHORITY.getType())
                        .set(WeMomentTaskResultEntity::getRemark,"该员工未激活或不在应用的可见范围内"));
            }
        }
        this.saveOrUpdate(weMomentTaskEntity);
    }

    /**
     * 查询未发布的朋友圈任务
     * @return {@link List<WeMomentTaskEntity>}
     */
    @Override
    public List<WeMomentTaskEntity> listOfNotPublish(Date subDay,Boolean isExpire) {
        return baseMapper.listOfNotPublish(subDay,isExpire);
    }

    /**
     * 临时素材id设置，话术附件类型 -> client接口所需attachment
     *
     * @param detailEntities 话术附件
     * @param corpId         企业id
     * @return
     */
    private List<MomentAttachment> buildMomentAttachment(List<WeWordsDetailEntity> detailEntities, String corpId) {
        List<MomentAttachment> attachments = new ArrayList<>();
        for (WeWordsDetailEntity detailEntity : detailEntities) {
            //图片
            if (GroupMessageType.IMAGE.getType().equals(detailEntity.getMediaType().toString())) {
                String mediaId = weCustomerMessageService.buildMediaId(detailEntity.getUrl(), GroupMessageType.IMAGE.getMessageType(), 1, detailEntity.getTitle(), corpId);
                MomentAttachment attachment = new MomentAttachment( new ImageMessageDTO(mediaId,detailEntity.getTitle()));
                attachments.add(attachment);
            }
            //链接
            if (GroupMessageType.LINK.getType().equals(detailEntity.getMediaType().toString())) {
                String mediaId = weCustomerMessageService.buildMediaId(detailEntity.getCoverUrl(), GroupMessageType.IMAGE.getMessageType(), 1, detailEntity.getTitle(), corpId);
                MomentAttachment attachment = new MomentAttachment(new LinkMessageDTO(mediaId,detailEntity.getTitle(),detailEntity.getUrl(),detailEntity.getContent()));
                attachments.add(attachment);
            }
            //视频
            if (GroupMessageType.VIDEO.getType().equals(detailEntity.getMediaType().toString())) {
                MomentAttachment attachment = MomentAttachment.buildMomentVideo(detailEntity, corpId);
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    /**
     * 保存we_moment_detail_rel附件关联表
     *
     * @param attachments  附件
     * @param momentTaskId 朋友圈任务id
     */
    private void saveMomentDetailRel(List<WeWordsDetailEntity> attachments, Long momentTaskId) {
        //保存we_moment_detail_rel附件关联表
        if (CollectionUtils.isNotEmpty(attachments)) {
            List<WeMomentDetailRelEntity> momentDetailRelEntities = new ArrayList<>();
            for (WeWordsDetailEntity attachment : attachments) {
                WeMomentDetailRelEntity weMomentDetailRelEntity = new WeMomentDetailRelEntity();
                weMomentDetailRelEntity.setDetailId(attachment.getId());
                weMomentDetailRelEntity.setMomentTaskId(momentTaskId);
                momentDetailRelEntities.add(weMomentDetailRelEntity);
            }
            weMomentDetailRelService.saveBatch(momentDetailRelEntities);
        }
    }

    /**
     * 保存任务附件
     *
     * @param corpId 企业id
     * @param attachments 附件
     */
    private void saveMomentDetail(String corpId, List<WeWordsDetailEntity> attachments) {
        //保存任务附件
        weWordsDetailService.saveOrUpdate(attachments,Boolean.FALSE,corpId);
    }

    /**
     * 校验参数
     *
     * @param createMomentTaskDTO 传参
     */
    private void checkCreateMomentTaskParam(CreateMomentTaskDTO createMomentTaskDTO) {
        Map<String, Integer> typeNumMap = new HashMap<>();
        //校验corpId
        StringUtils.checkCorpId(createMomentTaskDTO.getCorpId());
        //发布时间校验定时发送不能为空
        checkSendTime(createMomentTaskDTO.getTaskType(), createMomentTaskDTO.getSendTime(), createMomentTaskDTO.getCorpId());

        //校验附件类型只允许9图片、1链接、1视频
        List<WeWordsDetailEntity> attachments = createMomentTaskDTO.getAttachments();
        //校验内容 文本和附件不能同时为空
        checkMomentContent(attachments, createMomentTaskDTO.getText().getContent(), createMomentTaskDTO.getCorpId());
        if (CollectionUtils.isEmpty(attachments)) {
            return;
        }
        //附件校验
        for (WeWordsDetailEntity attachment : attachments) {
            if (GroupMessageType.isValidType(attachment.getMediaType().toString(), GroupMessageType.IMAGE, GroupMessageType.LINK, GroupMessageType.VIDEO)) {
                //保存附件数量
                if (typeNumMap.containsKey(attachment.getMediaType().toString())) {
                    Integer typeNum = typeNumMap.get(attachment.getMediaType().toString());
                    typeNumMap.put(attachment.getMediaType().toString(), ++typeNum);
                } else {
                    typeNumMap.put(attachment.getMediaType().toString(), 1);
                }
            } else {
                log.error("创建朋友圈任务失败，附件类型不合法 corpId:{}", createMomentTaskDTO.getCorpId());
                throw new CustomException(ResultTip.TIP_MOMENT_ATTACHMENT_TYPE_ERROR);
            }
        }
        //三种类型只能选择一个
        if (typeNumMap.keySet().size() > 1) {
            throw new CustomException(ResultTip.TIP_MOMENT_ATTACHMENT_TYPE_ERROR);
        }
        //校验附件数量
        checkAttachmentNum(typeNumMap, createMomentTaskDTO.getCorpId());
    }

    /**
     * 发布时间校验定时发送不能为空
     *
     * @param taskType 任务类型
     * @param sendTime 发送时间
     * @param corpId   企业id
     */
    private void checkSendTime(Integer taskType, Date sendTime, String corpId) {
        //发布时间校验 定时发送时 时间不能为空
        if (Integer.valueOf(1).equals(taskType) && sendTime == null) {
            log.error("创建朋友圈任务失败，定时发布时间不能为空 corpId:{}", corpId);
            throw new CustomException(ResultTip.TIP_MOMENT_ATTACHMENT_SEND_TIME_ERROR);
        }
    }

    /**
     * 校验内容 文本和附件不能同时为空
     *
     * @param attachments 附件
     * @param content     文本
     */
    private void checkMomentContent(List<WeWordsDetailEntity> attachments, String content, String corpId) {
        //校验内容 文本和附件不能同时为空
        if (CollectionUtils.isEmpty(attachments) && StringUtils.isBlank(content)) {
            log.error("创建朋友圈任务失败，发布内容不能为空 corpId:{}", corpId);
            throw new CustomException(ResultTip.TIP_MOMENT_ATTACHMENT_CONTENT_ERROR);
        }
    }

    /**
     * 校验附件数量 9图片、1链接、1视频
     *
     * @param typeNumMap 数量map
     * @param corpId     企业id
     */
    private void checkAttachmentNum(Map<String, Integer> typeNumMap, String corpId) {
        Integer imgNum = Optional.ofNullable(typeNumMap.get(GroupMessageType.IMAGE.getType())).orElse(0);
        Integer linkNum = Optional.ofNullable(typeNumMap.get(GroupMessageType.LINK.getType())).orElse(0);
        Integer videoNum = Optional.ofNullable(typeNumMap.get(GroupMessageType.VIDEO.getType())).orElse(0);
        //校验附件数量
        if (imgNum > 9 || linkNum > 1 || videoNum > 1) {
            log.error("创建朋友圈任务失败，附件数量不合法 corpId:{}", corpId);
            throw new CustomException(ResultTip.TIP_MOMENT_ATTACHMENT_NUM_ERROR);
        }
    }
}