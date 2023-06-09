package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.*;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.easyink.wecom.client.WeCustomerMessagePushClient;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.WeMediaDTO;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.WePresTagGroupTaskDTO;
import com.easyink.wecom.domain.dto.common.Attachment;
import com.easyink.wecom.domain.dto.common.Attachments;
import com.easyink.wecom.domain.dto.message.*;
import com.easyink.wecom.domain.entity.BaseExternalUserEntity;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.service.*;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WePresTagGroupTaskServiceImpl extends ServiceImpl<WePresTagGroupTaskMapper, WePresTagGroupTask> implements WePresTagGroupTaskService {

    private final WePresTagGroupTaskMapper taskMapper;
    private final WePresTagGroupTaskStatMapper taskStatMapper;
    private final WePresTagGroupTaskScopeMapper taskScopeMapper;
    private final WePresTagGroupTaskTagMapper taskTagMapper;
    private final WeCustomerMessagePushClient customerMessagePushClient;
    private final WeMessagePushClient messagePushClient;
    private final WeMaterialService materialService;
    private final WeGroupCodeMapper groupCodeMapper;
    private final WeGroupCodeService weGroupCodeService;
    private final WeCorpAccountService corpAccountService;
    private final WeCustomerMapper customerMapper;
    private final WePresTagGroupTaskStatMapper wePresTagGroupTaskStatMapper;
    private final WeUserService weUserService;
    private final WeGroupMemberService weGroupMemberService;
    private final WeGroupService weGroupService;
    private final WeCustomerService weCustomerService;

    @Value("${wecome.authorizeUrl}")
    private String authorizeUrl;

    @Lazy
    @Autowired
    public WePresTagGroupTaskServiceImpl(WePresTagGroupTaskMapper taskMapper, WePresTagGroupTaskStatMapper taskStatMapper, WePresTagGroupTaskScopeMapper taskScopeMapper, WePresTagGroupTaskTagMapper taskTagMapper, WeCustomerMessagePushClient customerMessagePushClient, WeMessagePushClient messagePushClient, WeMaterialService materialService, WeGroupCodeMapper groupCodeMapper, WeUserService weUserService, WeCorpAccountService corpAccountService, WeCustomerMapper customerMapper, WePresTagGroupTaskStatMapper wePresTagGroupTaskStatMapper, WeGroupCodeService weGroupCodeService, WeGroupMemberService weGroupMemberService, WeGroupService weGroupService, WeCustomerService weCustomerService) {
        this.taskMapper = taskMapper;
        this.taskStatMapper = taskStatMapper;
        this.taskScopeMapper = taskScopeMapper;
        this.taskTagMapper = taskTagMapper;
        this.customerMessagePushClient = customerMessagePushClient;
        this.messagePushClient = messagePushClient;
        this.materialService = materialService;
        this.groupCodeMapper = groupCodeMapper;
        this.weUserService = weUserService;
        this.corpAccountService = corpAccountService;
        this.customerMapper = customerMapper;
        this.wePresTagGroupTaskStatMapper = wePresTagGroupTaskStatMapper;
        this.weGroupCodeService = weGroupCodeService;
        this.weGroupMemberService = weGroupMemberService;
        this.weGroupService = weGroupService;
        this.weCustomerService = weCustomerService;
    }

    /**
     * 添加新标签建群任务
     *
     * @param task       建群任务本体信息
     * @param tagIdList  标签列表
     * @param emplIdList 员工列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int add(WePresTagGroupTask task, List<String> tagIdList, List<String> emplIdList) {
        if (taskMapper.insertTask(task) > 0) {
            // 保存标签对象
            if (CollUtil.isNotEmpty(tagIdList)) {
                List<WePresTagGroupTaskTag> taskTagList = tagIdList
                        .stream()
                        .map(id -> new WePresTagGroupTaskTag(task.getTaskId(), id))
                        .collect(Collectors.toList());
                taskTagMapper.batchBindsTaskTags(taskTagList);
            }

            // 保存员工信息
            if (CollUtil.isNotEmpty(emplIdList)) {
                List<WePresTagGroupTaskScope> wePresTagGroupTaskScopeList = emplIdList
                        .stream()
                        .map(id -> new WePresTagGroupTaskScope(task.getTaskId(), id, false))
                        .collect(Collectors.toList());
                taskScopeMapper.batchBindsTaskScopes(wePresTagGroupTaskScopeList);
            }
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTask(WePresTagGroupTaskDTO taskDTO, WePresTagGroupTask task, LoginUser loginUser) {
        if (taskDTO == null || task == null || loginUser == null) {
            throw new CustomException("请求参数不能为空");
        }
        List<String> scopeList = taskDTO.getScopeList();
        Integer sendType = taskDTO.getSendType();
        List<String> tagList = taskDTO.getTagList();
        Integer sendScope = taskDTO.getSendScope();
        WePresTagGroupTaskService wePresTagGroupTaskService = (WePresTagGroupTaskService) AopContext.currentProxy();
        //Society my sister Li 个人群发需要scopeList数据(成员userId)
        if (CustomerTrajectoryEnums.TaskSendType.SINGLE.getType().equals(sendType)) {
            //选择所有客户，则需要获取当前账号下的数据权限范围的成员userId
            if (PresTagGroupTaskSendScopeEnum.ALL.getSendScope().equals(sendScope)) {
                List<WeUser> weUserList = weUserService.getUserInDataScope(loginUser);
                scopeList = weUserList.stream().map(WeUser::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(scopeList)) {
                    throw new CustomException("查无可发送的成员信息，请重新选择！");
                }
            } else {
                //选择部分客户 需要指定成员
                if (CollectionUtils.isEmpty(scopeList)) {
                    throw new CustomException("请选择添加人！");
                }
            }
            //个人群发，客户数据需要保存到WePresTagGroupStat表
            wePresTagGroupTaskService.saveWePresTagGroupStat(task.getTaskId(), scopeList);
        }
        //保存任务记录
        int affectedRows = wePresTagGroupTaskService.add(task, tagList, scopeList);
        if (affectedRows > 0) {
            boolean hasScope = CollectionUtils.isNotEmpty(scopeList);
            boolean hasTag = CollectionUtils.isNotEmpty(tagList);
            //符合筛选条件的外部客户数据
            List<String> externalIds = this.selectExternalUserIds(task.getTaskId(), hasScope, hasTag, sendScope, taskDTO.getCusBeginTime(), taskDTO.getCusEndTime());
            wePresTagGroupTaskService.sendMessage(task, externalIds);
        }
        return affectedRows;
    }

    /**
     * 创建一个标签入群群发任务
     *
     * @param wePresTagGroupTaskDTO 任务参数对象
     * @param loginUser             当前登录人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTaskV2(WePresTagGroupTaskDTO wePresTagGroupTaskDTO, LoginUser loginUser) {
        if (wePresTagGroupTaskDTO == null || loginUser == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //选择所有客户，则需要获取当前账号下的数据权限范围的成员userId
        if (CollectionUtils.isEmpty(wePresTagGroupTaskDTO.getScopeList())) {
            List<WeUser> weUserList = weUserService.getUserInDataScope(loginUser);
            List<String> scopeList = weUserList.stream().filter(Objects::nonNull).map(WeUser::getUserId).collect(Collectors.toList());
            wePresTagGroupTaskDTO.setScopeList(scopeList);
        }
        //校验是否选择了使用员工
        if (CollectionUtils.isEmpty(wePresTagGroupTaskDTO.getScopeList())) {
            throw new CustomException(ResultTip.TIP_NOT_SELECT_STAFF);
        }
        //判断群活码是否存在 建议群活码接口类提供一个校验接口
        WeGroupCode weGroupCode = weGroupCodeService.getById(wePresTagGroupTaskDTO.getGroupCodeId());
        if (weGroupCode == null || weGroupCode.getId() == null) {
            throw new CustomException(ResultTip.TIP_ACTIVE_CODE_NOT_EXSIT);
        }
        // 检测任务名是否可用
        if (this.isNameOccupied(loginUser.getCorpId(), wePresTagGroupTaskDTO.getTaskName())) {
            throw new CustomException(ResultTip.TIP_TASK_NAME_EXSIT);
        }
        //转化数据库主表对象实体并入库
        WePresTagGroupTask task = new WePresTagGroupTask();
        BeanUtils.copyProperties(wePresTagGroupTaskDTO, task);
        task.setCorpId(loginUser.getCorpId());
        task.setCreateBy(loginUser.getUsername());
        task.setCusBeginTime(DateUtils.parseBeginDay(task.getCusBeginTime()));
        task.setCusEndTime(DateUtils.parseEndDay(task.getCusEndTime()));
        taskMapper.insertTask(task);
        //过滤客户和执行者
        Map<String, Set<String>> executiveStaffAndTargetMap = this.filterExecutiveEmployeeAndTarget(wePresTagGroupTaskDTO, loginUser, task.getTaskId());

        if (MapUtils.isEmpty(executiveStaffAndTargetMap)) {
            log.info("没有需要执行的员工,taskId:{}", task.getTaskId());
            return;
        }
        //推送消息给员工
        SpringUtils.getAopProxy(this).handleSendMessageAsync(executiveStaffAndTargetMap, task);
    }

    /**
     * 根据条件查询任务列表
     *
     * @param corpId    企业ID
     * @param taskName  任务名称
     * @param sendType  发送方式
     * @param createBy  创建人
     * @param beginTime 起始时间 正确格式为:yyyy-MM-dd
     * @param endTime   结束时间 正确格式为:yyyy-MM-dd
     * @return 结果
     */
    @Override
    public List<WePresTagGroupTaskVO> selectTaskList(String corpId, String taskName, Integer sendType, String createBy, String beginTime, String endTime) {
        beginTime = DateUtils.parseBeginDay(beginTime);
        endTime = DateUtils.parseEndDay(endTime);
        // 查询任务列表
        List<WePresTagGroupTaskVO> taskVoList = taskMapper.selectTaskList(corpId, taskName, sendType, createBy, beginTime, endTime);
        if (CollUtil.isNotEmpty(taskVoList)) {
            taskVoList.forEach(task -> this.setGroupCodeAndScopeAndTag(task, corpId));
        }
        return taskVoList;
    }

    /**
     * 通过id获取老客标签建群任务
     *
     * @param corpId 企业ID
     * @param taskId 任务id
     * @return {@link WePresTagGroupTaskVO}
     */
    @Override
    public WePresTagGroupTaskVO getTaskById(Long taskId, String corpId) {
        //校验corpId
        this.checkCorpId(corpId);
        WePresTagGroupTaskVO taskVo = taskMapper.selectTaskById(taskId);
        if (ObjectUtils.isNotEmpty(taskVo)) {
            setGroupCodeAndScopeAndTag(taskVo, corpId);
        }
        return taskVo;
    }

    /**
     * 批量删除老客标签建群任务
     *
     * @param idList 任务id列表
     * @return 删除的行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int batchRemoveTaskByIds(String corpId, Long[] idList) {
        List<Long> ids = Arrays.asList(idList);

        // 解除关联的标签
        LambdaQueryWrapper<WePresTagGroupTaskTag> tagQueryWrapper = new LambdaQueryWrapper<>();
        tagQueryWrapper.in(WePresTagGroupTaskTag::getTaskId, ids);
        taskTagMapper.delete(tagQueryWrapper);
        // 解除关联的员工
        LambdaQueryWrapper<WePresTagGroupTaskScope> scopeQueryWrapper = new LambdaQueryWrapper<>();
        scopeQueryWrapper.in(WePresTagGroupTaskScope::getTaskId, ids);
        taskScopeMapper.delete(scopeQueryWrapper);
        // 删除其用户统计
        LambdaQueryWrapper<WePresTagGroupTaskStat> statQueryWrapper = new LambdaQueryWrapper<>();
        statQueryWrapper.in(WePresTagGroupTaskStat::getTaskId, ids);
        taskStatMapper.delete(statQueryWrapper);

        // 最后删除task
        LambdaQueryWrapper<WePresTagGroupTask> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.in(WePresTagGroupTask::getTaskId, ids);
        taskQueryWrapper.eq(WePresTagGroupTask::getCorpId, corpId);
        return taskMapper.delete(taskQueryWrapper);
    }

    /**
     * 更新老客户标签建群任务
     *
     * @param corpId                企业ID
     * @param taskId                待更新任务id
     * @param wePresTagGroupTaskDto 更新数据
     * @return 更新条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int updateTask(String corpId, Long taskId, WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        //校验corpId
        this.checkCorpId(corpId);
        if (taskId == null) {
            throw new CustomException("任务ID不能为空");
        }
        if (wePresTagGroupTaskDto == null) {
            throw new CustomException("wePresTagGroupTaskDto不能为空");
        }
        WePresTagGroupTask wePresTagGroupTask = new WePresTagGroupTask();
        BeanUtils.copyProperties(wePresTagGroupTaskDto, wePresTagGroupTask);
        wePresTagGroupTask.setTaskId(taskId);
        wePresTagGroupTask.setCorpId(corpId);
        if (isNameOccupied(wePresTagGroupTask)) {
            throw new CustomException("任务名已存在");
        }

        wePresTagGroupTask.setUpdateBy(LoginTokenService.getUsername());
        if (taskMapper.updateTask(wePresTagGroupTask) > 0) {

            // 更新标签 - 先删除旧标签
            LambdaUpdateWrapper<WePresTagGroupTaskTag> taskTagQueryWrapper = new LambdaUpdateWrapper<>();
            taskTagQueryWrapper.eq(WePresTagGroupTaskTag::getTaskId, taskId);
            taskTagMapper.delete(taskTagQueryWrapper);
            // 更新标签 - 再添加新标签
            List<String> tagIdList = wePresTagGroupTaskDto.getTagList();
            if (CollUtil.isNotEmpty(tagIdList)) {
                List<WePresTagGroupTaskTag> wePresTagGroupTaskTagList = tagIdList
                        .stream()
                        .map(id -> new WePresTagGroupTaskTag(taskId, id))
                        .collect(Collectors.toList());
                taskTagMapper.batchBindsTaskTags(wePresTagGroupTaskTagList);
            }

            // 先解除旧的员工绑定信息
            LambdaUpdateWrapper<WePresTagGroupTaskScope> scopeQueryWrapper = new LambdaUpdateWrapper<>();
            scopeQueryWrapper.eq(WePresTagGroupTaskScope::getTaskId, taskId);
            taskScopeMapper.delete(scopeQueryWrapper);

            // 再重新绑定员工信息
            List<String> userIdList = wePresTagGroupTaskDto.getScopeList();
            if (CollUtil.isNotEmpty(userIdList)) {
                List<WePresTagGroupTaskScope> wePresTagGroupTaskScopeList = userIdList.stream().map(id -> new WePresTagGroupTaskScope(taskId, id, false)).collect(Collectors.toList());
                taskScopeMapper.batchBindsTaskScopes(wePresTagGroupTaskScopeList);
            }
            return 1;
        }
        return 0;
    }

    /**
     * 查询标签建群详情数据
     *
     * @param corpId       企业ID
     * @param taskId       任务id
     * @param customerName 客户名称
     * @param isInGroup    是否已进群
     * @param isSent       送达状态
     * @param pageNum      页码
     * @param pageSize     每页数量
     * @return WePresTagGroupTaskStatResultVO
     */
    @Override
    public WePresTagGroupTaskStatResultVO getStatByTaskId(String corpId, Long taskId, String customerName, Integer isInGroup, Integer isSent, Integer pageNum, Integer pageSize) {
        //校验参数
        validParam(corpId, taskId, pageNum, pageSize);
        WePresTagGroupTask task = taskMapper.selectById(taskId);
        List<WePresTagGroupTaskStatVO> statVoList;
        if (task.getSendType().equals(CustomerTrajectoryEnums.TaskSendType.CROP.getType())) {
            // 企业群发。通过企微接口统计
            QueryCustomerMessageStatusResultDataObjectDTO requestData = new QueryCustomerMessageStatusResultDataObjectDTO();
            requestData.setMsgid(task.getMsgid());
            try {
                QueryCustomerMessageStatusResultDTO resultDto = customerMessagePushClient.queryCustomerMessageStatus(requestData, corpId);
                // 该任务对应的所有外部联系人id
                List<String> externalIdList = taskStatMapper.getAllExternalIdByTaskId(taskId);
                //根据条件过滤
                return filterList(task.getGroupCodeId(), externalIdList, resultDto.getDetail_list(), customerName, isInGroup, isSent, pageNum, pageSize, corpId);
            } catch (Exception e) {
                log.error("标签建群-获取详情异常：{}", ExceptionUtils.getStackTrace(e));
                return new WePresTagGroupTaskStatResultVO(new ArrayList<>(), 0);
            }
        } else {
            // 个人群发。通过数据库进行统计
            startPage(pageNum, pageSize);
            statVoList = taskStatMapper.selectStatInfoByTaskId(taskId, customerName, isInGroup, isSent, corpId);
            setAvatar(statVoList, corpId);
            setWeUserInfo(statVoList, corpId);
            setGroupInfo(taskId, statVoList, corpId);
            return new WePresTagGroupTaskStatResultVO(statVoList, null);
        }
    }

    /**
     * 设置头像
     *
     * @param statVoList statVoList
     * @param corpId     企业ID
     */
    private void setAvatar(List<WePresTagGroupTaskStatVO> statVoList, String corpId) {
        List<String> weCusUserIdList = statVoList.stream().map(WePresTagGroupTaskStatVO::getExternalUserid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(weCusUserIdList)) {
            List<WeCustomer> weCustomerList = weCustomerService.list(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getCorpId, corpId).in(WeCustomer::getExternalUserid, weCusUserIdList));
            Map<String, String> avatarMap = new HashMap<>();
            for (WeCustomer weCustomer : weCustomerList) {
                avatarMap.put(weCustomer.getExternalUserid(), weCustomer.getAvatar());
            }
            for (WePresTagGroupTaskStatVO taskStat : statVoList) {
                taskStat.setAvatar(avatarMap.get(taskStat.getExternalUserid()));
            }
        }
    }


    /**
     * 校验参数
     *
     * @param corpId   企业ID
     * @param taskId   任务id
     * @param pageNum  页码
     * @param pageSize 每页数量
     */
    private void validParam(String corpId, Long taskId, Integer pageNum, Integer pageSize) {
        //校验corpId
        this.checkCorpId(corpId);
        if (taskId == null) {
            throw new CustomException("任务ID不能为空");
        }
        if (pageNum == null || pageSize == null) {
            throw new CustomException("分页参数不能为空");
        }
    }

    /**
     * 根据条件过滤和分页
     *
     * @param groupCodeId    群活码ID
     * @param externalIdList 已在群内的客户列表
     * @param detailList     从企微接口查询的所有数据
     * @param customerName   模糊客户姓名
     * @param isInGroup      是否在群
     * @param isSent         是否送达
     * @param pageNum        页码
     * @param pageSize       每页数量
     * @return WePresTagGroupTaskStatResultVO
     */
    private WePresTagGroupTaskStatResultVO filterList(Long groupCodeId, List<String> externalIdList, List<DetailMessageStatusResultDTO> detailList, String customerName,
                                                      Integer isInGroup, Integer isSent, Integer pageNum, Integer pageSize, String corpId) {
        List<WePresTagGroupTaskStatVO> dataList = new ArrayList<>();
        //把群发记录接口返回的userid和externalUserid存起来
        Set<String> userIdSet = new HashSet<>();
        Set<String> externalUseridSet = new HashSet<>();
        detailList.forEach(detail -> {
            externalUseridSet.add(detail.getExternal_userid());
            userIdSet.add(detail.getUserid());
        });

        List<String> externalUseridList = new ArrayList<>(externalUseridSet);
        List<String> userIdList = new ArrayList<>(userIdSet);
        //根据客户userId查询name
        List<WeCustomerNameAndUserIdVO> weCustomerList = customerMapper.selectWeCustomerByUserIdList(externalUseridList, userIdList, corpId);

        //转换成map
        Map<String, WeCustomerNameAndUserIdVO> customerInfoMap = new HashMap<>(weCustomerList.size());
        weCustomerList.forEach(weCustomer -> {
            if (StringUtils.isAnyBlank(weCustomer.getExternalUserId(), weCustomer.getUserId())) {
                return;
            }
            String key = weCustomer.getExternalUserId() + ":" + weCustomer.getUserId();
            customerInfoMap.put(key, weCustomer);
        });

        for (DetailMessageStatusResultDTO resultDTO : detailList) {
            if (StringUtils.isAnyBlank(resultDTO.getExternal_userid(), resultDTO.getUserid())) {
                continue;
            }
            //获取客户信息
            String key = resultDTO.getExternal_userid() + ":" + resultDTO.getUserid();
            WeCustomerNameAndUserIdVO customerInfo = customerInfoMap.get(key);
            if (customerInfo == null) {
                continue;
            }
            WePresTagGroupTaskStatVO statVo = new WePresTagGroupTaskStatVO();
            statVo.setStatus(resultDTO.getStatus());
            statVo.setExternalUserid(resultDTO.getExternal_userid());
            statVo.setUserId(resultDTO.getUserid());
            statVo.setCustomerName(customerInfo.getName());
            statVo.setRemark(customerInfo.getRemark());
            statVo.setInGroup(externalIdList.contains(resultDTO.getExternal_userid()));
            //查询是否符合搜索条件
            int result = statVo.isInGroup() ? 1 : 0;
            if (isInGroup != null && !isInGroup.equals(result)) {
                continue;
            }
            if (StringUtils.isNotBlank(customerName) && (!statVo.getCustomerName().contains(customerName) && !statVo.getRemark().contains(customerName))) {
                continue;
            }
            if (isSent != null) {
                //当isSent = 2 时，需要匹配Status=2、3的数据
                if (WeConstans.SEND_CORP_MESSAGE_ISSEND_FAIL.equals(isSent) && (MessageStatusEnum.ALREADY_SEND.getType().equals(statVo.getStatus()) || MessageStatusEnum.NOT_FRIEND.getType().equals(statVo.getStatus()))) {
                    dataList.add(statVo);
                    continue;
                }
                if (!isSent.equals(Integer.parseInt(statVo.getStatus()))) {
                    continue;
                }
            }
            dataList.add(statVo);
        }
        int total = dataList.size();

        int startSize = (pageNum - 1) * pageSize;
        int endSize = Math.min(total, pageNum * pageSize);
        if (startSize < 0 || startSize > total) {
            throw new CustomException("页码填写错误！");
        }
        dataList = dataList.subList(startSize, endSize);
        //设置头像
        setAvatar(dataList, corpId);
        //设置员工信息
        setWeUserInfo(dataList, corpId);
        //设置群信息
        setGroupInfo(groupCodeId, dataList, corpId);
        return new WePresTagGroupTaskStatResultVO(dataList, total);
    }


    /**
     * 设置请求分页数据
     */
    protected void startPage(Integer pageNum, Integer pageSize) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (ObjectUtils.isNotEmpty(pageNum) && ObjectUtils.isNotEmpty(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 根据任务id获取对应员工信息列表
     *
     * @param taskId 任务id
     * @param corpId 企业id
     * @return {@link List<WeCommunityTaskEmplVO>}
     */
    @Override
    public List<WeCommunityTaskEmplVO> getScopeListByTaskId(Long taskId, String corpId) {
        //校验corpId
        this.checkCorpId(corpId);
        return taskScopeMapper.getScopeListByTaskId(taskId, corpId);
    }

    /**
     * 根据任务id获取对应标签信息列表
     *
     * @param taskId 任务id
     * @return 结果
     */
    @Override
    public List<WeTag> getTagListByTaskId(Long taskId) {
        return taskTagMapper.getTagListByTaskId(taskId);
    }

    /**
     * 获取员工建群任务信息
     *
     * @param emplId 员工id
     * @param isDone 是否已处理
     * @param corpId 企业id
     * @return {@link List<WePresTagGroupTaskVO>}
     */
    @Override
    public List<WePresTagGroupTaskVO> getEmplTaskList(String emplId, boolean isDone, String corpId) {
        //校验corpId
        this.checkCorpId(corpId);
        List<WePresTagGroupTaskVO> taskVoList = taskMapper.getTaskListByEmplId(emplId, isDone);
        if (CollectionUtils.isNotEmpty(taskVoList)) {
            taskVoList.forEach(task -> this.setGroupCodeAndScopeAndTag(task, corpId));
        }
        return taskVoList;
    }

    /**
     * 员工发送信息后，变更其任务状态为 "完成"
     *
     * @param taskId 任务id
     * @param emplId 员工id
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int updateEmplTaskStatus(Long taskId, String emplId) {
        return taskScopeMapper.updateEmplTaskStatus(taskId, emplId);
    }

    /**
     * 任务名是否已占用
     *
     * @param task 任务信息
     * @return 名称是否占用
     */
    @Override
    public boolean isNameOccupied(WePresTagGroupTask task) {
        //校验corpId
        this.checkCorpId(task.getCorpId());
        if (StringUtils.isBlank(task.getTaskName())) {
            throw new CustomException("任务名称不能为空");
        }
        Long currentId = Optional.ofNullable(task.getTaskId()).orElse(-1L);
        LambdaQueryWrapper<WePresTagGroupTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WePresTagGroupTask::getDelFlag, WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE)
                .eq(WePresTagGroupTask::getTaskName, task.getTaskName())
                .eq(WePresTagGroupTask::getCorpId, task.getCorpId());
        List<WePresTagGroupTask> queryRes = baseMapper.selectList(queryWrapper);
        return !queryRes.isEmpty() && !currentId.equals(queryRes.get(0).getTaskId());
    }

    /**
     * 任务群活码、员工和标签
     *
     * @param corpId 企业id
     * @param taskVo 任务vo
     */
    private void setGroupCodeAndScopeAndTag(WePresTagGroupTaskVO taskVo, String corpId) {
        //校验corpId
        this.checkCorpId(corpId);
        // 任务群活码信息
        taskVo.fillGroupCodeVo();
        // 员工信息
        taskVo.setScopeList(this.getScopeListByTaskId(taskVo.getTaskId(), corpId));
        // 客户标签
        taskVo.setTagList(this.getTagListByTaskId(taskVo.getTaskId()));
    }

    /**
     * @param taskId    老客户标签建群任务id
     * @param hasScope  发送范围过滤
     * @param hasTag    老客户标签建群过滤
     * @param gender    性别
     * @param beginTime 起始时间 正确格式：yyyy-MM-dd
     * @param endTime   结束时间 正确格式：yyyy-MM-dd
     * @return
     */
    @Override
    public List<String> selectExternalUserIds(Long taskId, boolean hasScope, boolean hasTag, Integer gender, String beginTime, String endTime) {
        beginTime = DateUtils.parseBeginDay(beginTime);
        endTime = DateUtils.parseEndDay(endTime);
        return baseMapper.selectExternalUserIds(taskId, hasScope, hasTag, gender, beginTime, endTime);
    }

    @Override
    public void saveWePresTagGroupStat(Long taskId, List<String> scopeList) {
        wePresTagGroupTaskStatMapper.saveByFlowerCustomer(taskId, scopeList);
    }

    /**
     * 计算预期发送数量
     *
     * @param wePresTagGroupTaskDTO 任务条件
     * @param loginUser             当前登录
     * @return {@link PresTagExpectedReceptionVO}
     */
    @Override
    public PresTagExpectedReceptionVO getExpectedReceptionData(WePresTagGroupTaskDTO wePresTagGroupTaskDTO, LoginUser loginUser) {
        List<String> selectUserList = wePresTagGroupTaskDTO.getScopeList();
        //如果没有选择员工则进行补偿，获取当前账号下的数据权限范围的成员userId
        if (CollectionUtils.isEmpty(selectUserList)) {
            List<WeUser> weUserList = weUserService.getUserInDataScope(loginUser);
            List<String> scopeList = weUserList.stream().filter(Objects::nonNull).map(WeUser::getUserId).collect(Collectors.toList());
            wePresTagGroupTaskDTO.setScopeList(scopeList);
            selectUserList = scopeList;
        }
        //如果选择的员工补偿后依旧为空则直接返回
        if (CollectionUtils.isEmpty(selectUserList)) {
            return PresTagExpectedReceptionVO.builder().expectedCustomerCount(0).expectedUserCount(0).build();
        }
        //修正参数
        correctQueryParameters(wePresTagGroupTaskDTO);
        //根据条件查询发送客户列表
        List<BaseExternalUserEntity> list = this.selectExternalUserIds(loginUser.getCorpId(), wePresTagGroupTaskDTO.getSendGender(), wePresTagGroupTaskDTO.getCusBeginTime(), wePresTagGroupTaskDTO.getCusEndTime(), selectUserList, wePresTagGroupTaskDTO.getTagList());

        Set<String> userSet = new HashSet<>();
        Set<String> externalUserSet = new HashSet<>();

        //当没有选择群活码时，则按过滤数据返回
        if (wePresTagGroupTaskDTO.getGroupCodeId() == null) {
            return PresTagExpectedReceptionVO.builder().expectedCustomerCount(list.size()).expectedUserCount(userSet.size()).build();
        }

        //查询出当前活码对应的实际群中的所有群成员ID
        List<WeGroupCodeActual> actualCodeList = weGroupCodeService.selectActualList(wePresTagGroupTaskDTO.getGroupCodeId());
        Integer normal = 0;
        Set<String> chatIdSet = actualCodeList.stream().filter(actualCode -> normal.equals(actualCode.getDelFlag())).map(WeGroupCodeActual::getChatId).collect(Collectors.toSet());

        //当没有实际群时，按过滤数据返回
        if (CollectionUtils.isEmpty(chatIdSet)) {
            return PresTagExpectedReceptionVO.builder().expectedCustomerCount(list.size()).expectedUserCount(userSet.size()).build();
        }
        LambdaQueryWrapper<WeGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(WeGroupMember::getChatId, chatIdSet);
        List<WeGroupMember> groupMemberList = weGroupMemberService.list(queryWrapper);
        Set<String> memberSet = groupMemberList.stream().map(WeGroupMember::getUserId).collect(Collectors.toSet());

        list.forEach(o -> {
            //如果活码的实际群中已经包含该客户则不进行发送
            if (memberSet.contains(o.getExternalUserid())) {
                return;
            }
            userSet.add(o.getUserId());
            externalUserSet.add(o.getExternalUserid());
        });
        return PresTagExpectedReceptionVO.builder().expectedCustomerCount(externalUserSet.size()).expectedUserCount(userSet.size()).build();
    }

    /**
     * 修正查询参数
     * 解决场景：先选择部分客户编辑了条件后，再选择全部客户，前端还会把部分客户的条件参数带过来
     *
     * @param wePresTagGroupTaskDTO 任务参数
     */
    private void correctQueryParameters(WePresTagGroupTaskDTO wePresTagGroupTaskDTO) {
        if (PresTagGroupTaskSendScopeEnum.ALL.getSendScope().equals(wePresTagGroupTaskDTO.getSendScope())) {
            //如果发送是全部客户则对过滤条件进行清空
            wePresTagGroupTaskDTO.setSendGender(0);
            wePresTagGroupTaskDTO.setTagList(new ArrayList<>());
            wePresTagGroupTaskDTO.setCusBeginTime("");
            wePresTagGroupTaskDTO.setCusEndTime("");
        }
    }

    /**
     * 任务派发
     *
     * @param task 建群任务
     */
    @Override
    @Async
    public void sendMessage(WePresTagGroupTask task, List<String> externalIds) {
        try {
            Integer sendType = task.getSendType();

            if (sendType.equals(CustomerTrajectoryEnums.TaskSendType.CROP.getType())) {
                // 企业群发
                this.sendCorpMessage(task, externalIds);

            } else {
                // 个人群发
                this.sendEmployeeMessage(task);
            }

        } catch (Exception e) {
            log.error("任务派发失败：ex：{}", ExceptionUtils.getStackTrace(e));
            throw new WeComException(e.getMessage());
        }
    }

    /**
     * 企业群发
     *
     * @param task 建群任务信息
     */
    private void sendCorpMessage(WePresTagGroupTask task, List<String> externalIds) {
        try {
            // 构建企微api参数 [客户联系 - 消息推送 - 创建企业群发]
            if (externalIds.isEmpty()) {
                throw new CustomException("查不到客户！");
            }
            //校验corpId
            this.checkCorpId(task.getCorpId());
            if (task.getTaskId() == null || task.getGroupCodeId() == null || StringUtils.isBlank(task.getWelcomeMsg())) {
                throw new CustomException("任务基本信息不能为空");
            }
            WeCustomerMessagePushDTO queryData = new WeCustomerMessagePushDTO();
            queryData.setChat_type(ChatType.SINGLE.getName());
            queryData.setExternal_userid(externalIds);

            // 引导语
            TextMessageDTO text = new TextMessageDTO();
            text.setContent(task.getWelcomeMsg());
            queryData.setText(text.toText());

            // 群活码图片（上传临时文件获取media_id）
            ImageMessageDTO image = new ImageMessageDTO();
            WeGroupCode groupCode = groupCodeMapper.selectById(task.getGroupCodeId());
            WeMediaDTO mediaDto = materialService.uploadTemporaryMaterial(groupCode.getCodeUrl(), MediaType.IMAGE.getMediaType(), "临时文件", task.getCorpId());
            image.setMedia_id(mediaDto.getMedia_id());
            List<Attachment> list = new ArrayList<>();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("msgtype", "image");
//            jsonObject.put("image", image);
            Attachments attachments = new Attachments();
            attachments.setMsgtype(AttachmentTypeEnum.IMAGE.getTypeStr());
            attachments.setImage(image.toImage());
            list.add(attachments);
            queryData.setAttachments(list);

            // 调用企业群发接口
            SendMessageResultDTO resultDto = customerMessagePushClient.sendCustomerMessageToUser(queryData, task.getCorpId());

            // 设定该任务的msgid
            LambdaUpdateWrapper<WePresTagGroupTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WePresTagGroupTask::getTaskId, task.getTaskId());
            updateWrapper.set(WePresTagGroupTask::getMsgid, resultDto.getMsgid());
            this.update(updateWrapper);
        } catch (Exception e) {
            log.error("企业群发报错：ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 个人群发
     *
     * @param task 建群任务信息
     */
    private void sendEmployeeMessage(WePresTagGroupTask task) {
        if (task == null || StringUtils.isBlank(task.getCorpId()) || task.getTaskId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "task、corpId 、taskId不能为空");
        }
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        // 设置toUser参数
        List<WeCommunityTaskEmplVO> employeeList = taskScopeMapper.getScopeListByTaskId(task.getTaskId(), task.getCorpId());
        String toUser = employeeList.stream().map(WeCommunityTaskEmplVO::getUserId).collect(Collectors.joining("|"));
        pushDto.setTouser(toUser);

        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(task.getCorpId());
        String agentId = validWeCorpAccount.getAgentId();
        String corpId = validWeCorpAccount.getCorpId();
        if (StringUtils.isEmpty(agentId)) {
            throw new WeComException("当前agentId不可用或不存在");
        }
        pushDto.setAgentid(Integer.valueOf(agentId));

        // 设置文本消息
        TextMessageDTO text = new TextMessageDTO();

        String authorizeRedirectUrl = validWeCorpAccount.getH5DoMainName() + "/#/task";
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode(String.format("%s?corpId=%s&agentId=%s&type=%s", authorizeRedirectUrl, corpId, agentId, CommunityTaskType.TAG.getType()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("获取tag的redirectUrl失败：ex：{}", ExceptionUtils.getStackTrace(e));
        }
        String context = String.format(
                "你有一个新任务，<a href='%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect'>请点击此链接查看</a>",
                authorizeUrl, corpId, redirectUrl, corpId);
        text.setContent(context);
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());

        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送个人群发信息 ============> ");
        messagePushClient.sendMessageToUser(pushDto, agentId, task.getCorpId());
    }


    /**
     * 任务名是否被占用
     *
     * @param corpId   企业ID
     * @param taskName 任务名
     * @param taskId   任务ID
     * @return True已占用，false未占用
     */
    private boolean isNameOccupied(String corpId, String taskName, Long taskId) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(taskName)) {
            return false;
        }
        taskId = Optional.ofNullable(taskId).orElse(-1L);
        LambdaQueryWrapper<WePresTagGroupTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WePresTagGroupTask::getDelFlag, WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE)
                .eq(WePresTagGroupTask::getTaskName, taskName)
                .eq(WePresTagGroupTask::getCorpId, corpId)
                .last(GenConstants.LIMIT_1);
        WePresTagGroupTask queryRes = this.getOne(queryWrapper);
        return queryRes != null && !taskId.equals(queryRes.getTaskId());
    }

    /**
     * 任务名是否被占用
     *
     * @param corpId   企业ID
     * @param taskName 任务名
     * @return True已占用，false未占用
     */
    private boolean isNameOccupied(String corpId, String taskName) {
        return isNameOccupied(corpId, taskName, -1L);
    }

    /**
     * 校验企业ID
     * 该方法是原因将StringUtil 换成了lang3的StringUtil 没有了该方法
     *
     * @param corpId 企业ID
     * @throws {@link CustomException}
     */
    private void checkCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
    }

    /**
     * 过滤执行员工和发送目标客户集合
     *
     * @param wePresTagGroupTaskDTO 请求参数
     * @param loginUser             当前登录对象
     * @return Map<key:userId, Value:发送客户集合>
     */
    private Map<String, Set<String>> filterExecutiveEmployeeAndTarget(WePresTagGroupTaskDTO wePresTagGroupTaskDTO, LoginUser loginUser, Long taskId) {
        Map<String, Set<String>> resultMap = new HashMap<>();

        List<String> selectUserList = wePresTagGroupTaskDTO.getScopeList();
        //修正参数
        correctQueryParameters(wePresTagGroupTaskDTO);
        // 保存员工信息
        if (CollectionUtils.isEmpty(selectUserList)) {
            return resultMap;
        } else {
            List<WePresTagGroupTaskScope> wePresTagGroupTaskScopeList = selectUserList
                    .stream()
                    .map(id -> new WePresTagGroupTaskScope(taskId, id, false))
                    .collect(Collectors.toList());
            taskScopeMapper.batchBindsTaskScopes(wePresTagGroupTaskScopeList);
        }

        // 保存标签信息
        if (CollUtil.isNotEmpty(wePresTagGroupTaskDTO.getTagList())) {
            List<WePresTagGroupTaskTag> taskTagList = wePresTagGroupTaskDTO.getTagList()
                    .stream()
                    .map(id -> new WePresTagGroupTaskTag(taskId, id))
                    .collect(Collectors.toList());
            taskTagMapper.batchBindsTaskTags(taskTagList);
        }
        //根据条件查询发送客户列表
        List<BaseExternalUserEntity> list = this.selectExternalUserIds(loginUser.getCorpId(), wePresTagGroupTaskDTO.getSendGender(), wePresTagGroupTaskDTO.getCusBeginTime(), wePresTagGroupTaskDTO.getCusEndTime(), selectUserList, wePresTagGroupTaskDTO.getTagList());
        if (CollectionUtils.isEmpty(list)) {
            return resultMap;
        }
        //查询出当前活码对应的实际群中的所有群成员ID
        List<WeGroupCodeActual> actualCodeList = weGroupCodeService.selectActualList(wePresTagGroupTaskDTO.getGroupCodeId());
        Integer normal = 0;
        Set<String> chatIdSet = actualCodeList.stream().filter(actualCode -> normal.equals(actualCode.getDelFlag())).map(WeGroupCodeActual::getChatId).collect(Collectors.toSet());
        Set<String> memberSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(chatIdSet)) {
            LambdaQueryWrapper<WeGroupMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(WeGroupMember::getChatId, chatIdSet);
            List<WeGroupMember> groupMemberList = weGroupMemberService.list(queryWrapper);
            memberSet = groupMemberList.stream().map(WeGroupMember::getUserId).collect(Collectors.toSet());
        }
        List<WePresTagGroupTaskStat> statList = new ArrayList<>();
        Set<String> finalMemberSet = memberSet;
        list.forEach(o -> {
            //客户如果已在实际的某个群里就不发送了
            if (CollectionUtils.isNotEmpty(finalMemberSet) && finalMemberSet.contains(o.getExternalUserid())) {
                return;
            }
            WePresTagGroupTaskStat stat = new WePresTagGroupTaskStat();
            stat.setExternalUserid(o.getExternalUserid());
            stat.setCustomerName(o.getRemark());
            stat.setTaskId(taskId);
            stat.setUserId(o.getUserId());
            stat.setInGroup(false);
            stat.setSent(false);
            //组装发送详情
            statList.add(stat);
            //组装返回对象
            if (resultMap.containsKey(o.getUserId())) {
                Set<String> externalUseridSet = resultMap.get(o.getUserId());
                externalUseridSet.add(o.getExternalUserid());
            } else {
                Set<String> externalUseridSet = new HashSet<>();
                externalUseridSet.add(o.getExternalUserid());
                resultMap.put(o.getUserId(), externalUseridSet);
            }

        });

        //保存任务发送详情的基础信息
        if (CollectionUtils.isNotEmpty(statList)) {
            wePresTagGroupTaskStatMapper.insertBatch(statList);
        }
        return resultMap;
    }

    /**
     * 过滤去重客户列表
     *
     * @param gender         性别
     * @param beginTime      添加开始时间
     * @param endTime        添加结束时间
     * @param selectUserList 员工列表
     * @param tagList        标签列表
     * @return 客户列表
     */
    private List<BaseExternalUserEntity> selectExternalUserIds(String corpId, Integer gender, String beginTime, String endTime, List<String> selectUserList, List<String> tagList) {
        beginTime = DateUtils.parseBeginDay(beginTime);
        endTime = DateUtils.parseEndDay(endTime);
        List<BaseExternalUserEntity> baseExternalUserEntityList = baseMapper.getExternalUserIds(corpId, selectUserList, tagList, gender, beginTime, endTime);
        if (baseExternalUserEntityList == null) {
            return new ArrayList<>();
        }
        return baseExternalUserEntityList;
    }

    /**
     * 异步处理消息推送
     *
     * @param map  Map<key:userId,Value:发送客户集合>
     * @param task 任务详情
     */
    @Async
    public void handleSendMessageAsync(Map<String, Set<String>> map, WePresTagGroupTask task) {
        if (MapUtils.isEmpty(map) || task == null) {
            log.info("异步发送消息入参为空,map:{},task:{}", map, task);
            return;
        }

        if (CustomerTrajectoryEnums.TaskSendType.CROP.getType().equals(task.getSendType())) {
            //使用Set的特性做去重
            Set<String> allExternalUserSet = new HashSet<>();
            for (Set<String> val : map.values()) {
                allExternalUserSet.addAll(val);
            }
            List<String> allExternalUserList = new ArrayList<>(allExternalUserSet);
            this.sendCorpMessage(task, allExternalUserList);
        } else {
            this.sendAgentMessageToEmployee(task, map.keySet());
        }
    }

    /**
     * 推送新任务应用消息给员工
     *
     * @param task        任务信息
     * @param employeeSet 员工集合
     */
    private void sendAgentMessageToEmployee(WePresTagGroupTask task, Set<String> employeeSet) {
        if (task == null || StringUtils.isBlank(task.getCorpId()) || CollectionUtils.isEmpty(employeeSet)) {
            log.warn("推送新任务消息给员工，参数为空");
            return;
        }
        //构造UserId参数  userId1|userId2|userId3
        String toUser = employeeSet.stream().collect(Collectors.joining("|"));
        //获取应用ID
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(task.getCorpId());
        String agentId = validWeCorpAccount.getAgentId();
        String corpId = validWeCorpAccount.getCorpId();
        if (StringUtils.isBlank(agentId)) {
            log.warn("推送新任务消息给员工，应用应用未配置不进行发送!");
            return;
        }

        // 设置文本消息
        TextMessageDTO text = new TextMessageDTO();
        String authorizeRedirectUrl = validWeCorpAccount.getH5DoMainName() + "/#/task";
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode(String.format("%s?corpId=%s&agentId=%s&type=%s", authorizeRedirectUrl, corpId, agentId, CommunityTaskType.TAG.getType()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("获取tag的redirectUrl失败：ex：{}", ExceptionUtils.getStackTrace(e));
        }
        String context = String.format(
                "你有一个新任务，<a href='%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect'>请点击此链接查看</a>",
                authorizeUrl, corpId, redirectUrl, corpId);
        text.setContent(context);

        log.info("推送标签入群新任务消息，任务id:{},发送人数:{}", task.getTaskId(), employeeSet.size());
        //组装调用参数
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        pushDto.setTouser(toUser);
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        messagePushClient.sendMessageToUser(pushDto, agentId, task.getCorpId());
    }

    /**
     * 设置员工信息
     *
     * @param list   {@link List<WePresTagGroupTaskStatVO>}
     * @param corpId 企业id
     * @Description 设置员工部门和员工昵称
     */
    private void setWeUserInfo(List<WePresTagGroupTaskStatVO> list, String corpId) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> userIdList = list.stream()
                .filter(Objects::nonNull)
                .filter(o -> StringUtils.isNotBlank(o.getUserId()))
                .map(WePresTagGroupTaskStatVO::getUserId).collect(Collectors.toList());

        List<WeUserVO> userList = weUserService.listOfUser(corpId, userIdList);
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        Map<String, WeUserVO> userMap = userList.stream().collect(Collectors.toMap(WeUserVO::getUserId, Function.identity(), (key1, key2) -> key2));

        for (WePresTagGroupTaskStatVO wePresTagGroupTaskStatVO : list) {
            if (StringUtils.isNotBlank(wePresTagGroupTaskStatVO.getUserId())) {
                WeUserVO weUserVO = userMap.get(wePresTagGroupTaskStatVO.getUserId());
                if (weUserVO != null) {
                    wePresTagGroupTaskStatVO.setMainDepartmentName(weUserVO.getMainDepartmentName());
                    wePresTagGroupTaskStatVO.setUsername(weUserVO.getUserName());
                }

            }
        }
    }

    /**
     * 设置在群信息
     *
     * @param groupCodeId 群活码
     * @param list        详情集合
     * @param corpId      企业ID
     */
    private void setGroupInfo(Long groupCodeId, List<WePresTagGroupTaskStatVO> list, String corpId) {
        if (groupCodeId == null || CollectionUtils.isEmpty(list) || StringUtils.isBlank(corpId)) {
            return;
        }
        //获取实际群码
        List<WeGroupCodeActual> groupCodeActualList = weGroupCodeService.selectActualList(groupCodeId);
        if (CollectionUtils.isEmpty(groupCodeActualList)) {
            //如果没有实际群则不需要做匹配了
            return;
        }
        Set<String> chatIdSet = groupCodeActualList.stream().filter(Objects::nonNull).map(WeGroupCodeActual::getChatId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(chatIdSet)) {
            //如果没有实际群ID则不需要做匹配了
            return;
        }

        LambdaQueryWrapper<WeGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(WeGroupMember::getChatId, chatIdSet);
        queryWrapper.eq(WeGroupMember::getCorpId, corpId);
        List<WeGroupMember> memberList = weGroupMemberService.list(queryWrapper);
        if (CollectionUtils.isEmpty(chatIdSet)) {
            return;
        }
        //用userid 把每个群的群id存起来
        Map<String, Set<String>> memberMap = memberList.stream().filter(Objects::nonNull)
                .collect(Collectors.groupingBy(WeGroupMember::getUserId, Collectors.mapping(WeGroupMember::getChatId, Collectors.toSet())));

        LambdaQueryWrapper<WeGroup> query = new LambdaQueryWrapper<>();
        query.eq(WeGroup::getCorpId, corpId);
        query.in(WeGroup::getChatId, chatIdSet);
        List<WeGroup> groupList = weGroupService.list(query);
        if (groupList == null) {
            groupList = new ArrayList<>();
        }
        //用群id把群名存起来
        Map<String, String> groupNameMap = groupList.stream().filter(Objects::nonNull).collect(Collectors.toMap(WeGroup::getChatId, WeGroup::getGroupName));

        //组装所在群的群名，同时修正是否在群
        for (WePresTagGroupTaskStatVO wePresTagGroupTaskStatVO : list) {
            //先设置一个默认值
            wePresTagGroupTaskStatVO.setInGroupName("");
            String externalUserid = wePresTagGroupTaskStatVO.getExternalUserid();
            if (StringUtils.isBlank(externalUserid)) {
                continue;
            }
            Set<String> inGroupChatIdSet = memberMap.getOrDefault(externalUserid, new HashSet<>());
            if (CollectionUtils.isEmpty(inGroupChatIdSet)) {
                //如果externalUserid对应的集合是空的代表不在群内
                wePresTagGroupTaskStatVO.setInGroup(false);
                continue;
            }
            List<String> groupNameList = new ArrayList<>();
            for (String chatId : inGroupChatIdSet) {
                if (StringUtils.isBlank(chatId)) {
                    continue;
                }
                String groupName = groupNameMap.getOrDefault(chatId, "");
                if (StringUtils.isBlank(groupName)) {
                    continue;
                }
                groupNameList.add(groupName);
            }
            String groupNameStr = String.join("、", groupNameList);
            wePresTagGroupTaskStatVO.setInGroupName(groupNameStr);
            wePresTagGroupTaskStatVO.setInGroup(true);
        }
    }


}
