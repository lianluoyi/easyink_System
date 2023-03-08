package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.StaffActivateEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.domain.vo.statistics.*;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.WeUserBehaviorDataMapper;
import com.easyink.wecom.mapper.WeUserCustomerMessageStatisticsMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.mapper.form.WeFormCustomerFeedbackMapper;
import com.easyink.wecom.service.WeUserBehaviorDataService;
import com.easyink.wecom.service.WeUserCustomerMessageStatisticsService;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import com.easyink.wecom.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
    /**
     * 导出客户活跃度报表名称
     */
    protected static final String CUSTOMER_ACTIVITY_REPORT_FORMS = "客户活跃度报表";
    @Override
    public void getMessageStatistics(String corpId) {
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
        String yesterday = DateUtils.dateTime(DateUtils.getYesterday(new Date()));
        visibleUser.parallelStream().forEach(weUser -> {
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
                List<ConversationArchiveVO> userMessages = new CopyOnWriteArrayList<>();
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
                WeUserBehaviorData totalContactAndLossCnt = weFlowerCustomerRelMapper.getTotalContactAndLossCnt(userBehaviorData.getUserId(), userBehaviorData.getCorpId(),DateUtils.parseBeginDay(yesterday), DateUtils.parseEndDay(yesterday));
                userBehaviorData.setContactTotalCnt(totalContactAndLossCnt.getContactTotalCnt());
                userBehaviorData.setNewCustomerLossCnt(totalContactAndLossCnt.getNewCustomerLossCnt());
                weUserBehaviorDataService.updateById(userBehaviorData);
                statistics(userMessages, weUser, userBehaviorData);
            } catch (Exception e) {
                log.error("员工数据拉取失败: corpId:{},userId:【{}】,ex:【{}】", weUser.getCorpId(), weUser.getUserId(), ExceptionUtils.getStackTrace(e));
            }
        });
    }

    @Override
    public void statistics(List<ConversationArchiveVO> userMessages, WeUser weUser, WeUserBehaviorData userBehaviorData) {
        if (CollectionUtils.isEmpty(userMessages) || userBehaviorData == null) {
            // TODO 标识该员工 没有和任何人讲话
            String nullSign = "-1";
            WeUserCustomerMessageStatistics weUserCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            weUserCustomerMessageStatistics.setUserId(weUser.getUserId());
            weUserCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            weUserCustomerMessageStatistics.setExternalUserid(nullSign);
            weUserCustomerMessageStatistics.setSendTime(DateUtils.getYesterday(new Date()));
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
        // 统计客户发送消息
        for (Map.Entry<String, List<ConversationArchiveVO>> entry : customerSendMessageMap.entrySet()) {
            String externalUserId = entry.getKey();
            WeUserCustomerMessageStatistics userCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            userCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            userCustomerMessageStatistics.setUserId(weUser.getUserId());
            userCustomerMessageStatistics.setExternalUserid(externalUserId);
            // 员工对该客户发送的消息数
            userCustomerMessageStatistics.setUserSendMessageCnt(receiveUserMessageMap.getOrDefault(externalUserId, new ArrayList<>()).size());
            // 该客户对员工发送的消息数
            userCustomerMessageStatistics.setExternalUserSendMessageCnt(entry.getValue().size());
            // 获取添加客户时间
            userCustomerMessageStatistics.setAddTime(getCustomerAddTime(entry.getValue(), false));
            // 发送消息时间
            userCustomerMessageStatistics.setSendTime(DateUtils.getYesterday(new Date()));
            // 构建聊天记录并统计
            buildContactAndSet(weUser, userCustomerMessageStatistics, entry.getValue(), receiveUserMessageMap.getOrDefault(externalUserId, new ArrayList<>()));
            // 客户是否在30分钟内回复
            if (Boolean.TRUE.equals(userCustomerMessageStatistics.getRepliedWithinThirtyMinCustomerFlag())) {
                thirtyMinReplyCount ++;
            }
            // 是否为新客户 当天新加的客户
            if (isNewCustomer(entry.getValue())) {
                newCustomerStartContactCnt++;
            }
            receiveUserMessageMap.remove(externalUserId);
            userCustomerMessageStatisticsList.add(userCustomerMessageStatistics);
            chatCnt ++;
        }
        // 统计员工发送消息 但 客户未回复消息
        for (Map.Entry<String, List<ConversationArchiveVO>> entry : receiveUserMessageMap.entrySet()) {
            String externalUserId = entry.getKey();
            WeUserCustomerMessageStatistics userCustomerMessageStatistics = new WeUserCustomerMessageStatistics();
            userCustomerMessageStatistics.setCorpId(weUser.getCorpId());
            userCustomerMessageStatistics.setUserId(weUser.getUserId());
            userCustomerMessageStatistics.setExternalUserid(externalUserId);
            // 员工对该客户发送的消息数
            userCustomerMessageStatistics.setUserSendMessageCnt(entry.getValue().size());
            // 获取添加客户时间
            userCustomerMessageStatistics.setAddTime(getCustomerAddTime(entry.getValue(), true));
            // 发送消息时间
            userCustomerMessageStatistics.setSendTime(DateUtils.getYesterday(new Date()));
            //
            buildContactAndSet(weUser, userCustomerMessageStatistics, null, entry.getValue());
            userCustomerMessageStatisticsList.add(userCustomerMessageStatistics);
            chatCnt ++;
        }
        this.saveBatch(userCustomerMessageStatisticsList);
        // 保存用户行为
        saveUserBehaviorDate(userBehaviorData, thirtyMinReplyCount, newCustomerStartContactCnt, chatCnt);
    }

    /**
     * 保存用户行为数据
     *
     * @param userBehaviorData           {@link WeUserBehaviorData}
     * @param thirtyMinReplyCount        当天员工首次给客户发消息，客户在30分钟内回复的客户数
     * @param newCustomerStartContactCnt 当天新增客户中与员工对话过的人数
     * @param chatCnt                    当天会话数-不区分是否为员工主动发起
     */
    private void saveUserBehaviorDate(WeUserBehaviorData userBehaviorData, Integer thirtyMinReplyCount, Integer newCustomerStartContactCnt, Integer chatCnt) {
        if (userBehaviorData == null) {
            return;
        }
        userBehaviorData.setRepliedWithinThirtyMinCustomerCnt(thirtyMinReplyCount);
        userBehaviorData.setNewContactSpeakCnt(newCustomerStartContactCnt);
        userBehaviorData.setAllChatCnt(chatCnt);
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
        return weUserBehaviorDataMapper.getCustomerOverViewOfTotal(dto);
    }

    @Override
    @DataScope
    public List<CustomerOverviewVO> getCustomerOverViewOfUser(CustomerOverviewDTO dto, Boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (dto.getEndTime() == null) {
            dto.setEndTime(DateUtils.parseBeginDay(DateUtils.dateTime(DateUtils.getYesterday(new Date()))));
        }
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        return weUserBehaviorDataMapper.getCustomerOverViewOfUser(dto);
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
        return weUserCustomerMessageStatisticsMapper.getCustomerActivityOfDate(dto);
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
        return weUserCustomerMessageStatisticsMapper.getCustomerActivityOfUser(dto);
    }

    @Override
    @DataScope
    public List<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(CustomerActivityDTO dto, boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        return weUserCustomerMessageStatisticsMapper.getCustomerActivityOfCustomer(dto);
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
    public List<UserServiceVO> getUserServiceOfUser(UserServiceDTO dto, boolean pageFlag) {
        if (!isHaveDataScope(dto)) {
            return new ArrayList<>();
        }
        if (Boolean.TRUE.equals(pageFlag)) {
            startPage(dto);
        }
        return weUserCustomerMessageStatisticsMapper.getUserServiceOfUser(dto);
    }

    @Override
    public AjaxResult exportCustomerOverViewOfUser(CustomerOverviewDTO dto) {
        String sheetName = "客户概况报表";
        List<CustomerOverviewVO> list = getCustomerOverViewOfUser(dto, false);
        // 导出
        list.forEach(CustomerOverviewVO::bindExportData);
        ExcelUtil<CustomerOverviewVO> util = new ExcelUtil<>(CustomerOverviewVO.class);
        return util.exportExcel(list, sheetName);
    }

    @Override
    public AjaxResult exportCustomerActivityOfDate(CustomerActivityDTO dto) {
        List<CustomerActivityOfDateVO> list = getCustomerActivityOfDate(dto, false);
        // 导出
        List<CustomerActivityOfDateExportVO> exportList = list.stream().map(CustomerActivityOfDateExportVO::new).collect(Collectors.toList());
        ExcelUtil<CustomerActivityOfDateExportVO> util = new ExcelUtil<>(CustomerActivityOfDateExportVO.class);
        return util.exportExcel(exportList, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    public AjaxResult exportCustomerActivityOfUser(CustomerActivityDTO dto) {
        List<CustomerActivityOfUserVO> list = getCustomerActivityOfUser(dto, false);
        // 导出
        List<CustomerActivityOfUserExportVO> exportList = list.stream().map(CustomerActivityOfUserExportVO::new).collect(Collectors.toList());
        ExcelUtil<CustomerActivityOfUserExportVO> util = new ExcelUtil<>(CustomerActivityOfUserExportVO.class);
        return util.exportExcel(exportList, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    public AjaxResult exportCustomerActivityOfCustomer(CustomerActivityDTO dto) {
        List<CustomerActivityOfCustomerVO> list = getCustomerActivityOfCustomer(dto, false);
        // 导出
        ExcelUtil<CustomerActivityOfCustomerVO> util = new ExcelUtil<>(CustomerActivityOfCustomerVO.class);
        return util.exportExcel(list, CUSTOMER_ACTIVITY_REPORT_FORMS);
    }

    @Override
    public AjaxResult exportUserServiceOfUser(UserServiceDTO dto) {
        String sheetName = "员工服务报表";
        List<UserServiceVO> list = getUserServiceOfUser(dto, false);
        // 导出
        list.forEach(UserServiceVO::bindExportData);
        ExcelUtil<UserServiceVO> util = new ExcelUtil<>(UserServiceVO.class);
        return util.exportExcel(list, sheetName);
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
    private boolean isNewCustomer(List<ConversationArchiveVO> sendUserMessages) {
        Date customerAddTime = getCustomerAddTime(sendUserMessages, false);
        if (customerAddTime == null) {
            return false;
        }
        return DateUtils.isSameDay(customerAddTime, DateUtils.getYesterday(new Date()));
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
        contactStatisticsHandle.handleFirstSpeak(collect.get(firstIndex), userCustomerMessageStatistics);
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
        contactStatisticsHandle.saveStatisticsResult(userCustomerMessageStatistics);
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
            return new Date((Long) records.get(firstIndex).getToListInfo().get(createTimePlaceHolder));
        } else {
            return new Date((Long) records.get(firstIndex).getFromInfo().get(createTimePlaceHolder));
        }
    }
}
