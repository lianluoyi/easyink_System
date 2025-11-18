package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.conversation.ConversationArchiveConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.conversation.msgtype.MsgTypeEnum;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.ContactSpeakEnum;
import com.easyink.common.enums.StaffActivateEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.enums.statistics.StatisticsEnum;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.domain.vo.customer.SessionArchiveCustomerVO;
import com.easyink.wecom.domain.vo.statistics.*;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.mapper.form.WeFormCustomerFeedbackMapper;
import com.easyink.wecom.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）(WeUserCustomerMessageStatistics)表服务实现类
 *
 * @author wx
 * @since 2023-02-13 09:32:50
 */
@Service("weUserCustomerMessageStatisticsService")
@RequiredArgsConstructor
@Slf4j
public class WeUserCustomerMessageStatisticsServiceImpl extends ServiceImpl<WeUserCustomerMessageStatisticsMapper, WeUserCustomerMessageStatistics> implements WeUserCustomerMessageStatisticsService {

    private final WeUserBehaviorDataService weUserBehaviorDataService;
    private final WeUserService weUserService;
    private final WeConversationArchiveService weConversationArchiveService;
    private final WeUserMapper weUserMapper;
    private final WeUserCustomerMessageStatisticsMapper weUserCustomerMessageStatisticsMapper;
    private final WeUserBehaviorDataMapper weUserBehaviorDataMapper;
    private final WeFormCustomerFeedbackMapper weFormCustomerFeedbackMapper;
    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeCorpAccountMapper weCorpAccountMapper;
    private final WeDepartmentService weDepartmentService;
    private final WeCustomerMapper weCustomerMapper;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 导出员工服务报表命名
     */
    protected static final String SHEET_NAME="员工服务报表";
    /**
     * 员工是否与客户聊天标识符
     */
    protected static final String EMPTY_CHAT="-1";
    /**
     * 导出客户活跃度报表名称
     */
    protected static final String CUSTOMER_ACTIVITY_REPORT_FORMS = "客户活跃度报表";

    /**
     * 导出客户概况报表-日期维度名称
     */
    protected static final String CUSTOMER_OVERVIEW_DATE_FORMS = "客户概况报表-日期维度";
    /**
     * 导出客户概况报表-部门维度名称
     */
    protected static final String CUSTOMER_OVERVIEW_DEPARTMENT_FORMS = "客户概况报表-部门维度";

    /**
     * 更新历史记录中员工主动发起的会话数
     */
    @Override
    public void updateUserActiveChatCnt() {
        List<WeCorpAccount> weCorpAccounts = weCorpAccountMapper.selectList(new LambdaQueryWrapper<>());
        for (WeCorpAccount weCorpAccount : weCorpAccounts) {
            log.info(">>>>>>>>>>>>开始迁移历史记录中员工主动发起的会话数，corpId:{}", weCorpAccount.getCorpId());
            try {
                // 查询历史日期范围下员工主动发起会话数
                List<WeUserCustomerMessageStatistics> historyDataList = weUserCustomerMessageStatisticsMapper.findHistoryData(weCorpAccount.getCorpId());
                // 对历史日期去重
                List<Date> timeList = historyDataList.stream().distinct().map(item -> item.getSendTime()).collect(Collectors.toList());
                // 查询对应历史日期下的数据
                List<WeUserBehaviorData> updateList = weUserBehaviorDataMapper.selectList(new LambdaQueryWrapper<WeUserBehaviorData>()
                        .eq(WeUserBehaviorData::getCorpId, weCorpAccount.getCorpId()).in(WeUserBehaviorData::getStatTime, timeList));
                // 为对应日期设置员工发起会话数
                for (WeUserCustomerMessageStatistics statistics : historyDataList) {
                    for (WeUserBehaviorData behaviorData : updateList) {
                        if (behaviorData.getStatTime().toString().equals(statistics.getSendTime().toString())
                                && behaviorData.getUserId().equals(statistics.getUserId())
                                && behaviorData.getCorpId().equals(statistics.getCorpId())) {
                            behaviorData.setUserActiveChatCnt(statistics.getUserActiveChatCnt());
                        }
                    }
                }
                weUserBehaviorDataMapper.batchUpdate(updateList);
            } catch (Exception e) {
                log.error(">>>>>>>>>>>>迁移数据异常，corpId:{}，异常原因ex:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
            } finally {
                log.info(">>>>>>>>>>>>迁移历史记录中员工主动发起的会话数结束, corpId:{}", weCorpAccount.getCorpId());
            }
        }
    }

    @Override
    public void getMessageStatistics(String corpId, String time) {
        if (StringUtils.isBlank(corpId)) {
            log.error("corpId不允许为空。");
            return;
        }
        // 获取根员工
        List<WeUser> visibleUser = weUserService.getVisibleUser(corpId);
        visibleUser = visibleUser.stream().filter(it -> StaffActivateEnum.ACTIVE.getCode().equals(it.getIsActivate())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(visibleUser)) {
            log.info("[DataStatisticsTask] 该企业不存在可见的部门和员工,停止执行,corpId:{}", corpId);
            return;
        }
        // 获取前一天的数据
        String yesterday = time;
        String beginTime = DateUtils.parseBeginDay(yesterday);
        String endTime = DateUtils.parseEndDay(yesterday);
        visibleUser.forEach(weUser -> {
            try {
                //根据员工id在会话存档中获取全部对话
                ConversationArchiveQuery archiveQuery = new ConversationArchiveQuery();
                archiveQuery.setFromId(weUser.getUserId());
                archiveQuery.setCorpId(weUser.getCorpId());
                archiveQuery.setBeginTime(yesterday);
                archiveQuery.setEndTime(yesterday);
                int pageNum = 0;
                int pageSize = 100;
                // 员工的全部消息
                List<ConversationArchiveVO> userMessages = new ArrayList<>();
                PageInfo<ConversationArchiveVO> chatList;
                do {
                    pageNum++;
                    chatList = weConversationArchiveService.getChatList(archiveQuery, pageNum, pageSize);
                    if (chatList != null) {
                        userMessages.addAll(chatList.getList());
                    }
                }while (chatList != null && (chatList.getTotal() > ((long) pageNum * pageSize)));
                WeUserBehaviorData userBehaviorData = weUserBehaviorDataService.getOne(new LambdaUpdateWrapper<WeUserBehaviorData>()
                        .eq(WeUserBehaviorData::getStatTime, archiveQuery.getBeginTime())
                        .eq(WeUserBehaviorData::getUserId, weUser.getUserId())
                        .eq(WeUserBehaviorData::getCorpId, weUser.getCorpId())
                        .last(GenConstants.LIMIT_1));
                if (userBehaviorData == null) {
                    return;
                }
                // 处理消息
                if (!CollectionUtils.isEmpty(userMessages)) {
                    Iterator<ConversationArchiveVO> iterator = userMessages.iterator();
                    while (iterator.hasNext()) {
                        ConversationArchiveVO msg = iterator.next();
                        // ture 表示该消息为员工发送或员工接收的消息，将此消息删除
                        if (handleUserMsg(msg.getFromInfo(), msg.getToListInfo())) {
                            iterator.remove();
                        }
                        //过滤群聊数据
                        if (StringUtils.isNotBlank(msg.getRoomId())){
                            iterator.remove();
                        }
                    }
                }
                WeUserBehaviorData totalContactAndLossCnt = weFlowerCustomerRelMapper.getTotalContactAndLossCnt(userBehaviorData.getUserId(), userBehaviorData.getCorpId(), beginTime, endTime);
                List<String> userIdList = new ArrayList<>();
                userIdList.add(weUser.getUserId());
                Integer totalAllContactCnt = weFlowerCustomerRelMapper.getNormalTotalAllContactCnt(corpId, beginTime, endTime, userIdList);
                userBehaviorData.setTotalAllContactCnt(totalAllContactCnt);
                userBehaviorData.setContactTotalCnt(totalContactAndLossCnt.getContactTotalCnt());
                userBehaviorData.setNewCustomerLossCnt(totalContactAndLossCnt.getNewCustomerLossCnt());
                weUserBehaviorDataService.updateById(userBehaviorData);
                statistics(userMessages, weUser, userBehaviorData, yesterday);
            } catch (Exception e) {
                log.error("员工数据拉取失败: corpId:{},userId:【{}】,ex:【{}】", weUser.getCorpId(), weUser.getUserId(), ExceptionUtils.getStackTrace(e));
            }
        });
    }

    /**
     * 处理判断是否为员工之间的消息
     *
     * @param fromInfo 发送者信息
     * @param toListInfo 接收者信息
     * @return True 是为员工互相发送的消息； False：不是为员工互相发送的消息
     */
    public boolean handleUserMsg(JSONObject fromInfo, JSONObject toListInfo){
        if (fromInfo == null || toListInfo == null){
            return false;
        }
        // 是否为员工互相发送的消息
        if (fromInfo.get(WeConstans.IS_USER) != null && toListInfo.get(WeConstans.IS_USER) != null) {
            if (Boolean.FALSE.equals(fromInfo.get(WeConstans.IS_USER)) && Boolean.FALSE.equals(toListInfo.get(WeConstans.IS_USER))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void statistics(List<ConversationArchiveVO> userMessages, WeUser weUser, WeUserBehaviorData userBehaviorData, String time) {
        Date nowDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, time);
        if (CollectionUtils.isEmpty(userMessages) || userBehaviorData == null) {
            // TODO 标识该员工 没有和任何人讲话
            String nullSign = "-1";
            WeUserCustomerMessageStatistics weUserCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            weUserCustomerMessageStatistics.setUserId(weUser.getUserId());
            weUserCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            weUserCustomerMessageStatistics.setExternalUserid(nullSign);
            weUserCustomerMessageStatistics.setSendTime(nowDate);
            this.remove(new LambdaQueryWrapper<WeUserCustomerMessageStatistics>()
                    .eq(WeUserCustomerMessageStatistics::getCorpId, weUser.getCorpId())
                    .eq(WeUserCustomerMessageStatistics::getUserId, weUser.getUserId())
                    .eq(WeUserCustomerMessageStatistics::getSendTime, time));
            this.save(weUserCustomerMessageStatistics);
            return;
        }
        List<WeUserCustomerMessageStatistics> userCustomerMessageStatisticsList = new ArrayList<>();
        // 根据发送者分组 发送者为员工或客户 key : external_userId或userId value:客户 发送/接收 员工的消息 (下面会将客户发送，员工发送分离)
        Map<String, List<ConversationArchiveVO>> customerSendMessageMap = userMessages.stream().collect(Collectors.groupingBy(ConversationArchiveVO::getFrom, LinkedHashMap::new, Collectors.toList()));
        // key : external_userId, value : 客户接受员工的消息
        int firstIndex = 0;
        // 客户接收员工发送消息 key : userId value : 员工发送消息
        Map<String, List<ConversationArchiveVO>> receiveUserMessageMap = customerSendMessageMap.getOrDefault(weUser.getUserId(), new ArrayList<>())
                .stream()
                // 用接收者分组,toList数组中只有一个值,就是客户的externalUserId
                .collect(Collectors.groupingBy(it -> it.getToList().get(firstIndex),
                        LinkedHashMap::new,
                        Collectors.toList()));
        // 移除员工，只剩下客户发送的消息 key : external_userId value:客户 发送消息
        customerSendMessageMap.remove(weUser.getUserId());
        // 当天员工首次给客户发消息，客户在30分钟内回复的客户数
        int thirtyMinReplyCount = 0;
        // 当天新加的客户 并有过通过
        int newCustomerStartContactCnt = 0;
        // 当天会话数
        int chatCnt = 0;
        // 当天员工发起的会话数
        int userActiveChatCnt = 0;
        // 统计客户发送消息
        for (Map.Entry<String, List<ConversationArchiveVO>> entry : customerSendMessageMap.entrySet()) {
            String externalUserId = entry.getKey();
            List<ConversationArchiveVO> customerSendMessageRecords = entry.getValue();
            WeUserCustomerMessageStatistics userCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            userCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            userCustomerMessageStatistics.setUserId(weUser.getUserId());
            userCustomerMessageStatistics.setExternalUserid(externalUserId);
            // 员工对该客户发送的消息数
            userCustomerMessageStatistics.setUserSendMessageCnt(receiveUserMessageMap.getOrDefault(externalUserId, new ArrayList<>()).size());
            // 该客户对员工发送的消息数
            userCustomerMessageStatistics.setExternalUserSendMessageCnt(customerSendMessageRecords.size());
            // 获取添加客户时间
            userCustomerMessageStatistics.setAddTime(getCustomerAddTime(customerSendMessageRecords, false));
            // 发送消息时间
            userCustomerMessageStatistics.setSendTime(nowDate);
            // 构建聊天记录并统计
            buildContactAndSet(weUser, userCustomerMessageStatistics, customerSendMessageRecords, receiveUserMessageMap.getOrDefault(externalUserId, new ArrayList<>()));
            if (userCustomerMessageStatistics.getUserActiveDialogue() != null){
                if (userCustomerMessageStatistics.getUserActiveDialogue()){
                    userActiveChatCnt++;
                }
            }
            // 客户是否在30分钟内回复
            if (Boolean.TRUE.equals(userCustomerMessageStatistics.getRepliedWithinThirtyMinCustomerFlag())) {
                thirtyMinReplyCount ++;
            }
            // 是否为新客户 当天新加的客户 并且条件得话术不只是系统得打招呼消息
            if (isNewCustomer(customerSendMessageRecords, nowDate) && notOnlySystemSayHiMessage(customerSendMessageRecords)) {
                newCustomerStartContactCnt++;
            }
            receiveUserMessageMap.remove(externalUserId);
            userCustomerMessageStatisticsList.add(userCustomerMessageStatistics);
            chatCnt ++;
        }
        // 统计员工发送消息 但 客户未回复消息
        for (Map.Entry<String, List<ConversationArchiveVO>> entry : receiveUserMessageMap.entrySet()) {
            String externalUserId = entry.getKey();
            List<ConversationArchiveVO> userSendMessageRecords = entry.getValue();
            WeUserCustomerMessageStatistics userCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            userCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            userCustomerMessageStatistics.setUserId(weUser.getUserId());
            userCustomerMessageStatistics.setExternalUserid(externalUserId);
            // 员工对该客户发送的消息数
            userCustomerMessageStatistics.setUserSendMessageCnt(userSendMessageRecords.size());
            // 获取添加客户时间
            if (!Objects.isNull(getCustomerAddTime(userSendMessageRecords, true))) {
                userCustomerMessageStatistics.setAddTime(getCustomerAddTime(userSendMessageRecords, true));
            }
            // 发送消息时间
            userCustomerMessageStatistics.setSendTime(nowDate);
            //
            buildContactAndSet(weUser, userCustomerMessageStatistics, null, entry.getValue());
            if (userCustomerMessageStatistics.getUserActiveDialogue() != null) {
                if (userCustomerMessageStatistics.getUserActiveDialogue()) {
                    userActiveChatCnt++;
                }
            }
            userCustomerMessageStatisticsList.add(userCustomerMessageStatistics);
            chatCnt++;
        }
        // 删除员工当天原来的统计数据
        this.remove(new LambdaQueryWrapper<WeUserCustomerMessageStatistics>()
                .eq(WeUserCustomerMessageStatistics::getCorpId, weUser.getCorpId())
                .eq(WeUserCustomerMessageStatistics::getUserId, weUser.getUserId())
                .eq(WeUserCustomerMessageStatistics::getSendTime, time));
        this.saveBatch(userCustomerMessageStatisticsList);
        // 保存用户行为
        saveUserBehaviorDate(userBehaviorData, thirtyMinReplyCount, newCustomerStartContactCnt, chatCnt, userActiveChatCnt);
    }

    /**
     * 不包含系统的打招呼消息
     * @param customerSendMessageRecords 客户发送的消息记录
     * @return 是否只包含客户发送的系统的打招呼消息
     */
    private boolean notOnlySystemSayHiMessage(List<ConversationArchiveVO> customerSendMessageRecords) {
        return customerSendMessageRecords.stream().anyMatch(it ->
                // 不是文本消息 或者 文本消息不包含打招呼消息
                !MsgTypeEnum.TEXT.getType().equals(it.getMsgType()) || !ConversationArchiveConstants.isSystemSayHiMessage(it.getText().getContent())
        );

    }

    /**
     * 保存用户行为数据
     *
     * @param userBehaviorData           {@link WeUserBehaviorData}
     * @param thirtyMinReplyCount        当天员工首次给客户发消息，客户在30分钟内回复的客户数
     * @param newCustomerStartContactCnt 当天新增客户中与员工对话过的人数
     * @param chatCnt                    当天会话数-不区分是否为员工主动发起
     * @param userActiveChatCnt          当天员工主动发起的会话数量
     */
    private void saveUserBehaviorDate(WeUserBehaviorData userBehaviorData, Integer thirtyMinReplyCount, Integer newCustomerStartContactCnt, Integer chatCnt, Integer userActiveChatCnt) {
        if (userBehaviorData == null) {
            return;
        }
        userBehaviorData.setRepliedWithinThirtyMinCustomerCnt(thirtyMinReplyCount);
        userBehaviorData.setNewContactSpeakCnt(newCustomerStartContactCnt);
        userBehaviorData.setAllChatCnt(chatCnt);
        userBehaviorData.setUserActiveChatCnt(userActiveChatCnt);
        weUserBehaviorDataService.updateById(userBehaviorData);
    }

    @Override
    @DataScope
    public CustomerOverviewVO getCustomerOverViewOfTotal(StatisticsDTO dto) {
        if (!isHaveDataScope(dto)) {
            return null;
        }
        if (dto.getEndTime() == null) {
            dto.setEndTime(DateUtils.parseBeginDay(DateUtils.dateTime(DateUtils.getYesterday(new Date()))));
        }
        CustomerOverviewVO resultData = weUserBehaviorDataMapper.getCustomerOverViewOfTotal(dto);
        resultData.setCurrentNewCustomerCnt(weFlowerCustomerRelMapper.getCurrentNewCustomerCntByDataScope(dto));
        return resultData;
    }

    /**
     * 获取客户概况-员工情况
     *
     * @param dto {@link StatisticsDTO}
     * @return {@link CustomerOverviewVO}
     */
    @Override
    @DataScope
    public List<CustomerOverviewVO> getCustomerOverViewOfUser(CustomerOverviewDTO dto) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (dto.getEndTime() == null) {
            dto.setEndTime(DateUtils.parseBeginDay(DateUtils.dateTime(DateUtils.getYesterday(new Date()))));
        }
        // 获取结果列表
        List<CustomerOverviewVO> resultList = weUserBehaviorDataMapper.getCustomerOverViewOfUser(dto);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        List<String> userIds = resultList.stream().map(CustomerOverviewVO::getUserId).collect(Collectors.toList());
        // 获取根据开始，结束时间和员工ID，获取截止时间下的有效客户数
        List<CustomerOverviewVO> currNewCustomerCntByUser = weFlowerCustomerRelMapper.getCurrNewCustomerCntByUser(dto.getCorpId(), dto.getBeginTime(), dto.getEndTime(), userIds);
        if (CollectionUtils.isEmpty(currNewCustomerCntByUser)) {
            return resultList;
        }
        for (CustomerOverviewVO resultModel : resultList) {
            for (CustomerOverviewVO overviewVO : currNewCustomerCntByUser) {
                if (resultModel.getUserId().equals(overviewVO.getUserId())) {
                    // 为对应的员工设置截止当前时间的有效客户数
                    resultModel.setCurrentNewCustomerCnt(overviewVO.getCurrentNewCustomerCnt());
                }
            }
        }
        // 对新客留存率进行排序
        sortNewContactRetainRate(resultList, dto.getNewContactRetentionRateSort());

        return resultList;
    }

    /**
     * 获取客户概况-部门情况
     *
     * @param dto {@link CustomerOverviewDTO}
     * @return {@link CustomerOverviewDepartmentVO}
     */
    @Override
    @DataScope
    public List<CustomerOverviewDepartmentVO> getCustomerOverViewOfDepartment(CustomerOverviewDTO dto) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (dto.getEndTime() == null) {
            dto.setEndTime(DateUtils.parseBeginDay(DateUtils.dateTime(DateUtils.getYesterday(new Date()))));
        }
        // 获取结果列表
        List<CustomerOverviewDepartmentVO> resultList = weUserBehaviorDataMapper.getCustomerOverViewOfDepartment(dto);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        
        // 获取部门ID列表
        List<String> departmentIds = resultList.stream().map(CustomerOverviewDepartmentVO::getDepartmentId).collect(Collectors.toList());
        
        // 获取根据开始，结束时间和部门ID，获取截止时间下的有效客户数
        List<CustomerOverviewDepartmentVO> currNewCustomerCntByDepartment = getCurrNewCustomerCntByDepartment(dto.getCorpId(), dto.getBeginTime(), dto.getEndTime(), departmentIds);
        
        if (CollectionUtils.isNotEmpty(currNewCustomerCntByDepartment)) {
            for (CustomerOverviewDepartmentVO resultModel : resultList) {
                for (CustomerOverviewDepartmentVO overviewVO : currNewCustomerCntByDepartment) {
                    if (resultModel.getDepartmentId().equals(overviewVO.getDepartmentId())) {
                        // 为对应的部门设置截止当前时间的有效客户数
                        resultModel.setCurrentNewCustomerCnt(overviewVO.getCurrentNewCustomerCnt());
                        // 设置新客留存数（与有效客户数相同）
                        resultModel.setNewCustomerRetentionCnt(overviewVO.getCurrentNewCustomerCnt());
                    }
                }
            }
        }
        
        return resultList;
    }

    /**
     * 获取根据开始，结束时间和部门ID，获取截止时间下的有效客户数
     *
     * @param corpId        企业ID
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param departmentIds 部门ID列表
     * @return 部门维度的有效客户数列表
     */
    private List<CustomerOverviewDepartmentVO> getCurrNewCustomerCntByDepartment(String corpId, String beginTime, String endTime, List<String> departmentIds) {
        // 获取部门下的所有员工ID
        List<String> userIds = weDepartmentService.getDataScopeUserIdList(departmentIds, null, corpId);
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        
        // 获取员工维度的有效客户数
        List<CustomerOverviewVO> userCustomerCnt = weFlowerCustomerRelMapper.getCurrNewCustomerCntByUser(corpId, beginTime, endTime, userIds);
        if (CollectionUtils.isEmpty(userCustomerCnt)) {
            return new ArrayList<>();
        }
        
        // 按部门聚合统计
        Map<String, Integer> departmentCustomerCntMap = new HashMap<>();
        for (CustomerOverviewVO userVO : userCustomerCnt) {
            // 获取员工所属部门
            Long departmentId = weDepartmentService.selectDepartmentIdByUserId(userVO.getUserId(), corpId);
            if (departmentId != null) {
                String deptId = String.valueOf(departmentId);
                departmentCustomerCntMap.put(deptId, 
                    departmentCustomerCntMap.getOrDefault(deptId, 0) + userVO.getCurrentNewCustomerCnt());
            }
        }
        
        // 转换为VO列表
        List<CustomerOverviewDepartmentVO> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : departmentCustomerCntMap.entrySet()) {
            CustomerOverviewDepartmentVO vo = new CustomerOverviewDepartmentVO();
            vo.setDepartmentId(entry.getKey());
            vo.setCurrentNewCustomerCnt(entry.getValue());
            result.add(vo);
        }
        
        return result;
    }

    /**
     * 导出客户概况-数据总览-部门维度
     *
     * @param dto   {@link CustomerOverviewDTO}
     * @return 结果
     */
    @Override
    public AjaxResult exportCustomerOverViewOfDepartment(CustomerOverviewDTO dto) {
        List<CustomerOverviewDepartmentVO> list = getCustomerOverViewOfDepartment(dto);
        // 绑定导出数据
        list.forEach(CustomerOverviewDepartmentVO::bindExportData);
        ExcelUtil<CustomerOverviewDepartmentVO> util = new ExcelUtil<>(CustomerOverviewDepartmentVO.class);
        return util.exportExcel(list, CUSTOMER_OVERVIEW_DEPARTMENT_FORMS);
    }

    /**
     * 对新客留存率进行排序
     *
     * @param resultList 结果列表
     * @param newContactRetentionRateSort 新客留存率排序方式 ASC：正序， DESC：倒序
     */
    private void sortNewContactRetainRate(List<CustomerOverviewVO> resultList, String newContactRetentionRateSort) {
        if (newContactRetentionRateSort == null || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        // 对新客留存率单独排序，因String类型的排序，会导致100%比90.XX%小，所以转换为Double类型排序
        resultList.sort(Comparator.comparing(item -> {
            // 如果值为"-"，无法排序，使用自然排序器，直接返回null
            if (Constants.EMPTY_RETAIN_RATE_VALUE.equals(item.getNewContactRetentionRate())) {
                return null;
            }
            // 如果是倒序排序，则对转换的值取反
            if (GenConstants.DESC.equals(newContactRetentionRateSort)) {
                return -Double.parseDouble(item.getNewContactRetentionRate());
            }
            return Double.parseDouble(item.getNewContactRetentionRate());
        }, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * 导出客户概况-数据总览-日期维度
     *
     * @param dto   {@link CustomerOverviewDTO}
     * @return 结果
     */
    @Override
    @DataScope
    public AjaxResult exportCustomerOverViewOfDate(CustomerOverviewDTO dto) {
        List<CustomerOverviewDateVO> list = getCustomerOverViewOfDate(dto, false);
        // 导出
        list.forEach(CustomerOverviewDateVO::bindExportData);
        ExcelUtil<CustomerOverviewDateVO> util = new ExcelUtil<>(CustomerOverviewDateVO.class);
        return util.exportExcel(list, CUSTOMER_OVERVIEW_DATE_FORMS);
    }

    /**
     * 获取客户概况-日期维度
     *
     * @param dto      {@link StatisticsDTO}
     * @param pageFlag 是否分页，true 是， false 否
     * @return {@link CustomerOverviewVO}
     */
    @Override
    @DataScope
    public List<CustomerOverviewDateVO> getCustomerOverViewOfDate(CustomerOverviewDTO dto, Boolean pageFlag) {
        if (!isHaveDataScope(dto) || dto == null) {
            return new ArrayList<>();
        }
        if (dto.getEndTime() == null) {
            dto.setEndTime(DateUtils.parseBeginDay(DateUtils.dateTime(DateUtils.getYesterday(new Date()))));
        }
        //转换获取所需查询类
        UserServiceDTO userServiceDTO= new UserServiceDTO();
        BeanUtils.copyProperties(dto,userServiceDTO);
        // 查询时间段内的所有数据
        List<WeUserBehaviorData> weUserBehaviorDataList = weUserBehaviorDataMapper.getCustomerOverViewOfDate(dto);
        // 获取数据权限下的员工id列表
        List<String> userIdList = weDepartmentService.getDataScopeUserIdList(dto.getDepartmentIds(), dto.getUserIds(), dto.getCorpId());
        // 根据分页参数和时间范围，获取日期时间
        List<Date> dates = getDatesByPage(dto, pageFlag);
        // 最终需要返回的数据VO列表
        CopyOnWriteArrayList<CustomerOverviewDateVO> resultList = new CopyOnWriteArrayList<>();
        // 执行任务列表
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (Date date : dates) {
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                try {
                    // 组装返回数据
                    CustomerOverviewDateVO customerOverviewDateVO = this.buildVOData(weUserBehaviorDataList, date, dto.getCorpId(), userIdList);
                    resultList.add(customerOverviewDateVO);
                } catch (Exception e) {
                    log.error("[获取客户概况-日期维度] 出现异常，corpId:{}，异常原因ex:{}", dto.getCorpId(), ExceptionUtils.getStackTrace(e));
                }
            }, threadPoolTaskExecutor);
            completableFutures.add(voidCompletableFuture);
        }
        // 等待所有的CompletableFuture执行完成
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        if (StatisticsEnum.CustomerOverviewSortTypeEnum.NEW_CONTACT_RETENTION_RATE_SORT.getSortName().equals(dto.getSortName())) {
            // 新客留存率单独排序
            sortDateNewContactRetainRate(resultList, dto.getSortType());
        } else {
            // 根据排序规则进行排序
            StatisticsEnum.CustomerOverviewSortTypeEnum.sort(dto, resultList);
        }
        return resultList;
    }

    /**
     * 组装数据
     *
     * @param weUserBehaviorDataList {@link List<WeUserBehaviorData>}
     * @param date                   日期，格式{@link Date}
     * @param corpId                 企业id
     * @param userIdList             员工id列表
     * @return {@link CustomerOverviewDateVO}
     */
    private CustomerOverviewDateVO buildVOData(List<WeUserBehaviorData> weUserBehaviorDataList, Date date, String corpId, List<String> userIdList) {
        CustomerOverviewDateVO customerOverviewDateVO = new CustomerOverviewDateVO(DateUtils.dateTime(date));
        if (CollectionUtils.isEmpty(weUserBehaviorDataList) || date == null || StringUtils.isBlank(corpId)) {
            return customerOverviewDateVO;
        }
        String beginTime = DateUtils.parseBeginDay(DateUtils.dateTime(date));
        String endTime = DateUtils.parseEndDay(DateUtils.dateTime(date));
        // 获取有进行对话的员工
        List<UserServiceTimeDTO> userServiceTimeDTOList = weUserCustomerMessageStatisticsMapper.getFilterOfUser(corpId, userIdList, beginTime, endTime);
        for (WeUserBehaviorData weUserBehaviorData : weUserBehaviorDataList) {
            String judgeTime=DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, weUserBehaviorData.getStatTime());
            // 判断是否有对话
            if (DateUtils.dateTime(weUserBehaviorData.getStatTime()).equals(DateUtils.dateTime(date))) {
                int chatCnt = 0;
                if (CollectionUtils.isNotEmpty(userServiceTimeDTOList)) {
                    // 统计出员工在对应日期下主动发起会话的数量
                    long count = userServiceTimeDTOList.stream()
                            .filter(userServiceTimeDTO -> Objects.equals(userServiceTimeDTO.getUserId(), weUserBehaviorData.getUserId())
                                    && Objects.equals(judgeTime, userServiceTimeDTO.getSendTime())
                                    && ContactSpeakEnum.USER.getCode().equals(userServiceTimeDTO.getUserActiveDialogue()))
                            .count();
                    chatCnt = (int) count;
                }
                // 原始数据处理
                customerOverviewDateVO.handleAddData(weUserBehaviorData.getAllChatCnt(),
                        weUserBehaviorData.getContactTotalCnt(),
                        weUserBehaviorData.getNegativeFeedbackCnt(),
                        weUserBehaviorData.getNewCustomerLossCnt(),
                        weUserBehaviorData.getNewContactCnt(),
                        weUserBehaviorData.getNewContactSpeakCnt(),
                        weUserBehaviorData.getRepliedWithinThirtyMinCustomerCnt()
                        , chatCnt, weUserBehaviorData.getTotalAllContactCnt());
            }
        }
        // 获取日期维度下，截止当前时间的有效客户数
        CustomerOverviewVO customerCnt = weFlowerCustomerRelMapper.getCurrNewCustomerCnt(corpId, userIdList, beginTime, endTime);
        if (customerCnt == null) {
            return customerOverviewDateVO;
        }
        if (customerOverviewDateVO.getXTime().equals(customerCnt.getXTime())) {
                // 为对应日期设置截止日期下的有效客户数
                customerOverviewDateVO.setCurrentNewCustomerCnt(customerCnt.getCurrentNewCustomerCnt());
            }
        return customerOverviewDateVO;
    }

    /**
     * 根据分页参数和时间范围，获取日期时间
     *
     * @param dto      {@link CustomerOverviewDTO}
     * @param pageFlag 是否分页，true 是， false 否
     * @return 日期范围
     */
    private List<Date> getDatesByPage(CustomerOverviewDTO dto, Boolean pageFlag) {
        if (dto == null || StringUtils.isAnyBlank(dto.getBeginTime(), dto.getEndTime()) || dto.getPageNum() == null || dto.getPageSize() == null || pageFlag == null) {
            return Collections.emptyList();
        }
        // 获取时间范围内的所有日期
        Date beginDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getBeginTime());
        Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getEndTime());
        List<Date> dates = DateUtils.findDates(beginDate, endDate);
        // 排序条件为空，且需要分页，表示使用默认排序，按照时间倒序截取分页
        if (dto.getSortType() == null && pageFlag) {
        // 设置日期范围下的数据总数
        dto.setTotal((long) dates.size());
        // 获取截取起始位置
        int startIndex = PageInfoUtil.getStartIndex(dto.getPageNum(), dto.getPageSize());
        // 获取截取结束位置
        int endIndex = Math.min(startIndex + dto.getPageSize(), dates.size());
            // 默认按照时间倒序查询
            dates.sort(Comparator.comparing(Date::getTime).reversed());
            return dates.subList(startIndex, endIndex);
        }
        // 排序条件不为空，统计所有日期范围的数据，再进行排序返回
        return dates;
    }

    /**
     * 对新客留存率进行排序
     *
     * @param resultList 结果列表
     * @param sortType 新客留存率排序方式 ASC：正序， DESC：倒序
     */
    private void sortDateNewContactRetainRate(List<CustomerOverviewDateVO> resultList, String sortType) {
        if (sortType == null || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        // 对新客留存率单独排序，因String类型的排序，会导致100%比90.XX%小，所以转换为Double类型排序
        resultList.sort(Comparator.comparing(item -> {
            // 如果值为"-"，无法排序，使用自然排序器，直接返回null
            if (Constants.EMPTY_RETAIN_RATE_VALUE.equals(item.getNewContactRetentionRate())) {
                return null;
            }
            // 如果是倒序排序，则对转换的值取反
            if (GenConstants.DESC.equals(sortType)) {
                return -Double.parseDouble(item.getNewContactRetentionRate());
            }
            return Double.parseDouble(item.getNewContactRetentionRate());
        }, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage(StatisticsDTO dto) {
        if (dto == null) {
            return;
        }
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        if (com.easyink.common.utils.StringUtils.isNotNull(pageNum) && com.easyink.common.utils.StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize);
        }
    }

    @Override
    @DataScope
    public List<CustomerActivityOfDateVO> getCustomerActivityOfDate(CustomerActivityDTO dto, Boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        // 根据分页参数、是否分页，获取日期范围
        List<Date> dates = getDatesByPage(dto, pageFlag);
        if (CollectionUtils.isEmpty(dates)) {
            return Collections.emptyList();
        }
        // 结果列表
        List<CustomerActivityOfDateVO> resultVOList = new ArrayList<>();
        // 初始化日期范围内的列表数据
        for (Date date : dates) {
            resultVOList.add(new CustomerActivityOfDateVO(date));
        }
        // 获取查询条件下的员工id列表
        List<String> userIdList = weDepartmentService.getDataScopeUserIdList(dto.getDepartmentIds(), dto.getUserIds(), dto.getCorpId());
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (CustomerActivityOfDateVO customerActivityOfDateVO : resultVOList) {
            CompletableFuture<Void> buildDataFuture = CompletableFuture.runAsync(() -> {
                try {
                    buildCustomerActivityOfDateVOData(dto.clone(), customerActivityOfDateVO, userIdList);
                } catch (Exception e) {
                    log.info("[客户活跃度-日期维度] 处理单个日期维度数据异常，异常原因ex：{}，日期：{}，corpId：{}", ExceptionUtils.getStackTrace(e), customerActivityOfDateVO.getTime(), dto.getCorpId());
                }
            }, threadPoolTaskExecutor);
            futureList.add(buildDataFuture);
        }
        // 等待所有任务完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        return resultVOList;
    }

    /**
     * 构建客户活跃度-日期维度数据
     *
     * @param dto                      {@link CustomerActivityDTO}
     * @param customerActivityOfDateVO {@link CustomerActivityOfDateVO}
     * @param userIdList               员工id列表
     */
    private void buildCustomerActivityOfDateVOData(CustomerActivityDTO dto, CustomerActivityOfDateVO customerActivityOfDateVO, List<String> userIdList) {
        if (dto == null || customerActivityOfDateVO == null || customerActivityOfDateVO.getTime() == null) {
            return;
        }
        // 转换查询时间
        String searchTime = DateUtils.dateTime(customerActivityOfDateVO.getTime());
        // 设置查询时间
        dto.setSendStartTime(searchTime);
        dto.setSendEndTime(searchTime);
        // 查询当前时间下列表数据
        CustomerActivityOfDateVO dateData = weUserCustomerMessageStatisticsMapper.getCustomerActivityOfDate(dto, userIdList);
        // 数据不为空，设置数据
        if (dateData != null) {
            customerActivityOfDateVO.setUserSendMessageCnt(dateData.getUserSendMessageCnt());
            customerActivityOfDateVO.setCustomerSendMessageCnt(dateData.getCustomerSendMessageCnt());
            customerActivityOfDateVO.setChatCustomerCnt(dateData.getChatCustomerCnt());
        }
    }

    /**
     * 根据分页参数和时间范围，获取日期时间
     *
     * @param dto {@link CustomerOverviewDTO}
     * @param isPageFlag 是否需要分页，true 是, false 不是
     * @return 日期范围
     */
    private List<Date> getDatesByPage(CustomerActivityDTO dto, Boolean isPageFlag) {
        if (dto == null || StringUtils.isAnyBlank(dto.getSendStartTime(), dto.getSendEndTime()) || dto.getPageNum() == null || dto.getPageSize() == null || isPageFlag == null) {
            return Collections.emptyList();
        }
        // 获取时间范围内的所有日期
        Date beginDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getSendStartTime());
        Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getSendEndTime());
        List<Date> dates = DateUtils.findDates(beginDate, endDate);
        // 需要分页，根据分页参数截取
        if (isPageFlag) {
            // 获取截取起始位置
            int startIndex = PageInfoUtil.getStartIndex(dto.getPageNum(), dto.getPageSize());
            // 获取截取结束位置
            int endIndex = Math.min(startIndex + dto.getPageSize(), dates.size());
            // 设置日期范围下的数据总数
            dto.setTotal((long) dates.size());
            return dates.subList(startIndex, endIndex);
        }
        // 不需要分页，统计所有日期范围的数据返回
        return dates;
    }

    @Override
    @DataScope
    public List<SendMessageCntVO> getCustomerActivityOfDateTrend(CustomerActivityDTO dto) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        return weUserCustomerMessageStatisticsMapper.getCustomerActivityOfDateTrend(dto);
    }

    @Override
    @DataScope
    public List<CustomerActivityOfUserVO> getCustomerActivityOfUser(CustomerActivityDTO dto, Boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        String corpId = dto.getCorpId();
        // 获取查询条件下的userid列表
        List<String> userIdList = weDepartmentService.getDataScopeUserIdList(dto.getDepartmentIds(), dto.getUserIds(), corpId);
        // 获取员工维度列表
        List<CustomerActivityOfUserVO> resultVOList = weUserCustomerMessageStatisticsMapper.getCustomerActivityOfUser(dto, userIdList);
        // 补充客户活跃度-员工维度列表员工信息
        supplyCustomerActivityOfUserVOInfo(corpId, resultVOList);
        return resultVOList;
    }

    /**
     * 补充客户活跃度-员工维度列表员工信息
     *
     * @param corpId 企业id
     * @param list   客户活跃度-员工维度列表 {@link List<CustomerActivityOfUserVO>}
     */
    private void supplyCustomerActivityOfUserVOInfo(String corpId, List<CustomerActivityOfUserVO> list) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> userIdList = list.stream().map(CustomerActivityOfUserVO::getUserId).collect(Collectors.toList());
        List<WeUserVO> weUserVOS = weUserMapper.selectWeUserInfoByUserIdList(userIdList, corpId);
        for (CustomerActivityOfUserVO customerActivityOfUserVO : list) {
            for (WeUserVO weUserVO : weUserVOS) {
                if (customerActivityOfUserVO.getUserId().equals(weUserVO.getUserId())) {
                    customerActivityOfUserVO.setUserName(weUserVO.getUserName());
                    customerActivityOfUserVO.setDepartmentName(weUserVO.getMainDepartmentName());
                    customerActivityOfUserVO.setUserHeadImage(weUserVO.getHeadImageUrl());
                }
            }
        }
    }

    @Override
    @DataScope
    public List<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(CustomerActivityDTO dto, boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        String corpId = dto.getCorpId();
        // 获取查询条件下的userid列表
        List<String> userIdList = weDepartmentService.getDataScopeUserIdList(dto.getDepartmentIds(), dto.getUserIds(), corpId);
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        // 查询客户维度列表
        List<CustomerActivityOfCustomerVO> resultVOList = weUserCustomerMessageStatisticsMapper.getCustomerActivityOfCustomer(dto, userIdList);
        if (CollectionUtils.isEmpty(resultVOList)) {
            return Collections.emptyList();
        }
        // 补充客户活跃度-客户维度列表客户信息
        CompletableFuture<Void> customerInfoFuture = CompletableFuture.runAsync(() -> {
            try {
                supplyCustomerActivityOfCustomerInfo(corpId, resultVOList);
            } catch (Exception e) {
                log.info("[客户活跃度-客户维度] 补充客户信息异常ex：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        // 补充客户活跃度-客户维度列表员工信息
        CompletableFuture<Void> userInfoFuture = CompletableFuture.runAsync(() -> {
            try {
                supplyCustomerActivityOfCustomerVOUserInfo(corpId, resultVOList);
            } catch (Exception e) {
                log.info("[客户活跃度-客户维度] 补充员工信息异常ex：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        // 等待所有任务完成
        CompletableFuture.allOf(customerInfoFuture, userInfoFuture).join();
        return resultVOList;
    }

    /**
     * 补充客户活跃度-客户维度列表员工信息
     *
     * @param corpId 企业id
     * @param list   客户活跃度-客户维度列表 {@link List<CustomerActivityOfCustomerVO>}
     */
    private void supplyCustomerActivityOfCustomerVOUserInfo(String corpId, List<CustomerActivityOfCustomerVO> list) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> userIdList = list.stream().map(CustomerActivityOfCustomerVO::getUserId).collect(Collectors.toList());
        List<WeUserVO> weUserVOS = weUserMapper.selectWeUserInfoByUserIdList(userIdList, corpId);
        for (CustomerActivityOfCustomerVO customerActivityOfCustomerVO : list) {
            for (WeUserVO weUserVO : weUserVOS) {
                if (customerActivityOfCustomerVO.getUserId().equals(weUserVO.getUserId())) {
                    customerActivityOfCustomerVO.setUserName(weUserVO.getUserName());
                    customerActivityOfCustomerVO.setDepartmentName(weUserVO.getMainDepartmentName());
                }
            }
        }
    }

    /**
     * 补充客户活跃度-客户维度列表客户信息
     *
     * @param corpId 企业id
     * @param list   客户活跃度-客户维度列表 {@link List<CustomerActivityOfCustomerVO>}
     */
    private void supplyCustomerActivityOfCustomerInfo(String corpId, List<CustomerActivityOfCustomerVO> list) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> externalUseridList = list.stream().map(CustomerActivityOfCustomerVO::getExternalUserId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(externalUseridList)) {
            return;
        }
        List<SessionArchiveCustomerVO> customerInfoList = weCustomerMapper.selectWeCustomerListDistinctV2(corpId, externalUseridList);
        for (CustomerActivityOfCustomerVO customerActivityOfCustomerVO : list) {
            for (SessionArchiveCustomerVO sessionArchiveCustomerVO : customerInfoList) {
                if (customerActivityOfCustomerVO.getExternalUserId().equals(sessionArchiveCustomerVO.getExternalUserid())) {
                    customerActivityOfCustomerVO.setExternalUserName(sessionArchiveCustomerVO.getName());
                    customerActivityOfCustomerVO.setExternalUserHeadImage(sessionArchiveCustomerVO.getAvatar());
                }
            }
        }
    }

    @Override
    @DataScope
    public List<CustomerActivityOfCustomerVO> getCustomerActivityOfUserDetail(CustomerActivityUserDetailDTO dto) {
        startPage(dto);
        List<String> userIds = new ArrayList<>();
        userIds.add(dto.getUserId());
        dto.setUserIds(userIds);
        return weUserCustomerMessageStatisticsMapper.getCustomerActivityOfUserDetail(dto);
    }

    @Override
    @DataScope
    public UserServiceVO getUserServiceOfTotal(StatisticsDTO dto) {
        if (!isHaveDataScope(dto)) {
            return null;
        }
        UserServiceVO userServiceOfTotal = weUserCustomerMessageStatisticsMapper.getUserServiceOfTotal(dto);
        bindGoodReview(dto, userServiceOfTotal);
        return userServiceOfTotal;
    }

    /**
     * 绑定客户好评率
     *
     * @param dto                   {@link StatisticsDTO}
     * @param userServiceOfTotal    {@link UserServiceVO}
     */
    private void bindGoodReview(StatisticsDTO dto, UserServiceVO userServiceOfTotal) {
        if (dto == null || userServiceOfTotal == null) {
            return;
        }
        GoodCommitDTO goodCommitDTO = new GoodCommitDTO(dto);
        List<UserGoodReviewDTO> userGoodReviews = weFormCustomerFeedbackMapper.selectGoodReviews(goodCommitDTO);
        int goodReviewScore = 0;
        int goodReviewNum = 0;
        for (UserGoodReviewDTO it : userGoodReviews) {
            goodReviewNum += it.getNum();
            goodReviewScore += it.getScore();
        }
        userServiceOfTotal.setNum(goodReviewNum);
        userServiceOfTotal.setScore(goodReviewScore);
    }

    @Override
    @DataScope
    public PageInfo<UserServiceVO> getUserServiceOfUser(UserServiceDTO dto) {
        List<UserServiceVO> userServiceVOS=getUserServiceOfUserMain(dto);
        //返回分页后的数据
        return list2PageInfo(userServiceVOS,dto.getPageNum(),dto.getPageSize());
    }

    /**
     * 获取需要的员工数据
     * @param dto 查询条件
     * @return
     */
    @DataScope
    public List<UserServiceVO> getUserServiceOfUserMain(UserServiceDTO dto ){
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        GoodCommitDTO goodCommitDTO=new GoodCommitDTO(dto);
        List<UserServiceVO> userServiceVOS=weUserCustomerMessageStatisticsMapper.getUserServiceOfUser(dto);
        fillParentDepartmentName(userServiceVOS, dto.getCorpId());
        List<UserGoodReviewDTO> userGoodReviews = weFormCustomerFeedbackMapper.selsectGoodReviewsForPerson(goodCommitDTO);
        //进行好评率处理
        if (!CollectionUtils.isEmpty(userServiceVOS)&&!CollectionUtils.isEmpty(userGoodReviews)){
            handleGoodReviews(userGoodReviews,userServiceVOS,dto);
        }
        return userServiceVOS;
    }

    /**
     * 处理上级部门名称
     * @param userServiceVOS
     * @param corpId
     */
    private void fillParentDepartmentName(List<UserServiceVO> userServiceVOS, String corpId) {
        // 构建完整的部门路径（需要查询所有相关部门）
        Map<Long, WeDepartment> departmentMap = weDepartmentService.list(
                new LambdaQueryWrapper<WeDepartment>().eq(WeDepartment::getCorpId, corpId)
        ).stream().collect(Collectors.toMap(WeDepartment::getId, dept -> dept));
        for (UserServiceVO userServiceVO : userServiceVOS) {
            userServiceVO.setParentDepartmentName(weDepartmentService.buildParentDepartmentNames(userServiceVO.getUserId(), departmentMap, corpId, "/", true));
        }
    }

    /**
     * 进行手动分页
     *
     * @param arrayList 待分页数据
     * @param pageNum 页数
     * @param pageSize 页面大小
     * @return
     */
    public static PageInfo<UserServiceVO> list2PageInfo(List<UserServiceVO> arrayList, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        int pageStart = pageNum == 1 ? 0 : (pageNum - 1) * pageSize;
        int pageEnd = arrayList.size() < pageSize * pageNum ? arrayList.size() : pageSize * pageNum;
        List<UserServiceVO> pageResult = new LinkedList<>();
        if (arrayList.size() > pageStart) {
            pageResult = arrayList.subList(pageStart, pageEnd);
        }
        PageInfo<UserServiceVO> pageInfo = new PageInfo<>(pageResult);
        //获取PageInfo其他参数
        pageInfo.setTotal(arrayList.size());
        int endRow = pageInfo.getEndRow() == 0 ? 0 : (int) ((pageNum - 1) * pageSize + pageInfo.getEndRow() + 1);
        pageInfo.setEndRow(endRow);
        boolean hasNextPage = arrayList.size() > pageSize * pageNum;
        pageInfo.setHasNextPage(hasNextPage);
        boolean hasPreviousPage = pageNum == 1 ? false : true;
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setIsFirstPage(!hasPreviousPage);
        boolean isLastPage = arrayList.size() > pageSize * (pageNum - 1) && arrayList.size() <= pageSize * pageNum;
        pageInfo.setIsLastPage(isLastPage);
        int pages = arrayList.size() % pageSize == 0 ? arrayList.size() / pageSize : (arrayList.size() / pageSize) + 1;
        pageInfo.setNavigateLastPage(pages);
        int[] navigatePageNums = new int[pages];
        for (int i = 1; i < pages; i++) {
            navigatePageNums[i - 1] = i;
        }
        pageInfo.setNavigatepageNums(navigatePageNums);
        int nextPage = pageNum < pages ? pageNum + 1 : 0;
        pageInfo.setNextPage(nextPage);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages(pages);
        pageInfo.setPrePage(pageNum - 1);
        pageInfo.setSize(pageInfo.getList().size());
        int starRow = arrayList.size() < pageSize * pageNum ? 1 + pageSize * (pageNum - 1) : 0;
        pageInfo.setStartRow(starRow);
        return pageInfo;
    }
    /**
     * 处理好评率
     *
     * @param userGoodReviews 好评列表
     * @param userServiceVOS 员工列表
     * @return 增加好评率的员工列表
     */
    public void handleGoodReviews(List<UserGoodReviewDTO> userGoodReviews,List<UserServiceVO> userServiceVOS,UserServiceDTO dto){
        for (UserServiceVO userServiceVO:userServiceVOS){
            int score=0;
            int num=0;
            for (UserGoodReviewDTO userGoodReview: userGoodReviews) {
                if (Objects.equals(userServiceVO.getUserId(),userGoodReview.getUserId())){
                    score+=userGoodReview.getScore();
                    num++;
                }
            }
            userServiceVO.setNum(num);
            userServiceVO.setScore(score);
        }
        //判断是否需要用好评率进行排序
        if (!Objects.isNull(dto.getCustomerPositiveCommentsRateSort())){
            if (GenConstants.DESC.equals(dto.getCustomerPositiveCommentsRateSort())){
                userServiceVOS.sort(Comparator.comparing(UserServiceVO::getCustomerPositiveCommentsRateTmp,Comparator.reverseOrder()));
            }else {
                userServiceVOS.sort(Comparator.comparing(UserServiceVO::getCustomerPositiveCommentsRateTmp));
            }
        }
    }

    @Override
    @DataScope
    public AjaxResult exportCustomerOverViewOfUser(CustomerOverviewDTO dto) {
        String sheetName = "客户概况报表";
        List<CustomerOverviewVO> list = getCustomerOverViewOfUser(dto);
        // 导出
        list.forEach(CustomerOverviewVO::bindExportData);
        ExcelUtil<CustomerOverviewVO> util = new ExcelUtil<>(CustomerOverviewVO.class);
        return util.exportExcel(list, sheetName);
    }

    @Override
    @DataScope
    public AjaxResult exportCustomerActivityOfDate(CustomerActivityDTO dto) {
        List<CustomerActivityOfDateVO> list = getCustomerActivityOfDate(dto, false);
        // 导出
        List<CustomerActivityOfDateExportVO> exportList = list.stream().map(CustomerActivityOfDateExportVO::new).collect(Collectors.toList());
        ExcelUtil<CustomerActivityOfDateExportVO> util = new ExcelUtil<>(CustomerActivityOfDateExportVO.class);
        return util.exportExcel(exportList, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    @DataScope
    public AjaxResult exportCustomerActivityOfUser(CustomerActivityDTO dto) {
        List<CustomerActivityOfUserVO> list = getCustomerActivityOfUser(dto, false);
        // 导出
        List<CustomerActivityOfUserExportVO> exportList = list.stream().map(CustomerActivityOfUserExportVO::new).collect(Collectors.toList());
        ExcelUtil<CustomerActivityOfUserExportVO> util = new ExcelUtil<>(CustomerActivityOfUserExportVO.class);
        return util.exportExcel(exportList, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    @DataScope
    public AjaxResult exportCustomerActivityOfCustomer(CustomerActivityDTO dto) {
        List<CustomerActivityOfCustomerVO> list = getCustomerActivityOfCustomer(dto, false);
        // 导出
        ExcelUtil<CustomerActivityOfCustomerVO> util = new ExcelUtil<>(CustomerActivityOfCustomerVO.class);
        return util.exportExcel(list, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    @DataScope
    public AjaxResult exportUserServiceOfUser(UserServiceDTO dto) {
        String sheetName = "员工服务报表";
        List<UserServiceVO> list = getUserServiceOfUserMain(dto);
        // 导出
        list.forEach(UserServiceVO::bindExportData);
        ExcelUtil<UserServiceVO> util = new ExcelUtil<>(UserServiceVO.class);
        return util.exportExcel(list, sheetName);
    }

    @Override
    @DataScope
    public List<UserServiceTimeVO> getUserServiceOfTime(UserServiceDTO dto) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        Date start=DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getBeginTime());
        Date end=DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getEndTime());
        List<Date> dateList=DateUtils.findDates(start,end);
        List<UserServiceTimeVO> resultTmpMap=userServiceTimeCombination(dateList);
        List<UserServiceTimeDTO> timeVOList=weUserCustomerMessageStatisticsMapper.getUserServiceOfTime(dto);
        if (CollectionUtils.isEmpty(timeVOList)){
            return resultTmpMap;
        }
        GoodCommitDTO goodCommitDTO=new GoodCommitDTO(dto);
        //单独获取好评的list
        List<UserGoodReviewDTO> reviewDTOS=weFormCustomerFeedbackMapper.selectGoodReviewsForTime(goodCommitDTO);
        //进行数据计算
        List<UserServiceTimeVO> resultMap=handleServiceOfTime(timeVOList,reviewDTOS,resultTmpMap);
        return sortUserServiceOfTime(resultMap,dto);
    }

    /**
     * 统计每天的员工数据
     *
     * @param timeVOList 获取到所有的数据
     * @param goodReviewDTOS 用户评价数据
     * @param resultMap 拼接时间后的列表
     * @return 统计计算完成的员工数据list
     */
    public List<UserServiceTimeVO> handleServiceOfTime(List<UserServiceTimeDTO> timeVOList ,List<UserGoodReviewDTO> goodReviewDTOS,List<UserServiceTimeVO> resultMap){
        for (UserServiceTimeVO serviceTimeVO : resultMap) {
            //对所需数据进行记录
            for (UserServiceTimeDTO userServiceTimeVO : timeVOList) {
                //判断时间是否符合
                if (serviceTimeVO.getTime().equals(userServiceTimeVO.getSendTime())) {
                    //当员工没有与客户聊天，也会在数据库记录下这个员工的数据，会标记客户id为-1。
                    //记录聊天总数时，应先判断员工是否与客户聊天，再记录聊天总数
                    if (!EMPTY_CHAT.equals(userServiceTimeVO.getExternalUserid())){
                        //聊天总数统计发送消息数大于0的个数
                        if (userServiceTimeVO.getUserSendMessageCnt()>0){
                            serviceTimeVO.addChatTotal();
                        }
                        //增加发送消息数
                        serviceTimeVO.addSendContactCnt(userServiceTimeVO.getUserSendMessageCnt());
                        //增加有效对话次数
                        serviceTimeVO.addEffctiveCommunicationCustomerCnt(userServiceTimeVO.getThreeRoundsDialogueFlag());
                        //是否为客户先发起对话
                        if (Objects.equals(userServiceTimeVO.getUserActiveDialogue(), ContactSpeakEnum.CUSTOMER.getCode())) {
                            //增加客户主动发起聊天数
                            serviceTimeVO.addCustomerActiveStartContactCnt();
                        }
                        // 首次回复间隔时长大于0，表示为有效的聊天
                        if (userServiceTimeVO.getFirstReplyTimeIntervalAlterReceive() > 0 && ContactSpeakEnum.CUSTOMER.getCode().equals(userServiceTimeVO.getUserActiveDialogue())) {
                            // 增加已回复聊天数
                            serviceTimeVO.addAlreadyReplyCnt();
                            // 增加首次回复间隔时长
                            serviceTimeVO.addFirstReplyTimeIntervalAlterReceive(userServiceTimeVO.getFirstReplyTimeIntervalAlterReceive());
                        }
                        //判断是否为客户先发起的对话，且员工发送数量不为0
                        if (Objects.equals(userServiceTimeVO.getUserActiveDialogue(), ContactSpeakEnum.CUSTOMER.getCode()) && userServiceTimeVO.getUserSendMessageCnt() != 0) {
                            serviceTimeVO.addUserReplyContactCnt();
                        }
                    }
                }
            }
            //对客户评价进行记录
            for (UserGoodReviewDTO userGoodReviewDTO : goodReviewDTOS) {
                //判断时间是否符合
                if (DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,userGoodReviewDTO.getCreateTime()).equals(serviceTimeVO.getTime())) {
                    serviceTimeVO.addNUm();
                    serviceTimeVO.addScore(userGoodReviewDTO.getScore());
                }
            }
            //通过获取的数据计算每天的结果
            serviceTimeVO.calculateResult();
        }
        return resultMap;
    }

    /**
     * 将时间赋值到结果列表中
     *
     * @param dateList 日期列表
     * @return 组合后的list
     */
    public List<UserServiceTimeVO> userServiceTimeCombination (List<Date> dateList){
        List<UserServiceTimeVO> userServiceTimeVOList=new ArrayList<>();
        for (Date date:dateList){
            UserServiceTimeVO userServiceTimeVO=UserServiceTimeVO.builder().time(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,date)).build();
            userServiceTimeVOList.add(userServiceTimeVO);
        }
        return userServiceTimeVOList;
    }

    /**
     * 根据前端条件进行排序
     *
     * @param list 需要排序的员工数据
     * @param dto 所需排序规则
     * @return 排序完成的员工数据
     */
    public List<UserServiceTimeVO> sortUserServiceOfTime(List<UserServiceTimeVO> list,UserServiceDTO dto){
        if (dto.getSortType()!=null&&dto.getSortName()!=null){
            StatisticsEnum.UserServiceSortOfTimeEnums.sortList(dto.getSortName(),list,dto.getSortType());
        }else {
            list.sort(Comparator.comparing(UserServiceTimeVO::getTime).reversed());
        }
        return list;
    }

    @Override
    @DataScope
    public AjaxResult exportUserServiceOfTime(UserServiceDTO dto) {
        List<UserServiceTimeVO> list = getUserServiceOfTime(dto);
        // 导出
        list.forEach(UserServiceTimeVO::bindExportData);
        ExcelUtil<UserServiceTimeVO> util = new ExcelUtil<>(UserServiceTimeVO.class);
        return util.exportExcel(list, SHEET_NAME);
    }

    /**
     * 是否有数据权限
     * 如果只选定部门不选择员工 但是部门下没有员工则直接返回空列表
     *
     * @param dto   {@link StatisticsDTO}
     * @return true 继续查询 false:停止查询返回空list
     */
    private boolean isHaveDataScope(StatisticsDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getDepartmentIds())) {
            return true;
        }
        // 部门idList查询
        List<String> userIdsByDepartmentIds = weUserMapper.listOfUserId(dto.getCorpId(), dto.getDepartmentIds().toArray(new String[]{}));
        if (CollectionUtils.isEmpty(userIdsByDepartmentIds) && CollectionUtils.isEmpty(dto.getUserIds())) {
            return false;
        }
        if (dto.getUserIds() == null) {
            dto.setUserIds(userIdsByDepartmentIds);
        }else {
            dto.getUserIds().addAll(userIdsByDepartmentIds);
        }
        return true;
    }

    /**
     * 是新客户 当天新加的客户
     *
     * @param sendUserMessages              客户发送员工的消息
     * @return
     */
    private boolean isNewCustomer(List<ConversationArchiveVO> sendUserMessages, Date nowDate) {
        Date customerAddTime = getCustomerAddTime(sendUserMessages, false);
        if (customerAddTime == null) {
            return false;
        }
        return DateUtils.isSameDay(customerAddTime, nowDate);
    }

    /**
     * 通过时间建立对话
     *
     * @param weUser                        员工
     * @param userCustomerMessageStatistics {@link WeUserCustomerMessageStatistics}
     * @param sendUserMessages              客户发送员工的消息
     * @param receiveUserMessages           客户收到员工的消息
     */
    private void buildContactAndSet(WeUser weUser, WeUserCustomerMessageStatistics userCustomerMessageStatistics, List<ConversationArchiveVO> sendUserMessages, List<ConversationArchiveVO> receiveUserMessages) {
        if (userCustomerMessageStatistics == null || CollectionUtils.isEmpty(receiveUserMessages)) {
            return;
        }
        if (CollectionUtils.isEmpty(sendUserMessages)) {
            userCustomerMessageStatistics.setUserActiveDialogue(true);
            return;
        }
        sendUserMessages.addAll(receiveUserMessages);
        // 按照消息时间正序
        List<ConversationArchiveVO> collect = sendUserMessages.stream().sorted(Comparator.comparing(ConversationArchiveVO::getMsgTime)).collect(Collectors.toList());
        int firstIndex = 0;
        ContactStatisticsHandle contactStatisticsHandle = new ContactStatisticsHandle();
        contactStatisticsHandle.refresh();
        //判断第一次对话是否为员工发送
        Boolean isUserSpeak = isUserSpeak(collect.get(firstIndex),weUser);
        contactStatisticsHandle.handleFirstSpeak(collect.get(firstIndex), userCustomerMessageStatistics, isUserSpeak);
        collect.remove(firstIndex);
        for (ConversationArchiveVO conversation : collect) {
            // 客户发言
            if (!isUserSpeak(conversation, weUser)) {
                contactStatisticsHandle.customerSpeakHandle(conversation);
            }else if (isUserSpeak(conversation, weUser)){
            // 员工发言
                contactStatisticsHandle.userSpeakHandle(conversation);
            }
            if (contactStatisticsHandle.isEnd()) {
                break;
            }
        }
        contactStatisticsHandle.saveStatisticsResult(userCustomerMessageStatistics, isUserSpeak);
    }

    /**
     * 是否为员工发言
     *
     * @param conversation  {@link ConversationArchiveVO}
     * @param weUser        {@link WeUser}
     */
    private boolean isUserSpeak(ConversationArchiveVO conversation, WeUser weUser){
        if (conversation == null || StringUtils.isBlank(conversation.getFrom()) || weUser == null) {
            return false;
        }
        return conversation.getFrom().equals(weUser.getUserId());
    }


    /**
     * 获取客户添加时间
     *
     * @param records List<ConversationArchiveVO> 客户发送消息记录
     * @return
     */
    private Date getCustomerAddTime(List<ConversationArchiveVO> records, boolean userSendFlag) {
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        int firstIndex = 0;
        String createTimePlaceHolder = "createTime";
        if (Boolean.TRUE.equals(userSendFlag)) {
            //判断获取聊天数据的客户添加时间是否为null
            if (Objects.isNull(records.get(firstIndex))||Objects.isNull(records.get(firstIndex).getToListInfo())||Objects.isNull(records.get(firstIndex).getToListInfo().get(createTimePlaceHolder))){
                return  null;
            }
            return new Date((Long) records.get(firstIndex).getToListInfo().get(createTimePlaceHolder));
        } else {
            //判断获取聊天数据的客户添加时间是否为null
            if (Objects.isNull(records.get(firstIndex))||Objects.isNull(records.get(firstIndex).getFromInfo())||Objects.isNull(records.get(firstIndex).getFromInfo().get(createTimePlaceHolder))){
                return  null;
            }
            return new Date((Long) records.get(firstIndex).getFromInfo().get(createTimePlaceHolder));
        }
    }

}
