package com.easyink.wecom.service.impl.moment;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.enums.GroupMessageType;
import com.easyink.common.enums.MediaType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.moment.*;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.client.WeMomentClient;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.dto.message.ImageMessageDTO;
import com.easyink.wecom.domain.dto.message.LinkMessageDTO;
import com.easyink.wecom.domain.dto.moment.*;
import com.easyink.wecom.domain.entity.moment.*;
import com.easyink.wecom.domain.model.customer.CustomerUserNameModel;
import com.easyink.wecom.domain.model.moment.MomentResultFilterModel;
import com.easyink.wecom.domain.model.user.UserNameHeadImgModel;
import com.easyink.wecom.domain.query.moment.MomentDetailQueryContext;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.domain.vo.moment.*;
import com.easyink.wecom.domain.vo.sop.DepartmentVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.mapper.moment.WeMomentDetailRelMapper;
import com.easyink.wecom.mapper.moment.WeMomentTaskMapper;
import com.easyink.wecom.mapper.moment.WeMomentTaskResultMapper;
import com.easyink.wecom.mapper.moment.WeMomentUserCustomerRelMapper;
import com.easyink.wecom.publishevent.SaveMomentCustomerRefEvent;
import com.easyink.wecom.publishevent.SendAppMessageEvent;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.moment.WeMomentDetailRelService;
import com.easyink.wecom.service.moment.WeMomentTaskResultService;
import com.easyink.wecom.service.moment.WeMomentTaskService;
import com.easyink.wecom.service.moment.WeMomentUserCustomerRelService;
import com.easyink.wecom.utils.ApplicationMessageUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 类名： 朋友圈任务信息表接口
 *
 * @author 佚名
 * @date 2022/1/10 13:58
 */
@Service
@Slf4j
@AllArgsConstructor
public class WeMomentTaskServiceImpl extends ServiceImpl<WeMomentTaskMapper, WeMomentTaskEntity> implements WeMomentTaskService {

    private final WeMomentClient momentClient;
    private final WeWordsDetailService weWordsDetailService;
    private final WeMomentDetailRelMapper weMomentDetailRelMapper;
    private final WeMomentTaskResultService weMomentTaskResultService;
    private final WeMomentTaskResultMapper weMomentTaskResultMapper;
    private final WeCustomerMessageService weCustomerMessageService;
    private final WeMomentDetailRelService weMomentDetailRelService;
    private final WeUserService weUserService;
    private final ApplicationMessageUtil applicationMessageUtil;
    private final WeMomentUserCustomerRelService weMomentUserCustomerRelService;
    private final WeTagService weTagService;
    private final WeMomentUserCustomerRelMapper weMomentUserCustomerRelMapper;
    private final WeDepartmentService weDepartmentService;
    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    private final WeUserMapper weUserMapper;
    private final WeCustomerService weCustomerService;

    /**
     * 创建朋友圈任务
     *
     * @param createMomentTaskDTO 实体
     * @param loginUser           当前登录用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMomentTask(CreateMomentTaskDTO createMomentTaskDTO, LoginUser loginUser) {
        long beginTime = System.currentTimeMillis();
        //校验参数
        checkCreateMomentTaskParam(createMomentTaskDTO);
        WeMomentTaskEntity weMomentTaskEntity = buildMomentTask(loginUser, createMomentTaskDTO);
        // 上传并保存任务附件
        uploadAndSaveAttachment(createMomentTaskDTO, weMomentTaskEntity);
        // 根据员工和部门查询员工id列表
        log.info("根据权限下的员工和部门查询需要发送的的员工id列表");
        List<String> candidateUserIdList = weCustomerService.getUserIdList(weMomentTaskEntity.getUsers(), weMomentTaskEntity.getDepartments(), createMomentTaskDTO.getCorpId());
        log.info("根据权限下的员工和部门查询需要发送的的员工id列表： {}", candidateUserIdList.size());
        // 查询客户列表
        log.info("查询过滤员工");
        List<String> existDbUserId = weCustomerService.listExistDbUserIdByTagAndUserIdList(candidateUserIdList, weMomentTaskEntity.getTags(), weMomentTaskEntity.getCorpId());
        log.info("查询过滤员工： {}", existDbUserId.size());
        //朋友圈无客户抛出异常
        if (CollectionUtils.isEmpty(existDbUserId)) {
            throw new CustomException(ResultTip.TIP_MOMENT_CREATE_ERROR);
        }

        //保存执行结果
        saveTaskNotPublishResult(weMomentTaskEntity.getId(), candidateUserIdList, new HashSet<>(existDbUserId), createMomentTaskDTO.getType(), createMomentTaskDTO.getPushRange());
        Runnable saveCustomerRefInvoke = () -> {
            SpringUtil.getApplicationContext().publishEvent(new SaveMomentCustomerRefEvent(
                    candidateUserIdList,
                    weMomentTaskEntity.getTags(),
                    weMomentTaskEntity.getCorpId(),
                    weMomentTaskEntity.getId()
            ));
        };


        if (MomentTaskTypeEnum.RIGHT_NOW.getType().equals(createMomentTaskDTO.getTaskType())) {
            // 立即发送
            weMomentTaskEntity.setSendTime(DateUtils.getNowDate());
            if (MomentTypeEnum.ENTERPRISE_MOMENT.getType().equals(weMomentTaskEntity.getType())) {
                // 企业
                //创建企业朋友圈任务
                addMomentTask(createMomentTaskDTO.getAttachments(), weMomentTaskEntity, () -> new ArrayList<>(candidateUserIdList));
                //更新创建状态
                updateTaskStatus(weMomentTaskEntity);
            } else {
                // 个人
                weMomentTaskEntity.setStatus(MomentStatusEnum.FINISH.getType());
                // 过滤不在客户不正常的
                SpringUtil.getApplicationContext().publishEvent(new SendAppMessageEvent(
                        weMomentTaskEntity.getCorpId(),
                        candidateUserIdList.stream().filter(existDbUserId::contains).collect(Collectors.toList()),
                        WeConstans.PERSONAL_MOMENT_MSG,
                        new String[]{
                                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, weMomentTaskEntity.getSendTime()),
                                applicationMessageUtil.getMomentUrl(weMomentTaskEntity.getCorpId(), weMomentTaskEntity.getId())
                        }
                ));

                // 保存客户关系
                saveCustomerRefInvoke.run();
                //保存个人朋友圈任务
                this.saveOrUpdate(weMomentTaskEntity);
            }
        } else {
            // 定时发送
            // 个人朋友圈保存客户关系
            if (MomentTypeEnum.PERSONAL_MOMENT.getType().equals(weMomentTaskEntity.getType())) {
                saveCustomerRefInvoke.run();
            }
            weMomentTaskEntity.setStatus(MomentStatusEnum.NOT_START.getType());
            this.save(weMomentTaskEntity);
        }
        log.info("耗时: {}ms", System.currentTimeMillis() - beginTime);
    }

    /**
     * 构建朋友圈任务
     * @param loginUser 当前登录用户
     * @param createMomentTaskDTO 创建朋友圈DTO
     * @return 朋友圈任务entity
     */
    private WeMomentTaskEntity buildMomentTask(LoginUser loginUser, CreateMomentTaskDTO createMomentTaskDTO) {
        //权限下的部门
        //        sw.start("查询权限下部门");
        List<String> departmentScope = getDepartmentScope(loginUser, createMomentTaskDTO.getDepartments());
        //        sw.stop();
        //权限下的员工
        //        sw.start("查询权限下员工");
        Set<String> userScope = getUserScope(loginUser, createMomentTaskDTO.getUsers(), departmentScope);
        return new WeMomentTaskEntity(createMomentTaskDTO, new ArrayList<>(userScope), departmentScope, loginUser.getUserId());
    }

    /**
     * 上传资源附件以及落库
     * @param createMomentTaskDTO 创建朋友圈实体
     * @param weMomentTaskEntity 朋友圈任务实体
     */
    private void uploadAndSaveAttachment(CreateMomentTaskDTO createMomentTaskDTO, WeMomentTaskEntity weMomentTaskEntity) {
        //先上传一遍素材判断是否合法
        if (CollectionUtils.isNotEmpty(createMomentTaskDTO.getAttachments())) {
            buildMomentAttachment(createMomentTaskDTO.getAttachments(), weMomentTaskEntity.getCorpId());
        }
        //保存任务附件
        saveMomentDetail(createMomentTaskDTO.getCorpId(), createMomentTaskDTO.getAttachments());

        //保存we_moment_detail_rel附件关联表
        saveMomentDetailRel(createMomentTaskDTO.getAttachments(), weMomentTaskEntity.getId());
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
    public void startCreatMoment(WeMomentTaskEntity weMomentTaskEntity, List<WeWordsDetailEntity> attachments) {
        weMomentTaskEntity.setSendTime(DateUtils.getNowDate());
        //是否为企业朋友圈
        List<String> createMomentUserList = weCustomerService.getUserIdList(weMomentTaskEntity.getUsers(), weMomentTaskEntity.getDepartments(), weMomentTaskEntity.getCorpId());
        if (MomentTypeEnum.ENTERPRISE_MOMENT.getType().equals(weMomentTaskEntity.getType())) {
            //创建企业朋友圈任务
            addMomentTask(attachments, weMomentTaskEntity,
                    () -> new ArrayList<>(createMomentUserList)
            );
            //更新创建状态
            updateTaskStatus(weMomentTaskEntity);
        } else {
            //个人朋友圈、发送应用消息、设置创建状态
            List<String> existDbUserId = weCustomerService.listExistDbUserIdByTagAndUserIdList(
                    createMomentUserList,
                    weMomentTaskEntity.getTags(),
                    weMomentTaskEntity.getCorpId()
            );
            // 发送应用通知
            SpringUtil.getApplicationContext().publishEvent(new SendAppMessageEvent(
                    weMomentTaskEntity.getCorpId(),
                    // 过滤不在客户不正常的
                    createMomentUserList.stream().filter(existDbUserId::contains).collect(Collectors.toList()),
                    WeConstans.PERSONAL_MOMENT_MSG,
                    new String[]{
                            DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, weMomentTaskEntity.getSendTime()),
                            applicationMessageUtil.getMomentUrl(weMomentTaskEntity.getCorpId(), weMomentTaskEntity.getId())
                    }
            ));
            weMomentTaskEntity.setStatus(MomentStatusEnum.FINISH.getType());
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
        List<String> userIds = weUserService.getUserInDataScope(loginUser).stream().map(WeUser::getUserId).collect(Collectors.toList());
        if (loginUser.isSuperAdmin()) {
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
    public List<MomentUserCustomerVO> listOfMomentPublishDetail(MomentUserCustomerDTO momentUserCustomerDTO, PageDomain pageDomain) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        Long momentTaskId = momentUserCustomerDTO.getMomentTaskId();
        MomentResultFilterModel filterModel = buildMomentDetailFilterModel(momentUserCustomerDTO, corpId);
        if (CollectionUtils.isEmpty(filterModel.getFilterResultIdList()) && filterModel.isFilterFlag()) {
            return new ArrayList<>();
        }
        pageDomain.page();
        List<MomentUserCustomerVO> momentDetailList = baseMapper.listOfMomentPublishDetail(
                new MomentDetailQueryContext(
                        corpId, momentTaskId, filterModel));


        fillMomentUserAndCustomerInfo(momentDetailList, momentTaskId, corpId);
        return momentDetailList;
    }

    /**
     * 填充用户和客户信息
     * @param momentDetailList 朋友圈详情
     * @param momentTaskId 朋友圈任务id
     * @param corpId 企业id
     */
    private void fillMomentUserAndCustomerInfo(List<MomentUserCustomerVO> momentDetailList, Long momentTaskId, String corpId) {
        // 1.填充员工信息, 同一个企业下, userId唯一
        List<String> userIdList = momentDetailList.stream().map(MomentUserCustomerVO::getUserId).collect(Collectors.toList());
        // key:userId value:
        Map<String, UserNameHeadImgModel> userToInfoMapping = weUserMapper.selectList(new LambdaQueryWrapper<WeUser>()
                .select(WeUser::getUserId, WeUser::getName, WeUser::getAvatarMediaid)
                .eq(WeUser::getCorpId, corpId)
                .in(WeUser::getUserId, userIdList)
        ).stream().collect(Collectors.toMap(WeUser::getUserId, value -> new UserNameHeadImgModel(value.getUserId(), value.getName(), value.getAvatarMediaid()), (v1, v2) -> v1));


        // 2.填充客户昵称, 根据员工id分组, 对列表转化为客户昵称, 并逗号拼接昵称
        // key:userId value: customerName1,customerName2
        Map<String, String> userToCustomerNameMapping = weMomentTaskResultMapper.selectCustomerInfo(userIdList, corpId, momentTaskId)
                .stream()
                .collect(Collectors.groupingBy(CustomerUserNameModel::getUserId, Collectors.mapping(CustomerUserNameModel::getCustomerName, Collectors.joining(","))));

        momentDetailList.forEach(it -> {
            // 员工信息
            UserNameHeadImgModel userModel = userToInfoMapping.getOrDefault(it.getUserId(), new UserNameHeadImgModel());
            it.setUserName(userModel.getUserName());
            it.setHeadImageUrl(userModel.getHeadImg());

            // 客户昵称
            it.setCustomerName(userToCustomerNameMapping.getOrDefault(it.getUserId(), ""));

        });

    }

    /**
     * 构建朋友圈详情过滤条件model
     * @param momentUserCustomerDTO 朋友圈客户条件DTO
     * @param corpId 企业id
     * @return 过滤model
     */
    private MomentResultFilterModel buildMomentDetailFilterModel(MomentUserCustomerDTO momentUserCustomerDTO, String corpId) {

        MomentResultFilterModel resultFilterModel = new MomentResultFilterModel();
        // 1.userName过滤
        if (StringUtils.isNotBlank(momentUserCustomerDTO.getUserName())) {
            List<Long> userNameFilterDetailIdList = weMomentTaskResultMapper.selectIdListLikeUserNameKeyword(momentUserCustomerDTO.getMomentTaskId(), corpId, momentUserCustomerDTO.getUserName());
            resultFilterModel.addDetailIdAndConditionFilter(userNameFilterDetailIdList);
        }
        // 2.publishStatus过滤
        if (momentUserCustomerDTO.getPublishStatus() != null) {

            List<Long> pushStatusFilterDetailIdList = weMomentTaskResultMapper.selectList(new LambdaQueryWrapper<WeMomentTaskResultEntity>()
                    .select(WeMomentTaskResultEntity::getId)
                    .eq(WeMomentTaskResultEntity::getPublishStatus, momentUserCustomerDTO.getPublishStatus())
            ).stream().map(WeMomentTaskResultEntity::getId).collect(Collectors.toList());
            resultFilterModel.addDetailIdAndConditionFilter(pushStatusFilterDetailIdList);
        }

        return resultFilterModel;
    }

    @Override
    public MomentTotalVO getTotal(Long momentTaskId) {
        return baseMapper.getTotal(momentTaskId);
    }

    @Override
    public SearchMomentVO getMomentTaskBasicInfo(Long momentTaskId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        SearchMomentVO momentTaskBasicInfo = baseMapper.getMomentTaskBasicInfo(momentTaskId,LoginTokenService.getLoginUser().getCorpId());
        // 设置可提醒员工列表
        List<String> reMaindUserIdList = weMomentTaskResultService.list(
                Wrappers.lambdaQuery(WeMomentTaskResultEntity.class)
                        .select(WeMomentTaskResultEntity::getUserId)
                        .eq(WeMomentTaskResultEntity::getMomentTaskId, momentTaskId)
        ).stream().map(WeMomentTaskResultEntity::getUserId).collect(Collectors.toList());
        momentTaskBasicInfo.setRemindUserIdList(reMaindUserIdList);

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
                Date publishTime = null;
                if (MomentPublishStatusEnum.PUBLISH.getType().equals(momentTask.getPublish_status())) {
                    //设置发布时间
                    publishTime = new Date();
                }
                // 如果发布时间不为空,这里不会再次更新发布时间
                weMomentTaskResultService.updatePublishInfo(taskId, momentTask.getUserid(), momentTask.getPublish_status(), publishTime);
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
        log.info("发送员工朋友圈提醒信息,momentTaskId：{}", momentTaskId);
        if (MomentTypeEnum.ENTERPRISE_MOMENT.getType().equals(type)) {
            applicationMessageUtil.sendAppMessage(sendList, LoginTokenService.getLoginUser().getCorpId(), WeConstans.ENTERPRISE_MOMENT_USER_MSG, new String[]{
                    sendTime
            });
        } else {
            applicationMessageUtil.sendAppMessage(sendList, LoginTokenService.getLoginUser().getCorpId(), WeConstans.PERSONAL_MOMENT_USER_MSG, new String[]{
                    sendTime,
                    applicationMessageUtil.getMomentUrl(LoginTokenService.getLoginUser().getCorpId(), momentTaskId)
            });
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

    /**
     * 获取权限下员工范围数据
     * @param loginUser
     * @param users
     * @param departmentScope
     * @return
     */
    private Set<String> getUserScope(LoginUser loginUser, List<String> users, List<String> departmentScope) {
        // 获取数据权限下的所有员工id
        Set<String> loginUserScopeUserId = weUserService.getUserInDataScope(loginUser).stream().map(WeUser::getUserId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(users) && CollectionUtils.isEmpty(departmentScope)) {
            return loginUserScopeUserId;
        } else if (CollectionUtils.isNotEmpty(users)) {
            // 返回账号权限下的员工
            return users.stream().filter(loginUserScopeUserId::contains).collect(Collectors.toSet());
        } else {
            // 部门不为空
            List<String> userIdsFromDepartment = weUserService.listOfUserId(loginUser.getCorpId(), departmentScope.toArray(new String[]{}));
            if (CollectionUtils.isEmpty(userIdsFromDepartment)) {
                return loginUserScopeUserId;
            } else {
                // 部门下员工不为空,则返回员工的范围数据空
                return new HashSet<>();
            }
        }

    }

    /**
     * 需要保证详情能查询数据，插入成员待发布结果
     * @param taskId    任务id
     * @param candidateUserIdList   待发送员工
     * @param existDbUserId 客户所属员工
     * @param type 朋友圈创建类型 {@link MomentTypeEnum}
     * @param pushRange 选择发送类型 {@link MomentPushRangeEnum}
     */
    private void saveTaskNotPublishResult(Long taskId, List<String> candidateUserIdList, Set<String> existDbUserId, Integer type, Integer pushRange) {
        if (CollectionUtils.isEmpty(candidateUserIdList) || taskId == null) {
            return;
        }

        //个人朋友圈发送员工限制
        Set<String> existDbUserIdSet = new HashSet<>(candidateUserIdList);
        if (MomentTypeEnum.PERSONAL_MOMENT.getType().equals(type) && MomentPushRangeEnum.SELECT_CUSTOMER.getType().equals(pushRange)) {
            existDbUserIdSet = existDbUserIdSet.stream().filter(existDbUserId::contains).collect(Collectors.toSet());
        }
        List<WeMomentTaskResultEntity> resultEntityList = new ArrayList<>();
        //构造执行详情数据
        WeMomentTaskResultEntity resultEntity;
        for (String userId : existDbUserIdSet) {
            resultEntity = new WeMomentTaskResultEntity();
            resultEntity.setUserId(userId);
            resultEntity.setMomentTaskId(taskId);
            if (CollectionUtils.isNotEmpty(existDbUserId) && existDbUserId.contains(userId)) {
                resultEntity.setPublishStatus(MomentPublishStatusEnum.NOT_PUBLISH.getType());
            } else {
                resultEntity.setPublishStatus(MomentPublishStatusEnum.NO_AUTHORITY.getType());
                resultEntity.setRemark(WeConstans.MOMENT_NO_CUSTOMER);
            }
            resultEntityList.add(resultEntity);
        }
        //构造执行详情数据
        log.info("保存执行结果开始");
        BatchInsertUtil.doInsert(resultEntityList, weMomentTaskResultService::saveBatch, 500);
        log.info("保存执行结果结束, size: {}", resultEntityList.size());

    }

    /**
     * 创建企业朋友圈任务
     *
     * @param weMomentTaskEntity  任务实体
     */
    private void addMomentTask(List<WeWordsDetailEntity> attachmentDetails, WeMomentTaskEntity weMomentTaskEntity, Supplier<List<String>> createUserIdSupplier) {
        //构造创建企业朋友圈参数

        List<Integer> selectDepartments = StringUtils.isNotBlank(weMomentTaskEntity.getDepartments())
                ? Arrays.stream(weMomentTaskEntity.getDepartments().split(StrUtil.COMMA)).map(Integer::valueOf).collect(Collectors.toList())
                : Lists.newArrayList();

        List<String> selectTags = StringUtils.isNotBlank(weMomentTaskEntity.getTags())
                ? Arrays.asList(weMomentTaskEntity.getTags().split(StrUtil.COMMA))
                : Lists.newArrayList();

        // 查询部门下的员工列表
        // 发送的总员工列表 = 选择的员工 + 选择的部门下的员工
        AddMomentTaskDTO addMomentTaskDTO = new AddMomentTaskDTO(createUserIdSupplier.get(), selectDepartments, selectTags, weMomentTaskEntity.getContent());
        //临时素材id
        if (CollectionUtils.isNotEmpty(attachmentDetails)) {
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
        MomentPushRangeEnum.validCode(createMomentTaskDTO.getPushRange());

        //校验corpId
        if (StringUtils.isBlank(createMomentTaskDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
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